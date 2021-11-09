package com.example.programrecording.download

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.programrecording.R
import com.example.programrecording.view.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Service downloads a program when requested and updates Activity via binding. If Activity is
 * stopped/unbinds and program is still downloading, the service promotes itself to a
 * foreground service the download isn't interrupted.
 */
@AndroidEntryPoint
class ProgramDownloadService : Service() {

    @Inject
    lateinit var pref: DownloadPrefManager

    private val alarmReceiver by lazy { AlarmReceiver() }

    /*
     * Checks whether the bound activity has really gone away (foreground service with notification
     * created) or simply orientation change (no-op).
     */

    private var configurationChange = false

    private var serviceRunningInForeground = false

    private val localBinder = LocalBinder()

    private lateinit var notificationManager: NotificationManager

    // Called when download status changes.
    private val _downloadStatusFlow = MutableSharedFlow<DownloadStatus>(replay = 0)

    // Create job and scope for download
    private var downloadJob = SupervisorJob()

    private var scope = CoroutineScope(Dispatchers.Main + downloadJob)

    // Used only for local storage of the last known DownloadStatus.
    private var currentStatus: DownloadStatus = DownloadStatus.Downloading(0)

    // Used only for local storage of the DownloadRequest.
    private lateinit var downloadRequest: DownloadRequest

    override fun onCreate() {
        // Need to call super for Hilt
        super.onCreate()
        Log.d(TAG, "onCreate()")

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        registerReceiver(alarmReceiver, IntentFilter())

        setupListener()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand()")


        val startProgramDownloadFromAlarm = intent.getBooleanExtra(
            EXTRA_DOWNLOAD_ALARM, false
        )
        intent.getParcelableExtra<DownloadRequest>(EXTRA_DOWNLOAD_REQUEST)?.let {
            downloadRequest = it
        }

        val cancelProgramDownloadFromNotification = intent.getBooleanExtra(
            EXTRA_CANCEL_PROGRAM_DOWNLOAD_FROM_NOTIFICATION, false
        )

        when {
            startProgramDownloadFromAlarm -> {
                // TODO: 11/8/21 Only start foreground service if activity not in foreground
                startForegroundDownloadService()
                subscribeToDownloadStatusUpdates(downloadRequest)
            }
            cancelProgramDownloadFromNotification -> unsubscribeToDownloadStatusUpdates()
        }

        // Tells the system not to recreate the service after it's been killed.
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind()")

        // MainActivity (client) comes into foreground and binds to service, so the service can
        // become a background services.
        stopForeground(true)
        serviceRunningInForeground = false
        configurationChange = false
        return localBinder
    }

    override fun onRebind(intent: Intent) {
        Log.d(TAG, "onRebind()")

        // MainActivity (client) returns to the foreground and rebinds to service, so the service
        // can become a background services.
        stopForeground(true)
        serviceRunningInForeground = false
        configurationChange = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d(TAG, "onUnbind()")

        // MainActivity (client) leaves foreground, so service needs to become a foreground service.
        // NOTE: If this method is called due to a configuration change in MainActivity,
        // we do nothing.
        scope.launch(Dispatchers.Default) {
            if (!configurationChange && pref.isDownloading.first()) {
                Log.d(TAG, "Start foreground service")
                withContext(Dispatchers.Main) { startForegroundDownloadService() }
            }
        }

        // Ensures onRebind() is called if MainActivity (client) rebinds.
        return true
    }

    private fun startForegroundDownloadService() {
        val notification = generateNotification(currentStatus)
        startForeground(NOTIFICATION_ID, notification)
        serviceRunningInForeground = true
    }

    override fun onDestroy() {
        unregisterReceiver(alarmReceiver)
        super.onDestroy()
        scope.cancel()
        Log.d(TAG, "onDestroy()")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configurationChange = true
    }

    private fun setupListener() {
        scope.launch {
            _downloadStatusFlow.collect { downloadStatus ->
                currentStatus = downloadStatus
                val intent = Intent(ACTION_FOREGROUND_ONLY_DOWNLOAD_BROADCAST)
                intent.putExtra(EXTRA_DOWNLOAD_STATUS, downloadStatus)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

                // Updates notification content if this service is running as a foreground service.
                if (serviceRunningInForeground) notificationManager.notify(
                    NOTIFICATION_ID, generateNotification(downloadStatus)
                )
            }
        }
    }

    fun subscribeToDownloadStatusUpdates(downloadRequest: DownloadRequest) {
        Log.d(TAG, "subscribeToDownloadUpdates()")
        this.downloadRequest = downloadRequest


        // Binding to this service doesn't actually trigger onStartCommand(). That is needed to
        // ensure this Service can be promoted to a foreground service, i.e., the service needs to
        // be officially started (which we do here).

        startService(Intent(applicationContext, ProgramDownloadService::class.java))
        if (downloadJob.isCancelled) {
            downloadJob = SupervisorJob()
            scope = CoroutineScope(Dispatchers.Main + downloadJob)
            setupListener()
        }
        scope.launch {
            this@ProgramDownloadService.pref.setDownloading(true)
            try {
                var downloadedPercentage = 0
                updateStatus(DownloadStatus.Downloading(0))

                while (downloadedPercentage < 100) {
                    delay(2000)
                    ++downloadedPercentage
                    updateStatus(DownloadStatus.Downloading(downloadedPercentage))
                }

                unsubscribeToDownloadStatusUpdates(true)
            } catch (ex: Exception) {
                val msg = "Something went wrong. $ex"
                Log.e(TAG, msg)
            }
        }

    }

    fun unsubscribeToDownloadStatusUpdates(isFinished: Boolean = false) {
        Log.d(TAG, "unsubscribeToDownloadStatusUpdates()")
        scope.launch {
            try {
                val status = if (isFinished) DownloadStatus.Success else DownloadStatus.Canceled
                updateStatus(status)
                this@ProgramDownloadService.pref.setDownloading(false)
                stopSelf()
                scope.cancel()
            } catch (ex: Exception) {
                updateStatus(DownloadStatus.Failed("Failed to cancel download."))
            }
        }
    }

    private fun updateStatus(status: DownloadStatus) {
        scope.launch { _downloadStatusFlow.emit(status) }
    }

    /*
     * Generates a BIG_TEXT_STYLE Notification that represent latest DownloadStatus.
     */
    private fun generateNotification(dlStatus: DownloadStatus): Notification {
        Log.d(TAG, "generateNotification()")

        // Main steps for building a BIG_TEXT_STYLE notification:
        //      0. Get data
        //      1. Create Notification Channel for O+
        //      2. Build the BIG_TEXT_STYLE
        //      3. Set up Intent / Pending Intent for notification
        //      4. Build and issue the notification

        // 0. Get data
        val titleText = String.format("Downloading %s", downloadRequest.title)
        val mainNotificationText = when (dlStatus) {
            is DownloadStatus.Success -> "Download Complete"
            is DownloadStatus.Downloading -> "${dlStatus.progress} / 100"
            is DownloadStatus.Failed -> dlStatus.message
            is DownloadStatus.Canceled -> "Download Canceled"
        }

        // 1. Create Notification Channel for O+ and beyond devices (26+).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID, titleText, NotificationManager.IMPORTANCE_DEFAULT
            )

            // Adds NotificationChannel to system. Attempting to create an
            // existing notification channel with its original values performs
            // no operation, so it's safe to perform the below sequence.
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // 2. Build the BIG_TEXT_STYLE.
        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(mainNotificationText)
            .setBigContentTitle(titleText)

        // 3. Set up main Intent/Pending Intents for notification.
        val launchActivityIntent = Intent(this, MainActivity::class.java)

        val cancelIntent = Intent(this, ProgramDownloadService::class.java)
        cancelIntent.putExtra(EXTRA_CANCEL_PROGRAM_DOWNLOAD_FROM_NOTIFICATION, true)

        val servicePendingIntent = PendingIntent.getService(
            this, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val activityPendingIntent = PendingIntent.getActivity(
            this, 0, launchActivityIntent, 0
        )

        // 4. Build and issue the notification.
        // Notification Channel Id is ignored for Android pre O (26).
        val notificationCompatBuilder =
            NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)

        return notificationCompatBuilder
            .setStyle(bigTextStyle)
            .setContentTitle(titleText)
            .setContentText(mainNotificationText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(
                R.drawable.ic_launch, getString(R.string.open_app),
                activityPendingIntent
            )
            .addAction(
                R.drawable.ic_cancel,
                getString(R.string.cancel_download),
                servicePendingIntent
            )
            .build()
    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        internal val service: ProgramDownloadService
            get() = this@ProgramDownloadService
    }

    companion object {
        const val TAG = "ProgramDownloadService"

        private const val PACKAGE_NAME = "com.example.android.programdownload"

        internal const val ACTION_FOREGROUND_ONLY_DOWNLOAD_BROADCAST =
            "$PACKAGE_NAME.action.FOREGROUND_ONLY_DOWNLOAD_BROADCAST"

        internal const val EXTRA_DOWNLOAD_STATUS = "$PACKAGE_NAME.extra.DOWNLOAD_STATUS"
        internal const val EXTRA_DOWNLOAD_REQUEST = "$PACKAGE_NAME.extra.DOWNLOAD_REQUEST"
        internal const val EXTRA_DOWNLOAD_ALARM =
            "$PACKAGE_NAME.extra.START_PROGRAM_DOWNLOAD_FROM_ALARM"

        private const val EXTRA_CANCEL_PROGRAM_DOWNLOAD_FROM_NOTIFICATION =
            "$PACKAGE_NAME.extra.CANCEL_PROGRAM_DOWNLOAD_FROM_NOTIFICATION"

        private const val NOTIFICATION_ID = 12345678

        private const val NOTIFICATION_CHANNEL_ID = "program_download_channel_01"
    }
}

package com.example.programrecording.view

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.programrecording.R
import com.example.programrecording.databinding.ActivityMainBinding
import com.example.programrecording.download.DownloadStatus
import com.example.programrecording.download.ProgramDownloadService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

    private lateinit var navController: NavController

    private var foregroundOnlyLocationServiceBound = false

    // Downloads program and provides updates.
    private var programDownloadService: ProgramDownloadService? = null

    // Listens for download status broadcasts from ProgramDownloadService.
    private lateinit var foregroundOnlyBroadcastReceiver: ForegroundOnlyBroadcastReceiver

    // Monitors connection to the program download service.
    private val foregroundOnlyServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as ProgramDownloadService.LocalBinder
            programDownloadService = binder.service
            foregroundOnlyLocationServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            programDownloadService = null
            foregroundOnlyLocationServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        foregroundOnlyBroadcastReceiver = ForegroundOnlyBroadcastReceiver()
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)
    }

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(this, ProgramDownloadService::class.java)
        bindService(serviceIntent, foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            foregroundOnlyBroadcastReceiver,
            IntentFilter(ProgramDownloadService.ACTION_FOREGROUND_ONLY_DOWNLOAD_BROADCAST)
        )
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
            foregroundOnlyBroadcastReceiver
        )
        super.onPause()
    }

    override fun onStop() {
        if (foregroundOnlyLocationServiceBound) {
            unbindService(foregroundOnlyServiceConnection)
            foregroundOnlyLocationServiceBound = false
        }
        super.onStop()
    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp() || navController.navigateUp()
    }

    /**
     * Receiver for program download broadcasts from [ProgramDownloadService].
     */
    private inner class ForegroundOnlyBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val status = intent.getSerializableExtra(ProgramDownloadService.EXTRA_DOWNLOAD_STATUS)
            if (status != null && status is DownloadStatus) {
                val msg = when (status) {
                    is DownloadStatus.Downloading -> "${status.progress}"
                    is DownloadStatus.Success -> "Download Complete"
                    is DownloadStatus.Failed -> "Error: ${status.message}"
                    is DownloadStatus.Canceled -> "Download Canceled"
                }
                Log.d(TAG, "onReceive: Program Download: $msg")
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
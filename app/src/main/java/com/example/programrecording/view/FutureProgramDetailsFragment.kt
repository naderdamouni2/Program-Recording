package com.example.programrecording.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.programrecording.databinding.FragmentFutureProgramDetailsBinding
import com.example.programrecording.download.AlarmReceiver.Companion.setupProgramDownloadAlarm
import com.example.programrecording.download.DownloadRequest

class FutureProgramDetailsFragment : Fragment() {

    private var _binding: FragmentFutureProgramDetailsBinding? = null
    private val binding get() = _binding!!
    private val args: FutureProgramDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentFutureProgramDetailsBinding.inflate(layoutInflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = args.details.title
        handleFutureDetails()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleFutureDetails() = with(binding) {
        programPoster.setImageResource(args.details.programPoster)
        "Duration: ${args.details.duration} min".also { duration.text = it }
        "Start Time: ${args.details.startTime}".also { startTime.text = it }
        recordBtn.setOnClickListener {
            val downloadRequest = DownloadRequest(id = args.details.id, title = args.details.title)
            // TODO: 11/8/21 update logic to handle multiple downloads and resolve conflicts
            // Will start download in 30 secs
            val timeLeftTillProgramStart = (30 * 1000).toLong()
            it.context.setupProgramDownloadAlarm(downloadRequest, timeLeftTillProgramStart)
            it.isEnabled = false
            Toast.makeText(context, "Starting Download", Toast.LENGTH_LONG).show()
        }
    }
}
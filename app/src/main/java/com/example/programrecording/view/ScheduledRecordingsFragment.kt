package com.example.programrecording.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.programrecording.R
import com.example.programrecording.databinding.FragmentScheduledRecordingsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ScheduledRecordingsFragment : Fragment(R.layout.fragment_scheduled_recordings) {

    private var _binding: FragmentScheduledRecordingsBinding? = null
    private val binding get() = _binding!!
    private val args: ScheduledRecordingsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentScheduledRecordingsBinding.inflate(layoutInflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Scheduled Recordings"

        loadScheduledRecordings()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadScheduledRecordings() = with(binding) {
        val currentDate = Date()
        val formattedTime = SimpleDateFormat("ha", Locale.US).format(currentDate)
        args.details.startTime = formattedTime

        title.text = args.details.title
        args.details.programPoster.let { programPoster.setImageResource(it) }
        "Duration: ${args.details.duration} min".also { duration.text = it }
        "Start Time: ${args.details.startTime}".also { startTime.text = it }
    }

    companion object {
        private const val TAG = "SCHEDULED_RECORDINGS_FRAGMENT"
    }
}
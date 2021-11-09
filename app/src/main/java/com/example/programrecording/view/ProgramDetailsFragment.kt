package com.example.programrecording.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.programrecording.adapter.FutureProgramsAdapter
import com.example.programrecording.databinding.FragmentProgramDetailsBinding
import com.example.programrecording.model.FutureProgramMock
import com.example.programrecording.utils.ApiState
import com.example.programrecording.viewModel.ProgramViewModel

class ProgramDetailsFragment : Fragment() {

    private var _binding: FragmentProgramDetailsBinding? = null
    private val binding get() = _binding!!
    private val args: ProgramDetailsFragmentArgs by navArgs()
    private val programViewModel by activityViewModels<ProgramViewModel>()
    private val networkAdapter by lazy { FutureProgramsAdapter(listener = ::handleRecording) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentProgramDetailsBinding.inflate(layoutInflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.title = "${args.details.network} Network"
        loadProgramDetails()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() = with(programViewModel) {
        getFutureProgramListMock()
        futureProgramListMock.observe(viewLifecycleOwner) { state ->
            binding.pbLoading.isVisible = state is ApiState.Loading
            if (state is ApiState.Success) loadFuturePrograms(state.data)
            if (state is ApiState.Failure) handleFailure(state.errorMsg)
        }
    }

    private fun loadProgramDetails() = with(binding) {
        programPoster.setImageResource(args.details.programPoster)
        duration.text = String.format("Duration: %s min", args.details.duration)
        startTime.text = String.format("Start Time: %s", args.details.startTime)
    }

    private fun loadFuturePrograms(program: List<FutureProgramMock>) =
        with(binding.rvNetworkPrograms) {
            if (adapter == null) adapter = networkAdapter
            networkAdapter.updateList(program)
        }

    private fun handleRecording(program: FutureProgramMock) {
        val action = ProgramDetailsFragmentDirections.programDetailToFutureProgramDetails(program)
        findNavController().navigate(action)
    }

    private fun handleFailure(error: String) {
        Log.e(TAG, "ERROR LOADING FUTURE PROGRAMS --> $error")
    }

    companion object {
        private const val TAG = "PROGRAM_DETAILS_FRAGMENT"
    }
}
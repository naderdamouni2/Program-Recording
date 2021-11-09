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
import com.example.programrecording.R
import com.example.programrecording.adapter.ProgramsAdapter
import com.example.programrecording.databinding.FragmentProgramsListBinding
import com.example.programrecording.model.ProgramMock
import com.example.programrecording.utils.ApiState
import com.example.programrecording.viewModel.ProgramViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProgramListFragment : Fragment() {

    private var _binding: FragmentProgramsListBinding? = null
    private val binding get() = _binding!!
    private val programViewModel by activityViewModels<ProgramViewModel>()
    private val programAdapter by lazy { ProgramsAdapter(listener = ::getProgramDetails) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentProgramsListBinding.inflate(layoutInflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.title = "Live TV"
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() = with(programViewModel) {
        programListMock.observe(viewLifecycleOwner) { state ->
            binding.pbLoading.isVisible = state is ApiState.Loading
            if (state is ApiState.Success) loadPrograms(state.data)
            if (state is ApiState.Failure) handleFailure(state.errorMsg)
        }
    }

    private fun loadPrograms(programs: List<ProgramMock>) = with(binding.rvList) {
        if (adapter == null) adapter = programAdapter
        programAdapter.updateProgramList(programs)
    }

    private fun getProgramDetails(program: ProgramMock) {
        val action = ProgramListFragmentDirections.programListToProgramDetails(program)
        findNavController().navigate(action)
    }

    private fun handleFailure(error: String) {
        Log.e(TAG, "ERROR LOADING PROGRAMS --> $error")
    }

    companion object {
        private const val TAG = "PROGRAM_LIST_FRAGMENT"
    }
}
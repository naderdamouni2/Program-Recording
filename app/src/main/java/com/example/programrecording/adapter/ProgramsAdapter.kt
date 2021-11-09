package com.example.programrecording.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.programrecording.databinding.ListItemBinding
import com.example.programrecording.model.ProgramMock
import com.example.programrecording.model.programList

class ProgramsAdapter(
    private val listener: (ProgramMock) -> Unit
) : RecyclerView.Adapter<ProgramsAdapter.ProgramViewHolder>() {

    private val programList: MutableList<ProgramMock> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) = ProgramViewHolder.getInstance(parent, listener)

    override fun onBindViewHolder(holder: ProgramViewHolder, position: Int) {
        holder.loadPrograms(programList[position])
    }

    override fun getItemCount() = programList.size

    fun updateProgramList(programs: List<ProgramMock>) {
        val positionStart = programList.size
        programList.addAll(programs)
        notifyItemRangeInserted(0, positionStart)
    }

    class ProgramViewHolder(
        private val binding: ListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun loadPrograms(program: ProgramMock) = with(binding) {
            title.text = program.title
            programImage.setImageResource(program.channelPoster)
        }

        companion object {
            fun getInstance(parent: ViewGroup, listener: (ProgramMock) -> Unit): ProgramViewHolder {
                val binding = ListItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return ProgramViewHolder(binding).also {
                    listener(programList[it.adapterPosition])
                }
            }
        }
    }
}
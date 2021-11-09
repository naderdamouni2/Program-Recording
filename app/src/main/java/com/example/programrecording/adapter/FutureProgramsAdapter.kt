package com.example.programrecording.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.programrecording.databinding.ListFutureItemsBinding
import com.example.programrecording.model.FutureProgramMock
import com.example.programrecording.model.futureProgramsList

class FutureProgramsAdapter(
    private val futureProgramList: MutableList<FutureProgramMock> = mutableListOf(),
    private val listener: (FutureProgramMock) -> Unit
) : RecyclerView.Adapter<FutureProgramsAdapter.FutureProgramsViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) = FutureProgramsViewHolder.getInstance(parent, listener)

    override fun onBindViewHolder(holder: FutureProgramsViewHolder, position: Int) {
        holder.loadFuturePrograms(futureProgramList[position])
    }

    override fun getItemCount() = futureProgramList.size

    fun updateList(programs: List<FutureProgramMock>) {
        val startPosition = futureProgramList.size
        futureProgramList.addAll(programs)
        notifyItemRangeInserted(0, startPosition)
    }

    class FutureProgramsViewHolder(
        private val binding: ListFutureItemsBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun loadFuturePrograms(program: FutureProgramMock) = with(binding) {
            title.text = program.title
            timeSlot.text = program.startTime
        }

        companion object {
            fun getInstance(
                parent: ViewGroup,
                listener: (FutureProgramMock) -> Unit
            ): FutureProgramsViewHolder {
                val binding = ListFutureItemsBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return FutureProgramsViewHolder(binding).also { holder ->
                    holder.itemView.setOnClickListener {
                        listener(futureProgramsList[holder.adapterPosition])
                    }
                }
            }
        }
    }
}
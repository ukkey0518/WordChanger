package com.ukiuki.wordchanger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

class DisplayListAdapter(
    private val data: MutableList<WordRowData>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<DisplayListAdapter.MyViewHolder>() {




    class MyViewHolder(view: View, private val listener: OnItemClickListener) :
        RecyclerView.ViewHolder(view.rootView), View.OnClickListener {

        val tvInput: TextView = view.findViewById(R.id.row_tvInput)
        val tvOutput: TextView = view.findViewById(R.id.row_tvOutput)

        val icRemove: ImageView = view.findViewById(R.id.icRemove)
        val icNewline: ImageView = view.findViewById(R.id.icNewline)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.onClick(adapterPosition)
        }
    }




    interface OnItemClickListener {
        fun onClick(position: Int)
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.word_list_row, parent, false)
        return MyViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = data[position]
        holder.tvInput.text = item.tvInput
        holder.tvOutput.text = item.tvOutput

        val outputWord = item.tvOutput.replace("\n", "")
        holder.icRemove.isVisible = outputWord.isEmpty()
        holder.icNewline.isVisible = item.tvOutput.contains("\n")
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
package com.example.oderbook_gfrias.presentation.recyvlerViewAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.oderbook_gfrias.databinding.RowLayoutBinding

class MainRecyclerViewAdapter(con: Context, private val arrayList: Array<Pair<Float, Float>>, private val isBid: Boolean,
                              val itemClick: (Float) -> Unit) : RecyclerView.Adapter<MainRecyclerViewAdapter.BidsViewHolder>() {

    private var marketData = arrayList
    private lateinit var currentItem: Pair<Float, Float>

    inner class BidsViewHolder(private val binding: RowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Pair<Float, Float>){
            with (binding){
                tvPrice.text = data.toList()[0].toString()
                tvSize.text = data.toList()[1].toString()
                isBuy = isBid

                this.viewModel = viewModel
                executePendingBindings()

                rowLayout.setOnClickListener(){
                    itemClick(data.toList()[0])
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BidsViewHolder {
        val itemBinding = RowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BidsViewHolder(itemBinding)    }

    override fun onBindViewHolder(holder: BidsViewHolder, position: Int) {
        currentItem = marketData[position]

        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

}
package com.carcollector.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.carcollector.databinding.ItemLotBinding
import com.carcollector.model.LotItem

class LotAdapter(
    private val onClick: (LotItem) -> Unit
) : RecyclerView.Adapter<LotAdapter.LotViewHolder>() {

    private val items = mutableListOf<LotItem>()

    fun submitList(data: List<LotItem>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LotViewHolder {
        val binding = ItemLotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LotViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class LotViewHolder(
        private val binding: ItemLotBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LotItem) {
            binding.titleText.text = item.title
            binding.urlText.text = item.url
            binding.root.setOnClickListener { onClick(item) }
        }
    }
}

package com.example.review.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.review.databinding.ItemReviewImageBinding

class ReviewImageAdapter(
    private val urls: List<String>
) : RecyclerView.Adapter<ReviewImageAdapter.VH>() {

    inner class VH(val binding: ItemReviewImageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemReviewImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun getItemCount() = urls.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val url = urls[position]
        Glide.with(holder.itemView)
            .load(url)
            .centerCrop()
            .into(holder.binding.imageThumb)
    }
}
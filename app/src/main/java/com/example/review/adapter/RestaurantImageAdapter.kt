package com.example.review.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.review.databinding.ItemRestaurantImageBinding

class RestaurantImageAdapter(
    private var imageUrls: List<String>
) : RecyclerView.Adapter<RestaurantImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemRestaurantImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageUrls[position])
    }

    override fun getItemCount(): Int = imageUrls.size

    // API 호출 후 데이터를 업데이트하기 위한 함수
    fun updateUrls(newUrls: List<String>) {
        imageUrls = newUrls
        notifyDataSetChanged()
    }

    class ImageViewHolder(private val binding: ItemRestaurantImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(url: String) {
            Glide.with(binding.root)
                .load(url)
                .transform(CenterCrop(), RoundedCorners(16)) // 둥근 모서리 적용
                .into(binding.restaurantImageIv)
        }
    }
}
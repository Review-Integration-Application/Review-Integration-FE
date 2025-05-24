package com.example.review.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.review.databinding.ItemRestaurantDetailNaverBinding
import com.example.review.dataclass.Naver_review
import com.bumptech.glide.Glide

class NaverReviewRVAdapter(private val naverReviewList: ArrayList<Naver_review>): RecyclerView.Adapter<NaverReviewRVAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NaverReviewRVAdapter.ViewHolder {
        val binding: ItemRestaurantDetailNaverBinding = ItemRestaurantDetailNaverBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NaverReviewRVAdapter.ViewHolder, position: Int) {
        holder.bind(naverReviewList[position])
    }

    override fun getItemCount(): Int = naverReviewList.size

    inner class ViewHolder(val binding: ItemRestaurantDetailNaverBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(naver_review: Naver_review){
            // ★ 별점 텍스트 설정
            val fullStars = "★".repeat(naver_review.rating.toInt())
            val emptyStars = "☆".repeat(5 - naver_review.rating.toInt())
            binding.starTextView.text = fullStars + emptyStars

            binding.textDate.text = naver_review.date
            binding.textReview.text = naver_review.content

            // 이미지 처리
            val imageUrls = naver_review.imageUrls
            if (imageUrls.isNotEmpty()){ // 이미지가 비어있지 않으면
                binding.imageContainer.visibility = View.VISIBLE

                Glide.with(binding.image1.context)
                    .load(imageUrls[0])
                    .into(binding.image1)

                if (imageUrls.size > 1) {
                    binding.image2.visibility = View.VISIBLE
                    Glide.with(binding.image2.context)
                        .load(imageUrls[1])
                        .into(binding.image2)
                } else {
                    binding.image2.visibility = View.GONE
                }
            } else {
                binding.imageContainer.visibility = View.GONE
            }
        }
    }
}
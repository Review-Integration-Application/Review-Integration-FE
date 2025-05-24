package com.example.review.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.review.databinding.ItemRestaurantDetailKakaoBinding
import com.example.review.dataclass.Kakao_review

class KakaoReviewRVAdapter(private val kakaoReviewList: ArrayList<Kakao_review>): RecyclerView.Adapter<KakaoReviewRVAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KakaoReviewRVAdapter.ViewHolder {
        val binding: ItemRestaurantDetailKakaoBinding = ItemRestaurantDetailKakaoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: KakaoReviewRVAdapter.ViewHolder, position: Int) {
        holder.bind(kakaoReviewList[position])
    }

    override fun getItemCount(): Int = kakaoReviewList.size

    inner class ViewHolder(val binding: ItemRestaurantDetailKakaoBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(kakao_review: Kakao_review){
            // ★ 별점 텍스트 설정
            val fullStars = "★".repeat(kakao_review.rating.toInt())
            val emptyStars = "☆".repeat(5 - kakao_review.rating.toInt())
            binding.starTextView.text = fullStars + emptyStars

            binding.textDate.text = kakao_review.date
            binding.textReview.text = kakao_review.content

            // 이미지 처리
            val imageUrls = kakao_review.imageUrls
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
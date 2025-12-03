package com.example.review.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.review.databinding.ItemRestaurantDetailKakaoBinding
import com.example.review.dataclass.Kakao_review

class KakaoReviewRVAdapter(
    private val kakaoReviewList: MutableList<Kakao_review>
): RecyclerView.Adapter<KakaoReviewRVAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemRestaurantDetailKakaoBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Kakao_review) {
            binding.textDate.text = item.date
            binding.textReview.text = item.review_content

            val urls = item.image_url
            val rv = binding.imageList

            if (urls.isEmpty()) {
                rv.visibility = View.GONE
            } else {
                rv.visibility = View.VISIBLE
                if (rv.layoutManager == null) {
                    rv.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                }
                rv.adapter = ReviewImageAdapter(urls)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val b = ItemRestaurantDetailKakaoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(b)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(kakaoReviewList[position])
    }

    override fun getItemCount(): Int = kakaoReviewList.size

    fun submit(list: List<Kakao_review>) {
        kakaoReviewList.clear()
        kakaoReviewList.addAll(list)
        notifyDataSetChanged()
    }
}
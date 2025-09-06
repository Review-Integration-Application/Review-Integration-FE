package com.example.review.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.review.databinding.ItemRestaurantDetailNaverBinding
import com.example.review.dataclass.Naver_review
import com.bumptech.glide.Glide

class NaverReviewRVAdapter(
    private val naverReviewList: MutableList<Naver_review>   // ★ ArrayList -> MutableList 로
) : RecyclerView.Adapter<NaverReviewRVAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemRestaurantDetailNaverBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Naver_review) {
            binding.textDate.text = item.date
            binding.textReview.text = item.content

            val urls = item.imageUrls
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
        val b = ItemRestaurantDetailNaverBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(b)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(naverReviewList[position])
    }

    override fun getItemCount(): Int = naverReviewList.size

    fun submit(list: List<Naver_review>) {
        naverReviewList.clear()
        naverReviewList.addAll(list)
        notifyDataSetChanged()
    }
}
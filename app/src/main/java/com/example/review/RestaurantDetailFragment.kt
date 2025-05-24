package com.example.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.review.databinding.FragmentRestaurantDetailBinding

class RestaurantDetailFragment: Fragment() {

    lateinit var binding: FragmentRestaurantDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRestaurantDetailBinding.inflate(inflater, container, false)

        // 네이버 리뷰 버튼 클릭 시
        binding.naverReviewBt.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RestaurantDetailNaverFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        // 카카오 리뷰 버튼 클릭 시
        binding.kakaoReviewBt.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RestaurantDetailKakaoFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        return binding.root
    }
}
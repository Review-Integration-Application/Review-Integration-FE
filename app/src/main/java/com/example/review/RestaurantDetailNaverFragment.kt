package com.example.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.review.adapter.NaverReviewRVAdapter
import com.example.review.databinding.FragmentRestaurantDetailNaverBinding
import com.example.review.dataclass.Naver_review

class RestaurantDetailNaverFragment: Fragment() {
    lateinit var binding: FragmentRestaurantDetailNaverBinding
    private var naverReviewDatas = ArrayList<Naver_review>()
    private lateinit var naverReviewRVAdapter: NaverReviewRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRestaurantDetailNaverBinding.inflate(inflater, container, false)

        // 더미 데이터 (API 받기 전)
        naverReviewDatas.apply {
            add(Naver_review(5f, "2025.02.12", "정말 맛있어요!", listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg")))
            add(Naver_review(3f, "2025.01.30", "그냥 그랬어요.", emptyList()))
            add(Naver_review(2f, "2025.01.03", "별로였어요.", emptyList()))
        }

        // 어댑터와 더미데이터 연결
        naverReviewRVAdapter = NaverReviewRVAdapter(naverReviewDatas)

        // 리사이클러뷰에 어댑터 연결
        binding.naverContentVp.adapter = naverReviewRVAdapter

        // 레이아웃 매니저 설정
        binding.naverContentVp.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        return binding.root
    }
}
package com.example.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.review.adapter.KakaoReviewRVAdapter
import com.example.review.databinding.FragmentRestaurantDetailKakaoBinding
import com.example.review.dataclass.Kakao_review

class RestaurantDetailKakaoFragment: Fragment() {
    lateinit var binding: FragmentRestaurantDetailKakaoBinding
    private var kakaoReviewDatas = ArrayList<Kakao_review>()
    private lateinit var kakaoReviewRVAdapter: KakaoReviewRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRestaurantDetailKakaoBinding.inflate(inflater, container, false)

        // 더미 데이터 (API 받기 전)
        kakaoReviewDatas.apply {
            add(Kakao_review(5f, "2025.02.12", "정말 맛있어요!", listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg")))
            add(Kakao_review(3f, "2025.01.30", "그냥 그랬어요.", emptyList()))
            add(Kakao_review(2f, "2025.01.03", "별로였어요.", emptyList()))
        }

        // 어댑터와 더미데이터 연결
        kakaoReviewRVAdapter = KakaoReviewRVAdapter(kakaoReviewDatas)

        // 리사이클러뷰에 어댑터 연결
        binding.kakaoContentVp.adapter = kakaoReviewRVAdapter

        // 레이아웃 매니저 설정
        binding.kakaoContentVp.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        return binding.root
    }
}
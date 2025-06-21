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
            add(Naver_review(5f, "2025.02.12", "정말 맛있어요!", R.drawable.img1, R.drawable.img2))
            add(Naver_review(3f, "2025.01.30", "그냥 그랬어요."))
            add(Naver_review(2f, "2025.01.03", "별로였어요."))
            add(Naver_review(4f, "2025.01.08", "맛있게 잘 먹었습니다."))
            add(Naver_review(5f, "2025.03.03", "예약없이 바로 들어갔어요! 잘 먹었습니다 :)"))
            add(Naver_review(5f, "2025.03.12", "종로구 최고 맛집!"))
            add(Naver_review(4f, "2025.03.16", "주말에는 웨이팅이 조금 있어요! 맛은 아주 맛있습니당"))
            add(Naver_review(5f, "2025.03.16", "주말이라 한 20분정도 웨이팅 있었습니다! 맛있네요!"))
            add(Naver_review(3f, "2025.04.03", "살짝 간이 싱거웠어요 ㅜㅜ"))
            add(Naver_review(5f, "2025.04.12", "인테이러가 너무 이뻐요!"))
            add(Naver_review(4f, "2025.05.01", "종종 먹으러 갈 것 같아요! 좋아요!"))
            add(Naver_review(5f, "2025.05.03", "맛집 인정!"))
            add(Naver_review(5f, "2025.05.13", "너무 맛있어요~"))
            add(Naver_review(5f, "2025.05.14", "저번에 먹었는데 맛있어서 또 왔습니다~ 잘 먹었어요!"))
            add(Naver_review(4f, "2025.05.20", "굿! 배부르게 잘 먹었습니다"))
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
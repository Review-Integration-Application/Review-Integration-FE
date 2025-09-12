package com.example.review

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.review.RestaurantDetailNaverFragment.Companion
import com.example.review.adapter.KakaoReviewRVAdapter
import com.example.review.api.Response.KakaoReviewResponse
import com.example.review.api.Response.NaverReviewResponse
import com.example.review.api.RestoreItf
import com.example.review.api.RetrofitBaseObj
import com.example.review.databinding.FragmentRestaurantDetailKakaoBinding
import com.example.review.dataclass.Kakao_review
import com.example.review.dataclass.Naver_review
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private fun KakaoReviewResponse.toUi(): Kakao_review {
    val urls = (image_url ?: "")
        .split(",", ";")
        .map { it.trim() }
        .filter { it.isNotEmpty() && it.lowercase() != "none" }

    return Kakao_review(
        date = date ?: "",
        review_content = review_content ?: "",
        image_url = urls
    )
}

class RestaurantDetailKakaoFragment: Fragment() {

    companion object {
        private const val ARG_ID = "store_id"

        fun newInstance(storeId: Int): RestaurantDetailKakaoFragment {
            val f = RestaurantDetailKakaoFragment()
            f.arguments = Bundle().apply { putInt(ARG_ID, storeId) }
            return f
        }
    }

    private var _binding: FragmentRestaurantDetailKakaoBinding? = null
    private val binding get() = _binding!!

    private lateinit var kakaoReviewRVAdapter: KakaoReviewRVAdapter
    private var items = mutableListOf<Kakao_review>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRestaurantDetailKakaoBinding.inflate(inflater, container, false)

        // 더미 데이터 (API 받기 전)
//        kakaoReviewDatas.apply {
//            add(Kakao_review(5f, "2025.02.12", "정말 맛있어요!", R.drawable.img1, R.drawable.img2))
//            add(Kakao_review(3f, "2025.01.30", "그냥 그랬어요."))
//            add(Kakao_review(2f, "2025.01.03", "별로였어요."))
//            add(Kakao_review(4f, "2025.01.08", "맛있게 잘 먹었습니다."))
//            add(Kakao_review(5f, "2025.03.03", "예약없이 바로 들어갔어요! 잘 먹었습니다 :)"))
//
//        }

        // 어댑터와 더미데이터 연결
        kakaoReviewRVAdapter = KakaoReviewRVAdapter(items)

        // 리사이클러뷰에 어댑터 연결
        //binding.kakaoContentVp.adapter = kakaoReviewRVAdapter

        // 레이아웃 매니저 설정
        //binding.kakaoContentVp.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.kakaoContentVp.apply {
            adapter = kakaoReviewRVAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        binding.backButtonIv.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        val storeId = arguments?.getInt(ARG_ID) ?: -1
        if (storeId != -1) fetchKakaoReviews(storeId)

        return binding.root
    }

    private fun fetchKakaoReviews(storeId: Int) {
        val svc = RetrofitBaseObj.getRetrofit().create(RestoreItf::class.java)

        // ★ 서버가 "리스트" 반환할 때
        svc.getKakaoReview(storeId).enqueue(object: Callback<List<KakaoReviewResponse>> {
            override fun onResponse(
                call: Call<List<KakaoReviewResponse>>,
                response: Response<List<KakaoReviewResponse>>
            ) {
                if (!isAdded) return
                if (!response.isSuccessful) {
                    Log.e("카카오 리뷰 API 응답 실패", "HTTP ${response.code()} ${response.errorBody()?.string()}")
                    return
                }
                Log.d("카카오 리뷰 API 조회 성공", "성공성공")
                val body = response.body().orEmpty()
                val mapped = body.map { it.toUi() }
                items.clear()
                items.addAll(mapped)
                kakaoReviewRVAdapter.notifyDataSetChanged()
            }
            override fun onFailure(call: Call<List<KakaoReviewResponse>>, t: Throwable) {
                if (!isAdded) return
                Log.e("카카오 리뷰 API 연동 실패", "onFailure: ${t.message}", t)
            }
        })

        /* ★ 서버가 "단일 객체" 반환할 때는 위 대신 이걸 사용
        svc.getNaverReviews(storeId).enqueue(object: Callback<NaverReviewResponse> {
            override fun onResponse(
                call: Call<NaverReviewResponse>,
                response: Response<NaverReviewResponse>
            ) {
                if (!isAdded) return
                if (!response.isSuccessful) return
                response.body()?.let { one ->
                    val mapped = listOf(one.toUi())
                    items.clear()
                    items.addAll(mapped)
                    naverReviewRVAdapter.notifyDataSetChanged()
                }
            }
            override fun onFailure(call: Call<NaverReviewResponse>, t: Throwable) {}
        })
        */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
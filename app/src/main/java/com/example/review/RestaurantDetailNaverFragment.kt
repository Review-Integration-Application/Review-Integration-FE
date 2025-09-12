package com.example.review

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.review.adapter.NaverReviewRVAdapter
import com.example.review.api.Response.NaverReviewResponse
import com.example.review.api.RestoreItf
import com.example.review.api.RetrofitBaseObj
import com.example.review.databinding.FragmentRestaurantDetailNaverBinding
import com.example.review.dataclass.Naver_review
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private fun NaverReviewResponse.toUi(): Naver_review {
    val urls = (img_url ?: "")
        .split(",", ";")
        .map { it.trim() }
        .filter { it.isNotEmpty() && it.lowercase() != "none" }

    return Naver_review(
        date = date ?: "",
        content = content ?: "",
        imageUrls = urls
    )
}

class RestaurantDetailNaverFragment: Fragment() {

    companion object {
        private const val ARG_ID = "store_id"

        fun newInstance(storeId: Int): RestaurantDetailNaverFragment {
            val f = RestaurantDetailNaverFragment()
            f.arguments = Bundle().apply { putInt(ARG_ID, storeId) }
            return f
        }
    }

    private var _binding: FragmentRestaurantDetailNaverBinding? = null
    private val binding get() = _binding!!

    private lateinit var naverReviewRVAdapter: NaverReviewRVAdapter
    private val items = mutableListOf<Naver_review>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRestaurantDetailNaverBinding.inflate(inflater, container, false)

//        // 더미 데이터 (API 받기 전)
//        naverReviewDatas.apply {
//            add(Naver_review(5f, "2025.02.12", "정말 맛있어요!", R.drawable.img1, R.drawable.img2))
//            add(Naver_review(3f, "2025.01.30", "그냥 그랬어요."))
//            add(Naver_review(2f, "2025.01.03", "별로였어요."))
//            add(Naver_review(4f, "2025.01.08", "맛있게 잘 먹었습니다."))
//            add(Naver_review(5f, "2025.03.03", "예약없이 바로 들어갔어요! 잘 먹었습니다 :)"))
//            add(Naver_review(5f, "2025.03.12", "종로구 최고 맛집!"))
//        }

        // 어댑터와 더미데이터 연결
        naverReviewRVAdapter = NaverReviewRVAdapter(items)

        // 리사이클러뷰에 어댑터 연결
        //binding.naverContentVp.adapter = naverReviewRVAdapter

        // 레이아웃 매니저 설정
        //binding.naverContentVp.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.naverContentVp.apply {
            adapter = naverReviewRVAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        binding.backButtonIv.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        val storeId = arguments?.getInt(ARG_ID) ?: -1
        if (storeId != -1) fetchNaverReviews(storeId)

        return binding.root
    }

    private fun fetchNaverReviews(storeId: Int) {
        val svc = RetrofitBaseObj.getRetrofit().create(RestoreItf::class.java)

        // ★ 서버가 "리스트" 반환할 때
        svc.getNaverReview(storeId).enqueue(object: Callback<List<NaverReviewResponse>> {
            override fun onResponse(
                call: Call<List<NaverReviewResponse>>,
                response: Response<List<NaverReviewResponse>>
            ) {
                if (!isAdded) return
                if (!response.isSuccessful) {
                    Log.e("네이버 리뷰 API 응답 실패", "HTTP ${response.code()} ${response.errorBody()?.string()}")
                    return
                }
                Log.d("네이버 리뷰 API 조회 성공", "성공성공")
                val body = response.body().orEmpty()
                val mapped = body.map { it.toUi() }
                items.clear()
                items.addAll(mapped)
                naverReviewRVAdapter.notifyDataSetChanged()
            }
            override fun onFailure(call: Call<List<NaverReviewResponse>>, t: Throwable) {
                if (!isAdded) return
                Log.e("네이버 리뷰 API 연동 실패", "onFailure: ${t.message}", t)
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
package com.example.review

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.review.api.Response.RestaurantDetailResponse
import com.example.review.api.RestoreItf
import com.example.review.api.RetrofitBaseObj
import com.example.review.databinding.FragmentRestaurantDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantDetailFragment: Fragment() {

    companion object {
        private const val ARG_ID = "restore_id"

        fun newInstance(restoreId: Int): RestaurantDetailFragment {
            val fragment = RestaurantDetailFragment()
            val args = Bundle()
            args.putInt(ARG_ID, restoreId)
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: FragmentRestaurantDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRestaurantDetailBinding.inflate(inflater, container, false)

        // 네이버 리뷰 버튼 클릭 시
        binding.naverReviewBt.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main, RestaurantDetailNaverFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        // 카카오 리뷰 버튼 클릭 시
        binding.kakaoReviewBt.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main, RestaurantDetailKakaoFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val restoreId = arguments?.getInt(ARG_ID) ?: -1
        if (restoreId == -1) {
            binding.restaurantDetailTitleTv.text = "식당 정보 없음"
            return
        }

        Log.d("식당 아이디", restoreId.toString())

        // 식당 상세 조회 api
        val restoreDetailService = RetrofitBaseObj.getRetrofit().create(RestoreItf::class.java)
        restoreDetailService.getRestaurant(restoreId).enqueue(object:
            Callback<RestaurantDetailResponse>{
            override fun onResponse(
                call: Call<RestaurantDetailResponse>,
                response: Response<RestaurantDetailResponse>
            ) {
                if (response.isSuccessful) {
                    val detail = response.body()
                    detail?.let {
                        binding.restaurantDetailTitleTv.text = it.restore_name
                        binding.restaurantTypeTv.text = it.category
                        binding.totalReviewTv.text = "총 리뷰 ${it.total_reviews_num}"
                        binding.reviewSummationTv.text = it.review_short ?: ""
                    }
                }
            }

            override fun onFailure(call: Call<RestaurantDetailResponse>, t: Throwable) {
                binding.restaurantDetailTitleTv.text = "조회 실패"
            }

        })

        // 백버튼 클릭 시
        binding.backButtonIv.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
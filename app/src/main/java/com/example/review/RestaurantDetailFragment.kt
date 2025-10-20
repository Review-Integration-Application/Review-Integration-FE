package com.example.review

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.review.adapter.ImageSliderAdapter
import com.example.review.adapter.RestaurantImageAdapter
import com.example.review.api.Response.RestaurantDetailResponse
import com.example.review.api.RestoreItf
import com.example.review.api.RetrofitBaseObj
import com.example.review.databinding.FragmentRestaurantDetailBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantDetailFragment: Fragment() {

    companion object {
        private const val ARG_ID = "store_id"

        fun newInstance(storeId: Int): RestaurantDetailFragment {
            val fragment = RestaurantDetailFragment()
            val args = Bundle()
            args.putInt(ARG_ID, storeId)
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: FragmentRestaurantDetailBinding? = null
    private val binding get() = _binding!!

//    private lateinit var pagerAdapter: ImageSliderAdapter
//    private var realSize: Int = 0
//    private var isPagerCallbackRegistered = false

    private lateinit var imageAdapter: RestaurantImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRestaurantDetailBinding.inflate(inflater, container, false)

//        // 초기 빈 어댑터 연결 (경고 방지)
//        pagerAdapter = ImageSliderAdapter(emptyList())
//        binding.restaurantImagePager.adapter = pagerAdapter
//        binding.restaurantImagePager.offscreenPageLimit = 1

        // RecyclerView를 미리 설정합니다.
        setupRecyclerView()

        // 클릭 리스너 설정은 그대로 유지합니다. XML의 ID와 일치합니다.
        setupClickListeners()

        return binding.root
    }

    private fun setupRecyclerView() {
        imageAdapter = RestaurantImageAdapter(emptyList())
        binding.restaurantImageRv.adapter = imageAdapter
        binding.restaurantImageRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setupClickListeners() {
        binding.naverReviewTv.setOnClickListener {
            val storeId = arguments?.getInt(ARG_ID) ?: -1
            parentFragmentManager.beginTransaction()
                .replace(R.id.main, RestaurantDetailNaverFragment.newInstance(storeId))
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.kakaoReviewTv.setOnClickListener {
            val storeId = arguments?.getInt(ARG_ID) ?: -1
            parentFragmentManager.beginTransaction()
                .replace(R.id.main, RestaurantDetailKakaoFragment.newInstance(storeId))
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.backButtonIv.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val storeId = arguments?.getInt(ARG_ID) ?: -1
        if (storeId == -1) {
            binding.restaurantDetailTitleTv.text = "식당 정보 없음"
            return
        }

        Log.d("식당 아이디", storeId.toString())

        // 식당 상세 조회 api
        val restoreDetailService = RetrofitBaseObj.getRetrofit().create(RestoreItf::class.java)
        restoreDetailService.getRestaurant(storeId).enqueue(object:
            Callback<RestaurantDetailResponse>{
            override fun onResponse(
                call: Call<RestaurantDetailResponse>,
                response: Response<RestaurantDetailResponse>
            ) {
                if (!isAdded) return
                if (!response.isSuccessful) {
                    Log.e("DETAIL", "HTTP ${response.code()} ${response.errorBody()?.string()}")
                    binding.restaurantDetailTitleTv.text = "조회 실패 (${response.code()})"
                    return
                }

                val detail = response.body()
                if (detail == null) {
                    Log.e("DETAIL", "body null")
                    binding.restaurantDetailTitleTv.text = "데이터 없음"
                    return
                }

                binding.restaurantDetailTitleTv.text = detail.store_name ?: "이름 미제공"
                binding.restaurantDescriptionTv.text = detail.review_summary ?: "깔끔하고 맛있는 음식과 매력적인 인테리어"
                val category = detail.category
                if (!category.isNullOrBlank()) {
                    binding.chipCategory.text = "#$category"
                    binding.chipCategory.isVisible = true
                } else {
                    binding.chipCategory.isVisible = false
                }

                // 이미지 URL 파싱
                val urls = detail.img_urls
                    ?.split(",", ";")
                    ?.map { it.trim() }
                    ?.filter { it.isNotEmpty() && it.lowercase() != "none" }
                    .orEmpty()

                // [수정됨] ViewPager2 대신 RecyclerView 업데이트
                binding.restaurantImageRv.isVisible = urls.isNotEmpty()
                imageAdapter.updateUrls(urls)
            }

            override fun onFailure(call: Call<RestaurantDetailResponse>, t: Throwable) {
                if (!isAdded) return
                Log.e("DETAIL", "onFailure: ${t.message}", t)
                binding.restaurantDetailTitleTv.text = "조회 실패"
                binding.restaurantImageRv.isVisible = false // 실패 시 이미지 뷰 숨김
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
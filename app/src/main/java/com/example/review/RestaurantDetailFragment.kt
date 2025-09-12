package com.example.review

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.review.adapter.ImageSliderAdapter
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

    private lateinit var pagerAdapter: ImageSliderAdapter
    private var realSize: Int = 0
    private var isPagerCallbackRegistered = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRestaurantDetailBinding.inflate(inflater, container, false)

        // 초기 빈 어댑터 연결 (경고 방지)
        pagerAdapter = ImageSliderAdapter(emptyList())
        binding.restaurantImagePager.adapter = pagerAdapter
        binding.restaurantImagePager.offscreenPageLimit = 1

        // 네이버 리뷰 버튼 클릭 시
        binding.naverReviewBt.setOnClickListener {
            val storeId = arguments?.getInt(ARG_ID) ?: -1
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.main,
                    RestaurantDetailNaverFragment.newInstance(storeId)  // ← storeId 전달
                )
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        // 카카오 리뷰 버튼 클릭 시
        binding.kakaoReviewBt.setOnClickListener {
            val storeId = arguments?.getInt(ARG_ID) ?: -1
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.main,
                    RestaurantDetailKakaoFragment.newInstance(storeId)  // ← storeId 전달
                )
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        return binding.root
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
                binding.restaurantTypeTv.text = detail.category ?: ""
                binding.reviewSummationTv.text = detail.review_summary ?: ""

                // 이미지 URL 파싱
                val urls = detail.img_urls
                    ?.split(",", ";")
                    ?.map { it.trim() }
                    ?.filter { it.isNotEmpty() && it.lowercase() != "none" }
                    .orEmpty()

                setupImagePager(urls)

//                val raw = detail.img_urls?.trim()
//                val urls = raw?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }.orEmpty()
//
//                if (urls.isNotEmpty()) {
//                    binding.restaurantImage1Iv.visibility = View.VISIBLE
//                    Glide.with(this@RestaurantDetailFragment)
//                        .load(urls[0])
//                        .transform(CenterCrop(), RoundedCorners(16))
//                        .into(binding.restaurantImage1Iv)
//                } else {
//                    binding.restaurantImage1Iv.visibility = View.GONE
//                }
//
//                if (urls.size >= 2) {
//                    binding.restaurantImage2Iv.visibility = View.VISIBLE
//                    Glide.with(this@RestaurantDetailFragment)
//                        .load(urls[1])
//                        .transform(CenterCrop(), RoundedCorners(16))
//                        .into(binding.restaurantImage2Iv)
//                } else {
//                    binding.restaurantImage2Iv.visibility = View.GONE
//                }
            }

            override fun onFailure(call: Call<RestaurantDetailResponse>, t: Throwable) {
                if (!isAdded) return
                Log.e("DETAIL", "onFailure: ${t.message}", t)
                binding.restaurantDetailTitleTv.text = "조회 실패"
                setupImagePager(emptyList()) // 실패 시 비워서 안전하게
            }

        })

        // 백버튼 클릭 시
        binding.backButtonIv.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setupImagePager(urls: List<String>) {
        realSize = urls.size

        // 표시/숨김
        val showPager = realSize > 0
        binding.restaurantImagePager.visibility = if (showPager) View.VISIBLE else View.GONE
        binding.imageIndicator.visibility = if (showPager) View.VISIBLE else View.GONE
        if (!showPager) return

        // 어댑터에 데이터 주입
        pagerAdapter.submitUrls(urls)

        // 시작 위치: 1 (복제 앞 아이템을 건너뛰고 실제 첫 장)
        if (pagerAdapter.getDisplaySize() >= 2) {
            binding.restaurantImagePager.setCurrentItem(1, false)
        }

        // 인디케이터: 실제 개수만큼 점 생성
        val indicator = binding.imageIndicator
        indicator.removeAllTabs()
        repeat(realSize) { indicator.addTab(indicator.newTab()) }
        indicator.setScrollPosition(0, 0f, true) // 첫 점 활성화

        // 페이지 변경 콜백 (복제 경계에서 점프 + 점 업데이트)
        if (!isPagerCallbackRegistered) {
            binding.restaurantImagePager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val displaySize = pagerAdapter.getDisplaySize()
                    if (displaySize == 0) return

                    when (position) {
                        0 -> { // 앞 복제(C) -> 실제 마지막으로 순간 점프
                            binding.restaurantImagePager.post {
                                binding.restaurantImagePager.setCurrentItem(displaySize - 2, false)
                                indicator.setScrollPosition(realSize - 1, 0f, true)
                            }
                        }
                        displaySize - 1 -> { // 뒤 복제(A) -> 실제 첫 장으로 순간 점프
                            binding.restaurantImagePager.post {
                                binding.restaurantImagePager.setCurrentItem(1, false)
                                indicator.setScrollPosition(0, 0f, true)
                            }
                        }
                        else -> {
                            val realPos = pagerAdapter.toRealPos(position)
                            indicator.setScrollPosition(realPos, 0f, true)
                        }
                    }
                }
            })
            isPagerCallbackRegistered = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
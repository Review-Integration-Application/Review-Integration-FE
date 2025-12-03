package com.example.review

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.review.adapter.RestaurantImageAdapter
import com.example.review.api.Response.ImageResponse
import com.example.review.api.Response.RestaurantDetailResponse
import com.example.review.api.Response.ReviewSummaryResponse
import com.example.review.api.RestoreItf
import com.example.review.api.RetrofitBaseObj
import com.example.review.databinding.FragmentRestaurantDetailBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
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
    private var storeId: Int = -1 // storeId를 멤버 변수로 저장

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

        storeId = arguments?.getInt(ARG_ID) ?: -1 // storeId 저장

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
        // storeId가 -1이 아닌지 확인 후 사용
        binding.naverReviewTv.setOnClickListener {
            if (storeId != -1) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main, RestaurantDetailNaverFragment.newInstance(storeId))
                    .addToBackStack(null)
                    .commitAllowingStateLoss()
            }
        }

        binding.kakaoReviewTv.setOnClickListener {
            if (storeId != -1) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main, RestaurantDetailKakaoFragment.newInstance(storeId))
                    .addToBackStack(null)
                    .commitAllowingStateLoss()
            }
        }

        binding.backButtonIv.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (storeId == -1) {
            binding.restaurantDetailTitleTv.text = "식당 정보 없음"
            return
        }

        Log.d("식당 아이디", storeId.toString())

        // API 서비스를 한 번만 생성
        val restoreDetailService = RetrofitBaseObj.getRetrofit().create(RestoreItf::class.java)

        // 1. 식당 상세 조회 API 호출
        loadRestaurantDetails(restoreDetailService)

        // 2. 리뷰 요약 API 호출
        loadReviewSummary(restoreDetailService)

        // 3. 리뷰 요약 이미지 API 호출
        loadSummaryImage(restoreDetailService)
    }

    // [수정] 식당 상세 정보 로드 함수
    private fun loadRestaurantDetails(service: RestoreItf) {
        service.getRestaurant(storeId).enqueue(object: Callback<RestaurantDetailResponse> {
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

                val urls = detail.img_urls
                    ?.split(",", ";")
                    ?.map { it.trim() }
                    ?.filter { it.isNotEmpty() && it.lowercase() != "none" }
                    .orEmpty()

                binding.restaurantImageRv.isVisible = urls.isNotEmpty()
                imageAdapter.updateUrls(urls)
            }

            override fun onFailure(call: Call<RestaurantDetailResponse>, t: Throwable) {
                if (!isAdded) return
                Log.e("DETAIL", "onFailure: ${t.message}", t)
                binding.restaurantDetailTitleTv.text = "조회 실패"
                binding.restaurantImageRv.isVisible = false
            }
        })
    }

    // === [추가] 리뷰 요약 (장/단점, 해시태그) 로드 함수 ===
    private fun loadReviewSummary(service: RestoreItf) {
        service.getReviewSummary(storeId).enqueue(object: Callback<ReviewSummaryResponse> {
            override fun onResponse(
                call: Call<ReviewSummaryResponse>,
                response: Response<ReviewSummaryResponse>
            ) {
                if (!isAdded || _binding == null) return // 뷰가 파괴된 경우 방지

                if (response.isSuccessful && response.body() != null) {
                    val summary = response.body()!!

                    // 1. 장점/단점 텍스트 설정 (XML에서 수정한 ID 사용)
                    binding.goodPointContentTv.text = summary.good_points?.ifBlank { "정보 없음" } ?: "정보 없음"
                    binding.badPointContentTv.text = summary.bad_points?.ifBlank { "정보 없음" } ?: "정보 없음"

                    // 2. 해시태그 Chip 동적 생성
                    // (CSV 데이터가 "#태그1 #태그2" 처럼 공백으로 구분되어 있다고 가정)
                    setupHashtagChips(
                        binding.naverHashtagCg,
                        summary.naver_hashtag,
                        R.color.naverColor // colors.xml에 색상 정의 필요
                    )
                    setupHashtagChips(
                        binding.kakaoHashtagCg,
                        summary.kakao_hashtag,
                        R.color.kakaoColor // colors.xml에 색상 정의 필요
                    )

                } else {
                    Log.e("SUMMARY", "HTTP ${response.code()} ${response.errorBody()?.string()}")
                    binding.goodPointContentTv.text = "요약 정보 조회 실패"
                    binding.badPointContentTv.text = "요약 정보 조회 실패"
                }
            }

            override fun onFailure(call: Call<ReviewSummaryResponse>, t: Throwable) {
                if (!isAdded || _binding == null) return
                Log.e("SUMMARY", "onFailure: ${t.message}", t)
                binding.goodPointContentTv.text = "요약 정보 로드 실패"
                binding.badPointContentTv.text = "요약 정보 로드 실패"
            }
        })
    }

    // 4. [추가] 리뷰 요약 (긍/부정) 이미지 로드 함수
    private fun loadSummaryImage(service: RestoreItf) {
        if (storeId == -1) return // storeId 없으면 중단

        service.getRestaurantImage(storeId).enqueue(object : Callback<ImageResponse> {
            override fun onResponse(
                call: Call<ImageResponse>,
                response: Response<ImageResponse>
            ) {
                // Fragment가 파괴되었거나 view binding이 null이면 중단
                if (!isAdded || _binding == null) return

                if (response.isSuccessful && response.body() != null) {
                    val imageUrl = response.body()!!.imageUrl

                    // 서버에서 받은 URL이 유효한지 확인
                    if (!imageUrl.isNullOrBlank()) {

                        // Glide를 사용해 XML의 review_summary_percent_iv에 이미지 로드
                        Glide.with(requireContext()) // or this@RestaurantDetailFragment
                            .load(imageUrl) // 서버에서 받은 URL
                            .into(binding.reviewSummaryPercentIv) // XML의 ImageView ID

                    } else {
                        // URL이 null이거나 비어있는 경우
                        Log.w("SUMMARY_IMAGE", "Image URL is null or blank.")
                        // 필요시 기본 이미지를 설정하거나 숨길 수 있습니다.
                        // binding.reviewSummaryPercentIv.isVisible = false
                    }
                } else {
                    // HTTP 응답 실패
                    Log.e("SUMMARY_IMAGE", "Response failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                if (!isAdded || _binding == null) return
                // 네트워크 오류 등
                Log.e("SUMMARY_IMAGE", "onFailure: ${t.message}", t)
            }
        })
    }

    // === [추가] 해시태그 문자열을 파싱하여 Chip을 동적으로 추가하는 함수 ===
    private fun setupHashtagChips(chipGroup: ChipGroup, hashtagString: String?, chipColorResId: Int) {
        chipGroup.removeAllViews() // 기존에 XML에 있던 정적 칩 제거

        if (hashtagString.isNullOrBlank()) {
            // 해시태그가 없을 때 "정보 없음" 칩 하나만 추가
            val noDataChip = createChip("정보 없음", R.color.naverColor) // default 색상 필요
            chipGroup.addView(noDataChip)
            return
        }

        val hashtags = hashtagString.split(" ") // 공백으로 태그 분리
            .map { it.trim() }
            .filter { it.isNotEmpty() } // 빈 문자열 제거

        if (hashtags.isEmpty()) {
            val noDataChip = createChip("정보 없음", R.color.kakaoColor)
            chipGroup.addView(noDataChip)
            return
        }

        // CSV 파일에 `#`이 포함된 채로 저장되어 있으므로, 그대로 사용
        hashtags.forEach { tag ->
            val chip = createChip(tag, chipColorResId)
            chipGroup.addView(chip)
        }
    }

    // === [추가] Chip을 생성하는 헬퍼 함수 ===
    private fun createChip(tag: String, chipColorResId: Int) : Chip {
        val chip = Chip(context) // requireContext() 대신 context 사용 (null 안전)
        chip.text = tag

        // colors.xml에 정의된 색상 리소스를 가져옵니다.
        val chipColor = ContextCompat.getColor(requireContext(), chipColorResId)
        chip.chipBackgroundColor = ColorStateList.valueOf(chipColor)

        // 텍스트 색상 등 추가 스타일 설정
        // chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        // chip.isClickable = false
        // chip.isCheckable = false
        return chip
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
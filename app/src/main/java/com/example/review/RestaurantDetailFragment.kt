package com.example.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.review.databinding.FragmentRestaurantDetailBinding

class RestaurantDetailFragment: Fragment() {

    companion object {
        private const val ARG_NAME = "name"
        private const val ARG_DESC = "desc"

        fun newInstance(name: String, desc: String): RestaurantDetailFragment {
            val fragment = RestaurantDetailFragment()
            val args = Bundle()
            args.putString(ARG_NAME, name)
            args.putString(ARG_DESC, desc)
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

        val name = arguments?.getString(ARG_NAME)
        //val desc = arguments?.getString(ARG_DESC)

        // 바인딩해서 각 뷰에 세팅
        binding.restaurantDetailTitleTv.text = name ?: "이름없음"
        //binding.reviewSummationTv.text = desc ?: ""

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
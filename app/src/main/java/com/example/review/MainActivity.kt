package com.example.review

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.review.databinding.ActivityMainBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMapSdk

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 프래그먼트 띄우기
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, RestaurantDetailFragment())
            .commit()

        // 네이버 지도 클라이언트 ID 설정
//        NaverMapSdk.getInstance(this).client =
//            NaverMapSdk.NaverCloudPlatformClient("1nul4fzjii")
//
//        mapView = binding.mapView
//
//        // 지도 준비 완료 후 실행되는 코드
//        mapView.getMapAsync{ naverMap ->
//            // 용산구 중심 위치 설정 (위도: 37.5326, 경도: 126.9944)
//            val cameraPosition = CameraPosition(
//                LatLng(37.5326, 126.9944), // 용산구의 위도, 경도
//                12.0 // 줌 레벨 (조정 가능)
//            )
//            naverMap.moveCamera(CameraUpdate.toCameraPosition(cameraPosition))
//        }
    }

//    override fun onResume() {
//        super.onResume()
//        mapView.onResume()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        mapView.onPause()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mapView.onDestroy()
//    }
}
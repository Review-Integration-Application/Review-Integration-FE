package com.example.review

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.review.databinding.ActivityMainBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var mainGoogleMap: GoogleMap
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    data class Restaurant(val name: String, val desc: String, val lat: Double, val lng: Double)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 바텀시트 Behavior 연결
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN // 처음엔 숨김!

        // 식당 데이터 준비 (위치는 필요시 미세조정)
        val restaurants = listOf(
            Restaurant("송추가마골 인어반 광화문점", "한국식 BBQ", 37.5725, 126.9789),
            Restaurant("박순례 손말이고기 산정집 광화문점", "한식/고기", 37.5730, 126.9814),
            Restaurant("족발야시장&무청감자탕 광화문점", "족발/감자탕", 37.5717, 126.9768),
            Restaurant("오가와", "초밥/일식", 37.5724, 126.9826)
        )

        // 지도 준비
        val supportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        supportMapFragment.getMapAsync { map -> // 구글 지도 사용 준비 완료 시 반응하는 리스너 등록

            // 구글맵 객체 변수에 담아 사용
            mainGoogleMap = map

            // 지도의 옵션 설정
            map.uiSettings.isZoomControlsEnabled = true // 확대 축소 기능
            map.uiSettings.isMyLocationButtonEnabled = false // 현재 위치 표시하는 버튼 표시 여부

            // 종로구 카메라 초기 위치
            val jongnoLatLng = LatLng(37.572950, 126.979357)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(jongnoLatLng, 15f)
            mainGoogleMap.moveCamera(cameraUpdate)

            // 마커와 식당 연결
            val markerRestaurantMap = mutableMapOf<Marker, Restaurant>()
            for (restaurant in restaurants) {
                val marker = map.addMarker(
                    MarkerOptions()
                        .position(LatLng(restaurant.lat, restaurant.lng))
                        .title(restaurant.name)
                )
                marker?.let {
                    markerRestaurantMap[it] = restaurant
                }
            }

            // 마커 클릭 리스너
            map.setOnMarkerClickListener { marker ->
                markerRestaurantMap[marker]?.let { restaurant ->
                    binding.tvRestaurantName.text = restaurant.name
                    binding.tvRestaurantDesc.text = restaurant.desc
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
                true
            }
        }
    }
}
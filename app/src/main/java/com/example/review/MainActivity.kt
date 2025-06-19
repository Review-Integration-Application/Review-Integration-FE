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
    private var selectedRestaurant: Restaurant? = null

    data class Restaurant(val name: String, val desc: String, val lat: Double, val lng: Double)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 바텀시트 Behavior 연결
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN // 처음엔 숨김!

        // 지도 fragment를 add할 때, 인스턴스를 변수에 저장
        val mapFragment: SupportMapFragment
        if (savedInstanceState == null) {
            mapFragment = SupportMapFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, mapFragment, "map")
                .commit()
        } else {
            mapFragment = supportFragmentManager.findFragmentByTag("map") as SupportMapFragment
        }

        // SupportMapFragment 얻어서 getMapAsync 등록
        mapFragment.getMapAsync { map ->
            mainGoogleMap = map
            map.uiSettings.isZoomControlsEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = false

            // 종로구 카메라 초기 위치
            val jongnoLatLng = LatLng(37.572950, 126.979357)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(jongnoLatLng, 15f))

            // 식당 데이터 (임시)
            val restaurants = listOf(
                Restaurant("송추가마골 인어반 광화문점", "★4.3 / 총 리뷰 300", 37.5725, 126.9789),
                Restaurant("박순례 손말이고기 산정집 광화문점", "★4.0 / 총 리뷰 250", 37.5730, 126.9814),
                Restaurant("족발야시장&무청감자탕 광화문점", "★4.4 / 총 리뷰 270", 37.5717, 126.9768),
                Restaurant("오가와", "★4.2 / 총 리뷰 280", 37.5724, 126.9826)
            )

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

            // 마커 클릭 시
            map.setOnMarkerClickListener { marker ->
                markerRestaurantMap[marker]?.let { restaurant ->
                    binding.tvRestaurantName.text = restaurant.name
                    binding.tvRestaurantDesc.text = restaurant.desc
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    selectedRestaurant = restaurant   // 클릭된 식당 저장
                }
                true
            }
        }

        // 바텀시트 클릭 시 상세화면
        binding.bottomSheet.setOnClickListener {
            selectedRestaurant?.let { restaurant ->
                val fragment = RestaurantDetailFragment.newInstance(restaurant.name, restaurant.desc)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment) // 반드시 fragment_container!
                    .addToBackStack(null)
                    .commit()

                // 바텀시트 숨기기
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }
}
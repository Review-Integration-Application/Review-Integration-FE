package com.example.review

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.review.api.Response.RestaurantResponse
import com.example.review.api.RestoreItf
import com.example.review.api.RetrofitBaseObj
import com.example.review.databinding.ActivityMainBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private suspend fun geocodeAddress(addr: String): LatLng? = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(this@MainActivity, Locale.KOREA)
            // 필요하면 서울 범위 bias도 가능: getFromLocationName(addr, 1, 37.40,126.80, 37.70,127.20)
            val list = geocoder.getFromLocationName(addr, 1)
            if (!list.isNullOrEmpty()) LatLng(list[0].latitude, list[0].longitude) else null
        } catch (_: Exception) {
            null
        }
    }

    private fun normalizeAddress(s: String): String =
        s.replace("\n", " ").replace(Regex("\\(.*?\\)"), "").trim()

    lateinit var binding: ActivityMainBinding
    private lateinit var mainGoogleMap: GoogleMap
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private var selectedRestaurant: RestaurantResponse? = null

    //data class Restaurant(val name: String, val desc: String, val lat: Double, val lng: Double)

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
//            val restaurants = listOf(
//                Restaurant("송추가마골 인어반 광화문점", "★4.3 / 총 리뷰 300", 37.5725, 126.9789),
//                Restaurant("박순례 손말이고기 산정집 광화문점", "★4.0 / 총 리뷰 250", 37.5730, 126.9814),
//                Restaurant("족발야시장&무청감자탕 광화문점", "★4.4 / 총 리뷰 270", 37.5717, 126.9768),
//                Restaurant("오가와", "★4.2 / 총 리뷰 280", 37.5724, 126.9826)
//            )

            // 마커와 식당 연결
            val markerRestaurantMap = mutableMapOf<Marker, RestaurantResponse>()

            // 식당 조회 API
            val restoreService = RetrofitBaseObj.getRetrofit().create(RestoreItf::class.java)
            restoreService.getRestaurants().enqueue(object: Callback<List<RestaurantResponse>>{
                override fun onResponse(
                    call: Call<List<RestaurantResponse>>,
                    response: Response<List<RestaurantResponse>>
                ) {
                    if (response.isSuccessful) {
                        Log.d("서어어어어어어ㅓ엉ㅇ고오오오오옹", "성공성공")
//                        val restaurants = response.body() ?: emptyList()
//                        runOnUiThread {
//                            for (restaurant in restaurants) {
//                                val marker = map.addMarker(
//                                    MarkerOptions()
//                                        .position(LatLng(restaurant.lat, restaurant.lng))
//                                        .title(restaurant.restore_name)
//                                )
//                                marker?.let {
//                                    markerRestaurantMap[it] = restaurant
//                                }
//                            }
//                        }
                        val restaurants = response.body().orEmpty()

                        lifecycleScope.launch {
                            for (restaurant in restaurants) {
                                // 1) 위경도 이미 있으면 사용
                                val hasLatLng = restaurant.lat != 0.0 && restaurant.lng != 0.0

                                val pos: LatLng? = when {
                                    hasLatLng -> LatLng(restaurant.lat, restaurant.lng)

                                    // 2) 없으면 주소로 지오코딩 (address 필드명 맞게 수정)
                                    !restaurant.location.isNullOrBlank() -> {
                                        geocodeAddress(normalizeAddress(restaurant.location!!))
                                    }

                                    else -> null
                                }

                                pos?.let { latLng ->
                                    val marker = map.addMarker(
                                        MarkerOptions()
                                            .position(latLng)
                                            .title(restaurant.restore_name)
                                    )
                                    if (marker != null) {
                                        markerRestaurantMap[marker] = restaurant
                                    }
                                }

                                // 지오코딩 연속 호출시 살짝 텀(선택)
                                // delay(120)
                            }
                        }

                    } else {
                        Log.e("API 응답 실패", "HTTP ${response.code()}  ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<List<RestaurantResponse>>, t: Throwable) {
                    Log.e("API 연동 실패", "onFailure", t)
                }

            })

            // 마커 클릭 시 바텀시트 등장 (RestaurantResponse 사용)
            map.setOnMarkerClickListener { marker ->
                markerRestaurantMap[marker]?.let { restaurant ->
                    binding.tvRestaurantName.text = restaurant.restore_name
                    binding.tvRestaurantDesc.text =
                        "★${restaurant.restore_score} / 총 리뷰 ${restaurant.total_reviews_num}"
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    selectedRestaurant = restaurant
                    // 클릭된 식당 저장 (Restaurant 타입)
                }
                true
            }
        }

        // 바텀시트 클릭 시 상세화면으로 이동
        binding.bottomSheet.setOnClickListener {
            selectedRestaurant?.let { restaurant ->
                val fragment = RestaurantDetailFragment.newInstance(restaurant.restore_ID)
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
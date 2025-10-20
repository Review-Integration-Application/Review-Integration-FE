package com.example.review

import RestaurantListAdapter
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.review.api.Response.RestaurantResponse
import com.example.review.api.RestoreItf
import com.example.review.api.RetrofitBaseObj
import com.example.review.databinding.ActivityMainBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import kotlin.coroutines.resume

class MainActivity : AppCompatActivity() {

    private val addressCache = mutableMapOf<String, LatLng>()

    // ▼▼▼▼▼ 새로 추가된 프로퍼티 ▼▼▼▼▼
    private lateinit var restaurantListAdapter: RestaurantListAdapter
    private var restaurantList = listOf<RestaurantResponse>()
    private val markerRestaurantMap = mutableMapOf<Marker, RestaurantResponse>()
    // ▲▲▲▲▲ 새로 추가된 프로퍼티 ▲▲▲▲▲

    lateinit var binding: ActivityMainBinding
    private lateinit var mainGoogleMap: GoogleMap
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var selectedRestaurant: RestaurantResponse? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ▼▼▼▼▼ 새로 추가된 함수 호출 ▼▼▼▼▼
        setupDrawer()
        setupRecyclerView()
        // ▲▲▲▲▲ 새로 추가된 함수 호출 ▲▲▲▲▲

        // 프래그먼트 백스택 변경 감지
        supportFragmentManager.addOnBackStackChangedListener {
            // 백스택에 프래그먼트가 하나도 없으면 (즉, 메인 지도 화면이면)
            if (supportFragmentManager.backStackEntryCount == 0) {
                binding.menuButtonIv.visibility = View.VISIBLE
            }
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        val mapFragment: SupportMapFragment
        if (savedInstanceState == null) {
            mapFragment = SupportMapFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, mapFragment, "map")
                .commit()
        } else {
            mapFragment = supportFragmentManager.findFragmentByTag("map") as SupportMapFragment
        }

        mapFragment.getMapAsync { map ->
            mainGoogleMap = map
            map.uiSettings.isZoomControlsEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = false

            val jongnoLatLng = LatLng(37.572950, 126.979357)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(jongnoLatLng, 15f))

            // API 호출 및 마커 생성 로직을 별도 함수로 분리
            fetchAndDisplayRestaurants(map)
        }

        binding.bottomSheet.setOnClickListener {
            binding.menuButtonIv.visibility = View.GONE
            selectedRestaurant?.let { restaurant ->
                val fragment = RestaurantDetailFragment.newInstance(restaurant.store_id)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }

    // ▼▼▼▼▼ Drawer 설정 함수 (신규) ▼▼▼▼▼
    private fun setupDrawer() {
        binding.menuButtonIv.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }
    // ▲▲▲▲▲ Drawer 설정 함수 (신규) ▲▲▲▲▲

    // ▼▼▼▼▼ RecyclerView 설정 함수 (신규) ▼▼▼▼▼
    private fun setupRecyclerView() {
        restaurantListAdapter = RestaurantListAdapter { restaurant ->
            // 리스트 아이템 클릭 시 실행될 코드
            binding.drawerLayout.closeDrawer(GravityCompat.START)

            // 클릭한 식당의 마커 찾기
            val marker = markerRestaurantMap.entries.find { it.value.store_id == restaurant.store_id }?.key

            marker?.let {
                mainGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it.position, 16f))
                showBottomSheetForRestaurant(restaurant)
            }
        }

        binding.navView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.restaurant_recycler_view).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = restaurantListAdapter
        }
    }
    // ▲▲▲▲▲ RecyclerView 설정 함수 (신규) ▲▲▲▲▲


    // ▼▼▼▼▼ API 호출 및 마커 생성 로직 (분리된 함수) ▼▼▼▼▼
    private fun fetchAndDisplayRestaurants(map: GoogleMap) {
        val restoreService = RetrofitBaseObj.getRetrofit().create(RestoreItf::class.java)
        restoreService.getRestaurants().enqueue(object : Callback<List<RestaurantResponse>> {
            override fun onResponse(call: Call<List<RestaurantResponse>>, response: Response<List<RestaurantResponse>>) {
                if (response.isSuccessful) {
                    restaurantList = response.body().orEmpty()
                    restaurantListAdapter.submitList(restaurantList) // RecyclerView 업데이트

                    lifecycleScope.launch {
                        for (restaurant in restaurantList) {
                            val addr = normalizeAddress(restaurant.address)
                            val pos = geocodeAddressCompat(addr)

                            if (pos != null) {
                                val customMarkerIcon = createMarkerIconWithText(this@MainActivity, restaurant.store_name)
                                val marker = map.addMarker(
                                    MarkerOptions()
                                        .position(pos)
                                        .title(restaurant.store_name)
                                        .icon(customMarkerIcon)
                                )
                                marker?.let { markerRestaurantMap[it] = restaurant }
                            } else {
                                Log.w("Geocode", "주소 지오코딩 실패: $addr")
                            }
                            delay(120)
                        }
                    }
                } else {
                    Log.e("API 응답 실패", "HTTP ${response.code()} ${response.errorBody()?.string()}")
                }
            }
            override fun onFailure(call: Call<List<RestaurantResponse>>, t: Throwable) {
                Log.e("API 연동 실패", "onFailure", t)
            }
        })

        map.setOnMarkerClickListener { marker ->
            markerRestaurantMap[marker]?.let { restaurant ->
                showBottomSheetForRestaurant(restaurant)
            }
            true
        }
    }
    // ▲▲▲▲▲ API 호출 및 마커 생성 로직 (분리된 함수) ▲▲▲▲▲


    // ▼▼▼▼▼ 바텀시트 표시 함수 (분리된 함수) ▼▼▼▼▼
    private fun showBottomSheetForRestaurant(restaurant: RestaurantResponse) {
        selectedRestaurant = restaurant
        binding.tvRestaurantName.text = restaurant.store_name
        binding.tvRestaurantDesc.text = "총 리뷰 ${restaurant.total_review_num}"
        binding.reviewSummationTv.text = restaurant.review_summary ?: "깔끔하고 맛있는 음식과 매력적인 인테리어"

        restaurant.img_urls?.let { urls ->
            val firstUrl = urls.split(",").firstOrNull()?.trim()
            if (!firstUrl.isNullOrEmpty()) {
                Glide.with(this).load(firstUrl).centerCrop().into(binding.reviewMainImageIv)
            } else {
                binding.reviewMainImageIv.setImageDrawable(null)
            }
        } ?: run {
            binding.reviewMainImageIv.setImageDrawable(null)
        }
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }
    // ▲▲▲▲▲ 바텀시트 표시 함수 (분리된 함수) ▲▲▲▲▲


    // ▼▼▼▼▼ 아래는 기존과 동일한 유틸리티 함수들 ▼▼▼▼▼
    private fun createMarkerIconWithText(context: Context, storeName: String): BitmapDescriptor {
        val markerView = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            .inflate(R.layout.marker_layout, null)
        val tvMarkerName = markerView.findViewById<TextView>(R.id.tv_marker_name)
        tvMarkerName.text = storeName
        markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        markerView.layout(0, 0, markerView.measuredWidth, markerView.measuredHeight)
        val bitmap = Bitmap.createBitmap(markerView.measuredWidth, markerView.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        markerView.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun normalizeAddress(raw: String?): String {
        if (raw.isNullOrBlank()) return ""
        return raw
            .replace("\n", " ")
            .replace(Regex("\\(.*?\\)"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private suspend fun geocodeAddressCompat(query: String): LatLng? {
        if (query.isBlank()) return null
        addressCache[query]?.let { return it }
        val geocoder = Geocoder(this, Locale.KOREA)
        return try {
            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine<LatLng?> { cont ->
                    geocoder.getFromLocationName(query, 1, object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<android.location.Address>) {
                            val p = addresses.firstOrNull()
                            val latLng = p?.let { LatLng(it.latitude, it.longitude) }
                            cont.resume(latLng)
                        }
                        override fun onError(errorMessage: String?) {
                            cont.resume(null)
                        }
                    })
                }
            } else {
                withContext(Dispatchers.IO) {
                    val list = geocoder.getFromLocationName(query, 1)
                    val p = list?.firstOrNull()
                    p?.let { LatLng(it.latitude, it.longitude) }
                }
            }
            result?.also { addressCache[query] = it }
        } catch (e: Exception) {
            null
        }
    }
}
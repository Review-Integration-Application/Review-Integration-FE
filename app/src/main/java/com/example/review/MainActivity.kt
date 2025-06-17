package com.example.review

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.review.databinding.ActivityMainBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMapSdk

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    lateinit var binding: ActivityMainBinding

    val permissionList = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    // 위치 측정 리스너
    var myLocationListener: LocationListener? = null

    // 구글 지도 객체
    lateinit var mainGoogleMap: GoogleMap

    // 현재 사용자 위치에 표시되는 마커
    var myMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissions(permissionList, 0)

        // MapFragment 객체 추출
        val supportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        // 구글 지도 사용 준비 완료 시 반응하는 리스너 등록
        supportMapFragment.getMapAsync {

            // 구글맵 객체 변수에 담아 사용
            mainGoogleMap = it

            // 지도의 옵션 설정
            // 확대 축소 기능
            it.uiSettings.isZoomControlsEnabled = true

            // 현재 위치 표시 기능
            it.isMyLocationEnabled = true

            // 현재 위치 표시하는 버튼 표시 여부
            it.uiSettings.isMyLocationButtonEnabled = false

            // 위치 정보 관리하는 객체 가져오기
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

            // 권한 허용 여부 확인
            // 허용 : PERMISSION_GRANTED
            // 허용 X : PERMISSION_DENIED
            val a1 = ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
            val a2 = ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION)

            if(a1 == PackageManager.PERMISSION_GRANTED && a2 == PackageManager.PERMISSION_GRANTED){
                // 현재 저장되어 있는 위치 정보값 가져오기
                val location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                val location2 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                if(location1 != null){
                    setMyLocation(location1)
                }
                else if (location2 != null){
                    setMyLocation(location2)
                }

                // 현재 위치 측정하여 지도 갱신하는 함수
                getMyLocation()
            }
        }

//        // 프래그먼트 띄우기
//        supportFragmentManager.beginTransaction()
//            .replace(binding.fragmentContainer.id, RestaurantDetailFragment())
//            .commit()

//        // 네이버 지도 클라이언트 ID 설정
//        NaverMapSdk.getInstance(this).client = NaverMapSdk.NaverCloudPlatformClient("xxx82l9yu4")
//
//
//        mapView = binding.mapView
//
//        // 지도 준비 완료 후 실행되는 코드
//        mapView.getMapAsync{ naverMap ->
//            // 지도 세팅 코드
//            Log.d("NaverMap", "네이버 지도 콜백 호출됨")
//        }
    }

    // 현재 위치의 위도 경도값을 통해 구글 지도를 해당 위치로 이동
    fun setMyLocation(location: Location){

        // 위치 측정 중단
        if(myLocationListener != null){
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            locationManager.removeUpdates(myLocationListener!!)
            myLocationListener = null
        }

        // 위도 & 경도 관리 객체
        val latLng = com.google.android.gms.maps.model.LatLng(location.latitude, location.longitude)

        // 지도를 이용시키기 위한 객체
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)

        // 지도 이동
        // mainGoogleMap.moveCamera(cameraUpdate)
        // 애니메이션 적용
        mainGoogleMap.animateCamera(cameraUpdate)

        // 현재 위치에 마커 표시
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)

        // 마커 이미지 변경
        val markerBitmap = BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation)
        markerOptions.icon(markerBitmap)

        // 기존에 표시한 마커를 제거한다
        if(myMarker != null){
            myMarker?.remove()
            myMarker = null
        }
        mainGoogleMap.addMarker(markerOptions)

        Toast.makeText(this@MainActivity, "위도: ${location.latitude}, 경도 : ${location.longitude}", Toast.LENGTH_SHORT).show()
    }

    fun getMyLocation(){
        val a1 = ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
        val a2 = ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (a1 == PackageManager.PERMISSION_GRANTED && a2 == PackageManager.PERMISSION_GRANTED){

            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

            // 위치 측정 리스너
            myLocationListener = object : LocationListener {
                override fun onLocationChanged(p0: Location) {
                    setMyLocation(p0)
                }
            }

            // 위치 측정 요청
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, myLocationListener!!)
            }

//            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
//                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, myLocationListener!!)
//            }
        }
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
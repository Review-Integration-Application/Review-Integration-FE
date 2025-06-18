package com.example.review

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.review.databinding.ActivityMainBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

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

        // MapFragment 객체 추출
        val supportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        // 구글 지도 사용 준비 완료 시 반응하는 리스너 등록
        supportMapFragment.getMapAsync {

            // 구글맵 객체 변수에 담아 사용
            mainGoogleMap = it

            // 지도의 옵션 설정
            // 확대 축소 기능
            it.uiSettings.isZoomControlsEnabled = true

            // 현재 위치 표시하는 버튼 표시 여부
            it.uiSettings.isMyLocationButtonEnabled = false

            val jongnoLatLng = com.google.android.gms.maps.model.LatLng(37.572950, 126.979357)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(jongnoLatLng, 15f)
            mainGoogleMap.moveCamera(cameraUpdate)

            val markerOptions = MarkerOptions()
                .position(jongnoLatLng)
                .title("종로구")
            mainGoogleMap.addMarker(markerOptions)
        }
    }
}
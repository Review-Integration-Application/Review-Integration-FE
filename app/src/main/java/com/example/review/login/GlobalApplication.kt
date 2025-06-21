package com.example.review.login

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Kakao SDK 초기화
        KakaoSdk.init(this, "2f54706174cb1cedb14367762b572bb0") //네이티브 키 값 입력
    }
}
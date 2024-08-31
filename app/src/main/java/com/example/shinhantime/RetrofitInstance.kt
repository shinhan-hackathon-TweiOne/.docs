package com.example.shinhantime

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstances {

    private const val BASE_URL = "http://3.39.169.246:8080"

    // Retrofit 인스턴스를 생성하는 부분을 하나로 통합
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // UserApiService 인터페이스를 이용해 API 호출을 관리
    val userApiService: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }

    // FleaMarketApiService 인터페이스를 이용해 API 호출을 관리
    val fleaMarketApiService: FleaMarketApiService by lazy {
        retrofit.create(FleaMarketApiService::class.java)
    }

    // 다른 ApiService처럼 S3ApiService를 추가합니다.
    val s3ApiService: S3ApiService by lazy {
        retrofit.create(S3ApiService::class.java)
    }
}

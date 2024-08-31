package com.example.shinhantime

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitInstances {

    private const val BASE_URL = "http://3.39.169.246:8080"

    // 로깅 인터셉터 설정
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // OkHttpClient에 로깅 인터셉터 추가
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // Gson 인스턴스에 lenient 설정 추가
    private val gson = GsonBuilder()
        .setLenient()  // lenient 모드 설정
        .create()

    // Retrofit 인스턴스를 생성하는 부분을 하나로 통합
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create()) // Scalar converter 추가
            .addConverterFactory(GsonConverterFactory.create(gson))
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

    // S3ApiService 인터페이스를 이용해 API 호출을 관리
    val s3ApiService: S3ApiService by lazy {
        retrofit.create(S3ApiService::class.java)
    }
}

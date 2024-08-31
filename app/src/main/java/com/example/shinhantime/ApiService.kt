package com.example.shinhantime

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

data class AuthRequest(
    val phoneNumber: String,
)

data class AuthResponse(
    val statusCode: Int,
    val message: String,
    val authCode: String  // 서버에서 보내준 인증 코드, 실제 구조에 맞게 수정 필요
)

// 데이터 클래스 정의
data class VerifyAuthRequest(
    val phoneNumber: String,
    val name: String,
    val authCode: String
)

data class VerifyAuthResponse(
    val statusCode: Int,
    val message: String,
    val userDto: UserDto,
    val jwtToken: JwtToken
)

data class UserDto(
    val id: Int,
    val username: String,
    val name: String,
    val currentMoney: Long,
    val roles: List<String>,
    val mainAccount: Account?
)

data class JwtToken(
    val grantType: String,
    val accessToken: String,
    val refreshToken: String
)

data class ItemRegisterRequest(
    val itemName: String,
    val categoryName: String,
    val imageUrl: String,
    val price: Int
)

data class RegisterItemResponse(
    val message: String
)

// 서버로부터의 응답을 처리하는 데이터 클래스
data class UploadResponse(
    val url: String
)

data class Account(
    val id: Int,
    val accountNumber: String,
    val bankName: String,
    val userId: Int
)

// UserApiService: 유저 인증 관련 API 정의
interface UserApiService {
    // 인증번호 요청을 위한 엔드포인트
    @POST("/auth")
    fun requestAuthCode(@Body request: AuthRequest): Call<AuthResponse>

    // 인증번호 확인 및 사용자 인증을 위한 엔드포인트
    @POST("/verify_auth")
    fun verifyAuthCode(@Body request: VerifyAuthRequest): Call<VerifyAuthResponse>

    @GET("/api/user/{user_id}")
    fun getUserInfo(
        @Path("user_id") userId: Int
    ): Call<UserDto>



    @POST("/api/accounts/{user_id}/verify")
    fun verifyAccount(
        @Path("user_id") userId: Int,
        @Body requestBody: Map<String, String>
    ): Call<VerifyAuthResponse>
}

// FleaMarketApiService: 플리마켓 관련 API 정의
interface FleaMarketApiService {
    @GET("/api/kiosk/{userId}")
    fun getCategoriesAndItems(@Path("userId") userId: Int): Call<List<Category>>

    @POST("/api/kiosk/{userId}")
    fun registerItem(
        @Path("userId") userId: Int,
        @Body requestBody: ItemRegisterRequest
    ): Call<RegisterItemResponse>
}

// S3ApiService: S3 이미지 업로드 API 정의
interface S3ApiService {
    @Multipart
    @POST("/api/s3/upload")
    fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("bucketName") bucketName: RequestBody,
        @Part("keyName") keyName: RequestBody
    ): Call<UploadResponse>
}
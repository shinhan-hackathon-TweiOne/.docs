package com.example.shinhantime.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.shinhantime.ItemRegisterRequest
import com.example.shinhantime.R
import com.example.shinhantime.RegisterItemResponse
import com.example.shinhantime.RetrofitInstances
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddItemActivity : AppCompatActivity() {

    private lateinit var editTextCategory: EditText
    private lateinit var editTextName: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var buttonAddItem: Button

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        // Initialize views
        editTextCategory = findViewById(R.id.edittext_category)
        editTextName = findViewById(R.id.edittext_name)
        editTextPrice = findViewById(R.id.edittext_price)
        buttonAddItem = findViewById(R.id.button_add_item)

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        buttonAddItem.setOnClickListener {
            val category = editTextCategory.text.toString()
            val name = editTextName.text.toString()
            val price = editTextPrice.text.toString().toLongOrNull()

            if (price != null) {
                registerItemOnServer(category, name, price)
            } else {
                Toast.makeText(this, "가격을 올바르게 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerItemOnServer(categoryName: String, itemName: String, price: Long) {
        val userId = sharedPreferences.getInt("userId", -1)

        if (userId != -1) {
            // 요청 바디를 생성합니다. imageUrl은 빈 문자열로 설정합니다.
            val requestBody = ItemRegisterRequest(
                itemName = itemName,
                categoryName = categoryName,
                imageUrl = "",  // 이미지 URL이 없으므로 빈 문자열로 처리
                price = price
            )

            RetrofitInstances.fleaMarketApiService.registerItem(userId, requestBody)
                .enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.isSuccessful) {
                            if (!isFinishing && !isDestroyed) {
                                Log.d("Response Body", response.body().toString())
                                Toast.makeText(this@AddItemActivity, "항목이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        } else {
                            if (!isFinishing && !isDestroyed) {
                                Toast.makeText(this@AddItemActivity, "항목 등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        if (!isFinishing && !isDestroyed) {
                            Toast.makeText(this@AddItemActivity, "에러: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }
        else {
            Toast.makeText(this, "유효하지 않은 사용자 ID입니다.", Toast.LENGTH_SHORT).show()
        }
    }
}

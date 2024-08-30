package com.example.shinhantime.activity

import FleaMarketFragment
import FleaMarketViewModel
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import com.example.shinhantime.R
import com.example.shinhantime.fragment.FindingWithAroundFragment
import com.example.shinhantime.fragment.PasswordFragment

class FleaMarketActivity : AppCompatActivity() {

    private val viewModel: FleaMarketViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fleamarket)

        // 총 합계 텍스트뷰
        val totalPriceTextView = findViewById<TextView>(R.id.text_total_price)

        // 총 합계 값 관찰 및 UI 업데이트
        viewModel.totalPrice.observe(this, Observer { totalPrice ->
            totalPriceTextView.text = "총 합계: ${totalPrice}원"
        })

        val buttonAccessory = findViewById<Button>(R.id.button_accessory)
        val buttonBooks = findViewById<Button>(R.id.button_book)
        val buttonTrees = findViewById<Button>(R.id.button_tree)
        val buttonPayment = findViewById<Button>(R.id.button_payment)

        // 버튼 클릭 시 visibility 상태 토글
        buttonAccessory.setOnClickListener {
            println("ACCESSORY BUTTON")
            viewModel.toggleAccessoryVisibility()
        }

        buttonBooks.setOnClickListener {
            println("BOOK BUTTON")
            viewModel.toggleBooksVisibility()
        }

        buttonTrees.setOnClickListener {
            println("Tree BUTTON")
            viewModel.toggleTreesVisibility()
        }

        buttonPayment.setOnClickListener {
            val intent = Intent(this, LoadingActivity::class.java)
            intent.putExtra("pageName", "FleaMarketActivity")
            intent.putExtra("fragmentName", "SendWitch")
            intent.putExtra("loadType", "receive")
            startActivity(intent)
            finish()
        }

        // 프래그먼트 초기화
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, FleaMarketFragment())
            .commit()
    }

    override fun onBackPressed() {
        finish()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }
}
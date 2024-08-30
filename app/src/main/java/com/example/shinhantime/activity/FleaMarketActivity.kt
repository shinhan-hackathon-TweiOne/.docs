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
            intent.putExtra("pageName", "LoadingActivity")
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
}
// 과거의 잔재
//class FleaMarketActivity : AppCompatActivity() {
//
//    private val viewModel: FleaMarketViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_fleamarket)
//
//        findViewById<Button>(R.id.button_home).setOnClickListener {
//            startActivity(Intent(this, MainActivity::class.java))
//        }
//
//        findViewById<Button>(R.id.button_accessory).setOnClickListener {
//            toggleCategory(findViewById<Button>(R.id.button_accessory),"악세서리")
//        }
//
//        findViewById<Button>(R.id.button_book).setOnClickListener {
//            toggleCategory(findViewById<Button>(R.id.button_book), "책")
//        }
//
//        findViewById<Button>(R.id.button_tree).setOnClickListener {
//            toggleCategory(findViewById<Button>(R.id.button_tree), "나무")
//        }
//
//        // Fragment 생성 및 데이터 전달
//        val fragment = FindingWithAroundFragment().apply {
//            arguments = Bundle().apply {
//                putString("pageName", "FleaMarketActivity") // 원하는 데이터를 추가
//            }
//        }
//
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, fragment)
//            .commit()
//
//        // WindowInsets 처리 ( 필요 없는 경우 삭제 가능 )
//        val rootView = findViewById<View>(R.id.fragment_container)
//        rootView.setOnApplyWindowInsetsListener { view, insets ->
//            val navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
//            view.updatePadding(bottom = navigationBarHeight)
//            insets
//        }
//    }
//
//    private fun toggleCategory(btn: Button, category: String) {
//        if (viewModel.selectedCategories.value?.contains(category) == true) {
//            // 배경 변경
//            btn.setBackgroundColor(Color.TRANSPARENT) // 투명한 배경 설정
//            btn.setTextColor(Color.parseColor("#606060"))
//            viewModel.removeCategory(category)
//        } else {
//            // 배경 변경
//            btn.setBackgroundResource(R.drawable.small_rounded_gray_button)
//            btn.setTextColor(Color.parseColor("#000000"))
//            viewModel.addCategory(category)
//        }
//    }
//}
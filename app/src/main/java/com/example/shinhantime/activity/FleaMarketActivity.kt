package com.example.shinhantime.activity

import FleaMarketFragment
import FleaMarketViewModel
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.shinhantime.R

class FleaMarketActivity : AppCompatActivity() {

    private val viewModel: FleaMarketViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fleamarket)

        val totalPriceTextView = findViewById<TextView>(R.id.text_total_price)

        viewModel.totalPrice.observe(this, Observer { totalPrice ->
            totalPriceTextView.text = "총 합계: ${totalPrice}원"
        })

        val buttonPayment = findViewById<Button>(R.id.button_payment)
        buttonPayment.setOnClickListener {
            val intent = Intent(this, LoadingActivity::class.java)
            intent.putExtra("pageName", "FleaMarketActivity")
            intent.putExtra("fragmentName", "SendWitch")
            intent.putExtra("loadType", "receive")
            startActivity(intent)
            finish()
        }

        val categoryButtonsLayout = findViewById<LinearLayout>(R.id.category_buttons)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, FleaMarketFragment())
            .commit()

        viewModel.categories.observe(this, Observer { categories ->
            categoryButtonsLayout.removeAllViews()

            categories.forEach { category ->
                val buttonView = LayoutInflater.from(this)
                    .inflate(R.layout.component_fleamarket_category_button, categoryButtonsLayout, false)

                val button = buttonView.findViewById<Button>(R.id.button_category)
                button.text = category.name

                button.setOnClickListener {
                    viewModel.setCategoryVisibility(category.id)
                }

                categoryButtonsLayout.addView(buttonView)
            }
        })
    }

    override fun onBackPressed() {
        finish()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }
}

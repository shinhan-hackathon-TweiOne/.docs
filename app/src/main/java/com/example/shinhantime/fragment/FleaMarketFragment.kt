package com.example.shinhantime.activity

import FleaMarketViewModel
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.shinhantime.Item
import com.example.shinhantime.R
import com.example.shinhantime.Category

class FleaMarketFragment : Fragment() {

    private val viewModel: FleaMarketViewModel by activityViewModels()

    private lateinit var categoryButtonsLayout: LinearLayout
    private lateinit var fragmentContainer: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fleamarket, container, false)
        categoryButtonsLayout = view.findViewById(R.id.category_buttons)
        fragmentContainer = view.findViewById(R.id.fragment_container)

        // 초기 추가 버튼을 추가
        addInitialAddButton()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel에서 카테고리 데이터를 관찰하고 업데이트
        viewModel.categories.observe(viewLifecycleOwner, Observer { categories ->
            // 로그 추가
            println("Categories updated: ${categories.size}")
            displayCategories(categories)
        })
    }

    private fun addInitialAddButton() {
        val addButtonView = LayoutInflater.from(context)
            .inflate(R.layout.component_fleamarket_add_button, fragmentContainer, false)

        fragmentContainer.addView(addButtonView)

        fragmentContainer.findViewById<Button>(R.id.button_add).setOnClickListener {
            val intent = Intent(requireContext(), AddItemActivity::class.java)
            startActivity(intent)
        }
    }

    private fun displayCategories(categories: List<Category>) {
        // 이전에 추가된 모든 뷰를 제거
        categoryButtonsLayout.removeAllViews()
        fragmentContainer.removeAllViews()

        categories.forEach { category ->
            // 카테고리 버튼 추가
            val buttonView = LayoutInflater.from(context)
                .inflate(R.layout.component_fleamarket_category_button, categoryButtonsLayout, false) as LinearLayout

            val button = buttonView.findViewById<Button>(R.id.button_add)
            button.text = category.name
            button.setOnClickListener {
                toggleCategoryVisibility(category.id)
            }

            categoryButtonsLayout.addView(buttonView)

            // 카테고리 프레임 추가
            val frameView = LayoutInflater.from(context)
                .inflate(R.layout.component_fleamarket_frame, fragmentContainer, false)

            val frameContent = frameView.findViewById<GridLayout>(R.id.layout_items)
            frameView.tag = "category_${category.id}"

            category.items.forEach { item ->
                val itemView = LayoutInflater.from(context)
                    .inflate(R.layout.component_fleamarket_item, frameContent, false)

                val itemNameTextView = itemView.findViewById<TextView>(R.id.text_item_name)
                val itemPriceTextView = itemView.findViewById<TextView>(R.id.text_item_price)

                itemNameTextView.text = item.name
                itemPriceTextView.text = "${item.price}원"

                frameContent.addView(itemView)
            }

            fragmentContainer.addView(frameView)
        }

        // 기본으로 첫 번째 카테고리만 표시
        if (categories.isNotEmpty()) {
           // toggleCategoryVisibility(categories[0].id)
        }
    }

    // 선택된 카테고리의 항목만 표시하는 함수
    private fun toggleCategoryVisibility(categoryId: Int) {
        for (i in 0 until fragmentContainer.childCount) {
            val child = fragmentContainer.getChildAt(i)
            child.visibility = if (child.tag == "category_$categoryId") View.VISIBLE else View.GONE
        }
    }
}
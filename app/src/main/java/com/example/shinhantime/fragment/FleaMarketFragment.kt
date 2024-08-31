import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.shinhantime.Item
import com.example.shinhantime.R
import com.example.shinhantime.Category
import com.example.shinhantime.activity.ConfirmActivity

class FleaMarketFragment : Fragment() {

    private val viewModel: FleaMarketViewModel by activityViewModels()

    private lateinit var categoryButtonsLayout: LinearLayout
    private lateinit var fragmentContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fleamarket, container, false)
        categoryButtonsLayout = view.findViewById(R.id.category_buttons)
        fragmentContainer = view.findViewById(R.id.fragment_container)

        // 여기서 추가 버튼을 먼저 프래그먼트 컨테이너에 추가합니다.
        addInitialAddButton()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.categories.observe(viewLifecycleOwner, Observer { categories ->
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
        categoryButtonsLayout.removeAllViews()
        fragmentContainer.removeAllViews()

        categories.forEach { category ->
            // 카테고리 버튼 추가
            val button = LayoutInflater.from(context)
                .inflate(R.layout.component_fleamarket_category_button, categoryButtonsLayout, false) as Button
            button.text = category.name

            button.setOnClickListener {
                toggleCategoryVisibility(category.id)
            }

            categoryButtonsLayout.addView(button)

            // 카테고리별 프레임 및 아이템 추가
            val frameView = LayoutInflater.from(context)
                .inflate(R.layout.component_fleamarket_frame, fragmentContainer, false)

            val frameContent = frameView.findViewById<GridLayout>(R.id.layout_items)
            frameView.tag = "category_${category.id}"  // 프레임에 태그를 추가하여 나중에 참조 가능하게 함

            category.items.forEach { item ->
                val itemView = LayoutInflater.from(context)
                    .inflate(R.layout.component_fleamarket_item, frameContent, false)

                val itemNameTextView = itemView.findViewById<TextView>(R.id.text_item_name)
                val itemPriceTextView = itemView.findViewById<TextView>(R.id.text_item_price)
                val itemImageView = itemView.findViewById<ImageView>(R.id.image_item)

                itemNameTextView.text = item.name
                itemPriceTextView.text = "${item.price}원"

                Glide.with(this)
                    .load(item.imageUrl)
                    .into(itemImageView)

                frameContent.addView(itemView)
            }

            fragmentContainer.addView(frameView)
        }
    }

    private fun toggleCategoryVisibility(categoryId: Int) {
        for (i in 0 until fragmentContainer.childCount) {
            val child = fragmentContainer.getChildAt(i)
            child.visibility = if (child.tag == "category_$categoryId") View.VISIBLE else View.GONE
        }
    }
}

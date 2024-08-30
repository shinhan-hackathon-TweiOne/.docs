import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.shinhantime.R

class FleaMarketFragment : Fragment() {

    private val viewModel: FleaMarketViewModel by activityViewModels()

    private val itemList: List<Pair<String, String>> = listOf(
        // 악세서리
        Pair("귀걸이", "earring"),
        Pair("목걸이", "necklace"),
        Pair("반지", "ring"),
        Pair("금팔찌", "goldenchain"),
        // 책
        Pair("사랑 이야기", "lovestory"),
        Pair("경제 이야기", "economystory"),
        Pair("문학 이야기", "literaturestory"),
        Pair("범죄 이야기", "crimestory"),
        // 나무 분재
        Pair("소나무", "pine"),
        Pair("철쭉", "royalazalea"),
        Pair("감나무", "persimmon"),
        Pair("단풍나무", "maple")
    )

    private lateinit var layoutAccessory: View
    private lateinit var layoutBooks: View
    private lateinit var layoutTrees: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // fragment view 설정
        val view = inflater.inflate(R.layout.fragment_fleamarket, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutAccessory = view.findViewById(R.id.layout_accessory)
        layoutBooks = view.findViewById(R.id.layout_books)
        layoutTrees = view.findViewById(R.id.layout_trees)

        layoutAccessory.findViewById<TextView>(R.id.text_category_name).text = "악세서리"
        layoutBooks.findViewById<TextView>(R.id.text_category_name).text = "책"
        layoutTrees.findViewById<TextView>(R.id.text_category_name).text = "분재"

        // 관찰자 설정: accessory visibility 변경
        viewModel.accessoryVisibility.observe(viewLifecycleOwner, Observer { visibility ->
            layoutAccessory.visibility = visibility ?: View.VISIBLE
        })

        // 관찰자 설정: books visibility 변경
        viewModel.booksVisibility.observe(viewLifecycleOwner, Observer { visibility ->
            layoutBooks.visibility = visibility ?: View.VISIBLE
        })

        // 관찰자 설정: trees visibility 변경
        viewModel.treesVisibility.observe(viewLifecycleOwner, Observer { visibility ->
            layoutTrees.visibility = visibility ?: View.VISIBLE
        })

        setInit()

    }

    private fun setInit() {
        for (i in 0..11) {
            // i 값을 기준으로 레이아웃을 선택
            val layout = when (i) {
                in 0..3 -> layoutAccessory
                in 4..7 -> layoutBooks
                in 8..11 -> layoutTrees
                else -> throw IllegalStateException("Unexpected value: $i")
            }

            // 동적으로 항목 ID를 참조하여 접근
            val resID = resources.getIdentifier("item_${i % 4 + 1}", "id", requireContext().packageName)
            val itemLayout = layout.findViewById<View>(resID)

            // 항목 이름과 가격을 참조
            val itemImageView = itemLayout.findViewById<ImageView>(R.id.image_item)
            val itemNameTextView = itemLayout.findViewById<TextView>(R.id.text_item_name)
            val itemPriceTextView = itemLayout.findViewById<TextView>(R.id.text_item_price)

            val quantityLayout = itemLayout.findViewById<ConstraintLayout>(R.id.layout_quantity)
            val quantityTextView = itemLayout.findViewById<TextView>(R.id.text_quantity)
            val decreaseButton = itemLayout.findViewById<Button>(R.id.button_decrease)
            val increaseButton = itemLayout.findViewById<Button>(R.id.button_increase)

            val itemName = itemList[i].first
            val itemImageName = itemList[i].second

            // Glide를 사용하여 이미지 로드
            val imageResID = resources.getIdentifier(itemImageName, "drawable", requireContext().packageName)
            Glide.with(this)
                .load(imageResID)
                .into(itemImageView)

            // 예시로 이름과 가격 설정
            itemNameTextView.text = itemName
            val itemPrice = viewModel.itemPrices[itemName] ?: 0
            itemPriceTextView.text = "${itemPrice}원"

            // 수량 조절 레이아웃 초기 상태는 GONE
            quantityLayout.visibility = View.GONE

            // 항목 클릭 시 가격 업데이트 처리
            itemLayout.setOnClickListener {
                if (quantityLayout.visibility == View.GONE) {
                    quantityLayout.visibility = View.VISIBLE
                    viewModel.selectItem(itemName)
                    quantityTextView.text = "1"
                }
            }

            // 증가 버튼 클릭 이벤트
            increaseButton.setOnClickListener {
                viewModel.increaseItemQuantity(itemName)
            }

            // 감소 버튼 클릭 이벤트
            decreaseButton.setOnClickListener {
                viewModel.decreaseItemQuantity(itemName)
                val currentQuantity = viewModel.itemQuantities.value?.get(itemName) ?: 0
                if (currentQuantity == 0) {
                    quantityLayout.visibility = View.GONE
                }
            }

            // 수량 변경 관찰하여 업데이트
            viewModel.itemQuantities.observe(viewLifecycleOwner, Observer { quantities ->
                val quantity = quantities[itemName] ?: 0
                if (quantity > 0) {
                    quantityTextView.text = quantity.toString()
                } else {
                    quantityLayout.visibility = View.GONE
                }
            })
        }
    }
}

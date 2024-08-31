import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FleaMarketViewModel : ViewModel() {

    // 아이템 가격 정보를 저장하는 맵
    val itemPrices = mapOf(
        "귀걸이" to 30000,
        "목걸이" to 20000,
        "반지" to 15000,
        "금팔찌" to 50000,
        
        "사랑 이야기" to 20000,
        "경제 이야기" to 40000,
        "문학 이야기" to 15000,
        "범죄 이야기" to 50000,
        
        "소나무" to 150000,
        "철쭉" to 20000,
        "감나무" to 100000,
        "단풍나무" to 80000
    )

    // 아이템 수량 정보를 저장하는 MutableLiveData
    private val _itemQuantities = MutableLiveData<MutableMap<String, Int>>(mutableMapOf())
    val itemQuantities: LiveData<MutableMap<String, Int>> get() = _itemQuantities

    // 총 가격을 저장하는 MutableLiveData
    private val _totalPrice = MutableLiveData<Int>(0)
    val totalPrice: LiveData<Int> get() = _totalPrice

    // 아이템 선택 시 호출되는 메서드
    fun selectItem(itemName: String) {
        val quantities = _itemQuantities.value ?: mutableMapOf()
        if (!quantities.containsKey(itemName)) {
            quantities[itemName] = 1
        }
        _itemQuantities.value = quantities
        calculateTotalPrice()
    }

    // 아이템 수량 증가
    fun increaseItemQuantity(itemName: String) {
        val quantities = _itemQuantities.value ?: mutableMapOf()
        val currentQuantity = quantities[itemName] ?: 0
        quantities[itemName] = currentQuantity + 1
        _itemQuantities.value = quantities
        calculateTotalPrice()
    }

    // 아이템 수량 감소
    fun decreaseItemQuantity(itemName: String) {
        val quantities = _itemQuantities.value ?: mutableMapOf()
        val currentQuantity = quantities[itemName] ?: 0
        if (currentQuantity > 1) {
            quantities[itemName] = currentQuantity - 1
        } else {
            quantities.remove(itemName)
        }
        _itemQuantities.value = quantities
        calculateTotalPrice()
    }

    // 총 가격 계산
    private fun calculateTotalPrice() {
        val quantities = _itemQuantities.value ?: return
        var total = 0
        for ((itemName, quantity) in quantities) {
            val pricePerItem = itemPrices[itemName] ?: 0
            total += pricePerItem * quantity
        }
        _totalPrice.value = total
    }

    init {
        _totalPrice.value = 0
    }

    private val _accessoryVisibility = MutableLiveData<Int>()
    val accessoryVisibility: LiveData<Int> get() = _accessoryVisibility

    private val _booksVisibility = MutableLiveData<Int>()
    val booksVisibility: LiveData<Int> get() = _booksVisibility

    private val _treesVisibility = MutableLiveData<Int>()
    val treesVisibility: LiveData<Int> get() = _treesVisibility

    init {
        // Default visibility state
        _accessoryVisibility.value = View.VISIBLE
        _booksVisibility.value = View.VISIBLE
        _treesVisibility.value = View.VISIBLE
    }

    // Methods to toggle visibility
    fun toggleAccessoryVisibility() {
        _accessoryVisibility.value = if (_accessoryVisibility.value == View.VISIBLE) View.GONE else View.VISIBLE
        println("CHANGE")
        println(_accessoryVisibility.value)
    }

    fun toggleBooksVisibility() {
        _booksVisibility.value = if (_booksVisibility.value == View.VISIBLE) View.GONE else View.VISIBLE

    }

    fun toggleTreesVisibility() {
        _treesVisibility.value = if (_treesVisibility.value == View.VISIBLE) View.GONE else View.VISIBLE
    }
}

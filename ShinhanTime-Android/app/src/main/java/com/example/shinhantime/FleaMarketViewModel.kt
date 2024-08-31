import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shinhantime.Category
import com.example.shinhantime.Item
import com.example.shinhantime.RetrofitInstances
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FleaMarketViewModel : ViewModel() {

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    private val _filteredItems = MutableLiveData<List<Item>>()
    val filteredItems: LiveData<List<Item>> get() = _filteredItems

    private val _itemQuantities = MutableLiveData<MutableMap<String, Int>>(mutableMapOf())
    val itemQuantities: LiveData<MutableMap<String, Int>> get() = _itemQuantities

    private val _totalPrice = MutableLiveData<Int>(0)
    val totalPrice: LiveData<Int> get() = _totalPrice

    init {
        _totalPrice.value = 0
        fetchCategoriesAndItems(userId = 1)  // 예시로 userId = 1 사용
    }

    // 서버에서 카테고리와 아이템 데이터를 가져오는 함수
    private fun fetchCategoriesAndItems(userId: Int) {
        RetrofitInstances.fleaMarketApiService.getCategoriesAndItems(userId)
            .enqueue(object : Callback<List<Category>> {
                override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                    if (response.isSuccessful) {
                        _categories.value = response.body()

                        // 첫 번째 카테고리의 아이템을 초기 선택 상태로 설정
                        response.body()?.let {
                            if (it.isNotEmpty()) {
                                setCategoryVisibility(it[0].id)
                            }
                        }
                    } else {
                        // 에러 처리
                    }
                }

                override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                    // 네트워크 오류 등 처리
                }
            })
    }

    fun setCategoryVisibility(categoryId: Int) {
        val selectedCategoryItems = _categories.value?.find { it.id == categoryId }?.items
        _filteredItems.value = selectedCategoryItems ?: emptyList()
    }

    fun selectItem(itemName: String) {
        val quantities = _itemQuantities.value ?: mutableMapOf()
        if (!quantities.containsKey(itemName)) {
            quantities[itemName] = 1
        }
        _itemQuantities.value = quantities
        calculateTotalPrice()
    }

    fun increaseItemQuantity(itemName: String) {
        val quantities = _itemQuantities.value ?: mutableMapOf()
        val currentQuantity = quantities[itemName] ?: 0
        quantities[itemName] = currentQuantity + 1
        _itemQuantities.value = quantities
        calculateTotalPrice()
    }

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

    private fun calculateTotalPrice() {
        val quantities = _itemQuantities.value ?: return
        var total = 0.0
        for ((itemName, quantity) in quantities) {
            val pricePerItem = _filteredItems.value?.find { it.name == itemName }?.price ?: 0.0
            total += pricePerItem * quantity
        }
        _totalPrice.value = total.toInt()
    }
}

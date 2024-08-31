import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.shinhantime.ItemRegisterRequest
import com.example.shinhantime.R
import com.example.shinhantime.RegisterItemResponse
import com.example.shinhantime.RetrofitInstances
import com.example.shinhantime.UploadResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class AddItemActivity : AppCompatActivity() {

    private lateinit var editTextCategory: EditText
    private lateinit var editTextName: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var buttonSelectImage: Button
    private lateinit var imageViewPreview: ImageView
    private lateinit var buttonAddItem: Button

    private lateinit var sharedPreferences: SharedPreferences

    private var selectedImageUri: Uri? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        editTextCategory = findViewById(R.id.edittext_category)
        editTextName = findViewById(R.id.edittext_name)
        editTextPrice = findViewById(R.id.edittext_price)
        buttonSelectImage = findViewById(R.id.button_select_image)
        imageViewPreview = findViewById(R.id.imageView_preview)
        buttonAddItem = findViewById(R.id.button_add_item)

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        buttonSelectImage.setOnClickListener {
            openImageChooser()
        }

        buttonAddItem.setOnClickListener {
            val category = editTextCategory.text.toString()
            val name = editTextName.text.toString()
            val price = editTextPrice.text.toString().toIntOrNull()

            if (selectedImageUri != null && price != null) {
                uploadImageToServer(selectedImageUri!!, name)
            }
        }
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            imageViewPreview.setImageURI(selectedImageUri)
            imageViewPreview.visibility = View.VISIBLE
        }
    }

    private fun uploadImageToServer(uri: Uri, keyName: String) {
        val file = uriToFile(uri)
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)
        val bucketNamePart = RequestBody.create("text/plain".toMediaTypeOrNull(), "tweione-bucket")
        val keyNamePart = RequestBody.create("text/plain".toMediaTypeOrNull(), keyName)

        RetrofitInstances.s3ApiService.uploadImage(filePart, bucketNamePart, keyNamePart)
            .enqueue(object : Callback<UploadResponse> {
                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                    if (response.isSuccessful) {
                        val imageUrl = response.body()?.url
                        Toast.makeText(this@AddItemActivity, "Image uploaded successfully: $imageUrl", Toast.LENGTH_SHORT).show()
                        imageUrl?.let { registerItemOnServer(it) }
                    } else {
                        Toast.makeText(this@AddItemActivity, "Failed to upload image", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    Toast.makeText(this@AddItemActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun registerItemOnServer(imageUrl: String) {
        val categoryName = editTextCategory.text.toString()
        val itemName = editTextName.text.toString()
        val price = editTextPrice.text.toString().toIntOrNull()

        if (price != null) {
            val requestBody = ItemRegisterRequest(
                itemName = itemName,
                categoryName = categoryName,
                imageUrl = imageUrl,
                price = price
            )

            val userId = sharedPreferences.getInt("userId", -1)
            if (userId != -1) {
                RetrofitInstances.fleaMarketApiService.registerItem(userId, requestBody)
                    .enqueue(object : Callback<RegisterItemResponse> {
                        override fun onResponse(call: Call<RegisterItemResponse>, response: Response<RegisterItemResponse>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@AddItemActivity, "Item registered successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@AddItemActivity, "Failed to register item", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<RegisterItemResponse>, t: Throwable) {
                            Toast.makeText(this@AddItemActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            } else {
                Toast.makeText(this, "Invalid userId", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uriToFile(uri: Uri): File {
        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        val file = File(cacheDir, "image.jpg")
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.close()
        return file
    }
}

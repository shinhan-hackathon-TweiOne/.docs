package com.example.shinhantime.fragment

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.shinhantime.AuthRequest
import com.example.shinhantime.AuthResponse
import com.example.shinhantime.R
import com.example.shinhantime.RetrofitInstances
import com.example.shinhantime.VerifyAuthRequest
import com.example.shinhantime.VerifyAuthResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpInputInformationFragment : Fragment() {

    private lateinit var spinnerCarrier: Spinner
    private lateinit var buttonRequestCode: Button
    private lateinit var phoneNumber: EditText
    private lateinit var idNumber: EditText
    private lateinit var name: EditText
    private lateinit var layoutVerification: ConstraintLayout
    private lateinit var buttonConfirm: Button
    private lateinit var buttonEnroll: Button
    private lateinit var verificationCode: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up_input_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonRequestCode = view.findViewById(R.id.button_code_request)
        phoneNumber = view.findViewById(R.id.edit_phone_number)
        idNumber = view.findViewById(R.id.edit_id_number)
        name = view.findViewById(R.id.edit_name)
        layoutVerification = view.findViewById(R.id.layout_verification)
        buttonConfirm = view.findViewById(R.id.button_verification)
        buttonEnroll = view.findViewById(R.id.button_enroll)
        spinnerCarrier = view.findViewById(R.id.spinner_carrier)
        verificationCode = view.findViewById(R.id.edit_verification_code)


        setupSpinner()
        setupListeners()
    }

    private fun setupSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.carrier_array,
            R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCarrier.adapter = adapter
        }
    }

    private fun setupListeners() {
        // 통신사와 전화번호 입력 시 인증번호 요청 버튼 활성화
        val phoneNumberWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val phoneNumberText = phoneNumber.text.toString()
                // "-" 없이 숫자만으로 이루어진 형식, 앞 세 자리가 010인지 확인
                val phonePattern = Regex("^010\\d{8}$")
                val isValidPhoneNumber = phonePattern.matches(phoneNumberText)
                buttonRequestCode.isEnabled = spinnerCarrier.selectedItemPosition != 0 && isValidPhoneNumber
                buttonRequestCode.setBackgroundResource(
                    if (buttonRequestCode.isEnabled)
                        R.drawable.button_background_enabled
                    else
                        R.drawable.button_background_disabled
                )
                buttonRequestCode.setTextColor(
                    if (buttonRequestCode.isEnabled)
                        Color.parseColor("#ffffff")
                    else
                        Color.parseColor("#000000")
                )
                updateEnrollButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        phoneNumber.addTextChangedListener(phoneNumberWatcher)
        idNumber.addTextChangedListener(genericWatcher)
        name.addTextChangedListener(genericWatcher)
        verificationCode.addTextChangedListener(genericWatcher)

        spinnerCarrier.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                phoneNumberWatcher.afterTextChanged(null)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        buttonRequestCode.setOnClickListener {
            spinnerCarrier.isEnabled = false
            phoneNumber.isEnabled = false
            layoutVerification.visibility = View.VISIBLE
            buttonRequestCode.visibility = View.INVISIBLE

            requestAuthCode()
        }

        buttonConfirm.setOnClickListener {
            verifyAuthCodeAndSaveUserData()
        }

        buttonEnroll.setOnClickListener {
            enrollUserServer()
        }
    }

    private fun enrollUserServer() {

    }

    private fun requestAuthCode() {
        // 여기서 서버로 인증번호 요청을 합니다. (/auth 엔드포인트 호출)
        val phoneNumberText = phoneNumber.text.toString()
        val nameText = name.text.toString()

        val request = VerifyAuthRequest(
            phoneNumber = phoneNumberText,
            name = nameText,
            authCode = "" // 인증 요청 시에는 authCode가 필요 없습니다.
        )

        // Retrofit API 호출
        val authRequest = AuthRequest(phoneNumber = phoneNumberText)

        println("전화번호 : " + phoneNumberText)

        RetrofitInstances.userApiService.requestAuthCode(authRequest).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    // 인증번호가 성공적으로 요청되었음을 사용자에게 알림
                    // UI 갱신 등을 처리
                    println("성공 !!")
                } else {
                    // 실패 처리
                    // UI에 에러 메시지 표시
                    println("실패 !!")
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                // 네트워크 오류 등 처리
                println("네트워크 오류")
            }
        })
    }

    private fun verifyAuthCodeAndSaveUserData() {
        val authCode = verificationCode.text.toString()
        val phoneNumberText = phoneNumber.text.toString()

        val request = VerifyAuthRequest(
            phoneNumber = phoneNumberText,
            name = "ABCD",//name.text.toString(),
            authCode = authCode
        )

        println("인증 성공")

        // Retrofit API 호출
        RetrofitInstances.userApiService.verifyAuthCode(request).enqueue(object : Callback<VerifyAuthResponse> {
            override fun onResponse(call: Call<VerifyAuthResponse>, response: Response<VerifyAuthResponse>) {
                if (response.isSuccessful) {
                    val userId = response.body()?.userDto?.id ?: -1
                    val accessToken = response.body()?.jwtToken?.accessToken ?: ""
                    val refreshToken = response.body()?.jwtToken?.refreshToken ?: ""

                    // 서버에서 받은 데이터 저장
                    saveUserData(requireContext(), userId, accessToken, refreshToken)

                    // 비밀번호 설정 페이지로 이동
                    goInputPassword()
                } else {
                    // 인증 실패 처리
                    // 에러 메시지 표시 등
                }
            }

            override fun onFailure(call: Call<VerifyAuthResponse>, t: Throwable) {
                // 네트워크 오류 등 처리
            }
        })
    }

    private fun goInputPassword() {
        val fragmentManager = parentFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, SignUpLoginPasswordFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private val genericWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            updateEnrollButtonState()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun updateEnrollButtonState() {
        val isVerificationCodeEntered = verificationCode.text.isNotEmpty()
        val isIdNumberEntered = idNumber.text.isNotEmpty()
        val isNameEntered = name.text.isNotEmpty()

        buttonEnroll.isEnabled = isVerificationCodeEntered && isIdNumberEntered && isNameEntered
        buttonEnroll.setBackgroundResource(
            if (buttonEnroll.isEnabled)
                R.drawable.button_background_enabled
            else
                R.drawable.button_background_disabled
        )
        buttonEnroll.setTextColor(
            if (buttonEnroll.isEnabled)
                Color.parseColor("#ffffff")
            else
                Color.parseColor("#000000")
        )
    }

    private fun checkNameAndEnableConfirmButton() {
        lifecycleScope.launch {
            delay(500) // 이름 확인 작업 시뮬레이션
            buttonConfirm.isEnabled = true
            buttonConfirm.setBackgroundColor(Color.parseColor("#0046ff"))
        }
    }

    private fun saveUserData(context: Context, userId: Int, accessToken: String, refreshToken: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putInt("userId", userId)
        editor.putString("accessToken", accessToken)
        editor.putString("refreshToken", refreshToken)
        editor.apply()  // 데이터를 비동기적으로 저장
    }

    private fun loadUserData(context: Context): Map<String, Any?> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        val userId = sharedPreferences.getInt("userId", -1) // 기본값으로 -1을 설정
        val accessToken = sharedPreferences.getString("accessToken", null)
        val refreshToken = sharedPreferences.getString("refreshToken", null)

        return mapOf("userId" to userId, "accessToken" to accessToken, "refreshToken" to refreshToken)
    }
}

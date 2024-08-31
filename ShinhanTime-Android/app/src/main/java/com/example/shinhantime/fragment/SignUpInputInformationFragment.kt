package com.example.shinhantime.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.shinhantime.R
import android.text.Editable
import android.text.TextWatcher
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.example.shinhantime.activity.SignUpActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        buttonConfirm = view.findViewById(R.id.button_confirm)
        buttonEnroll = view.findViewById(R.id.button_enroll)
        spinnerCarrier = view.findViewById(R.id.spinner_carrier)
        verificationCode = view.findViewById(R.id.edit_verification_code)

        // Spinner에 ArrayAdapter 설정
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.carrier_array,
            R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCarrier.adapter = adapter
        }

        // 통신사와 전화번호 입력 시 인증번호 요청 버튼 활성화
        val phoneNumberWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val phoneNumberText = phoneNumber.text.toString()
                val phonePattern = Regex("^\\d{3}-\\d{4}-\\d{4}$")

                // 전화번호가 정확히 "000-0000-0000" 형식일 때만 버튼 활성화
                val isValidPhoneNumber = phonePattern.matches(phoneNumberText)
                buttonRequestCode.isEnabled = spinnerCarrier.selectedItemPosition != 0 && isValidPhoneNumber

                if (buttonRequestCode.isEnabled) {
                    println("SETTING ENABLE")
                    buttonRequestCode.setBackgroundResource(R.drawable.button_background_enabled)
                } else {
                    println("SETTING DISABLE")
                    buttonRequestCode.setBackgroundResource(R.drawable.button_background_disabled)
                }

                // 하단의 인증하기 버튼 활성화 여부 체크
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

        // 인증번호 요청 클릭 시
        buttonRequestCode.setOnClickListener {
            spinnerCarrier.isEnabled = false
            phoneNumber.isEnabled = false
            layoutVerification.visibility = View.VISIBLE
            buttonRequestCode.visibility = View.INVISIBLE
        }

        // 이름 확인 버튼 클릭 시
        buttonConfirm.setOnClickListener {
            checkNameAndEnableConfirmButton()
        }

        // 인증하기 버튼 클릭 시
        buttonEnroll.setOnClickListener {
            // 인증 로직 추가 필요
            goInputPassword()
        }
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
        
        // 실제로는 여기서 전부 다 검사 필요
        // 일단은 한글자라도 들어있으면 넘어감

        buttonEnroll.isEnabled = isVerificationCodeEntered && isIdNumberEntered && isNameEntered

        if (buttonEnroll.isEnabled) {
            buttonEnroll.setBackgroundResource(R.drawable.button_background_enabled)
        } else {
            buttonEnroll.setBackgroundResource(R.drawable.button_background_disabled)
        }
    }

    private fun checkNameAndEnableConfirmButton() {
        // 이름 확인 로직 (데이터베이스 확인 등) - 현재는 항상 성공하도록 설정
        lifecycleScope.launch {
            delay(500) // 이름 확인 작업 시뮬레이션
            buttonConfirm.isEnabled = true
            buttonConfirm.setBackgroundColor(Color.parseColor("#0046ff"))
        }
    }

    private fun goInputPassword() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, SignUpLoginPasswordFragment())
        transaction.commit()
    }
}

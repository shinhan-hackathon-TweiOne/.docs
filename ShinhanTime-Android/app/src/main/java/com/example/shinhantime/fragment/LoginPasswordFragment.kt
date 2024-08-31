package com.example.shinhantime.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.shinhantime.R
import com.example.shinhantime.activity.LoginActivity
import java.util.concurrent.Executor

class PasswordFragment : Fragment() {

    private lateinit var charViews: List<TextView>
    private val password = StringBuilder()
    private val maxPasswordLength = 6

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        charViews = listOf(
            view.findViewById(R.id.char_1),
            view.findViewById(R.id.char_2),
            view.findViewById(R.id.char_3),
            view.findViewById(R.id.char_4),
            view.findViewById(R.id.char_5),
            view.findViewById(R.id.char_6)
        )

        view.findViewById<Button>(R.id.button_1).setOnClickListener { appendDigit("1") }
        view.findViewById<Button>(R.id.button_2).setOnClickListener { appendDigit("2") }
        view.findViewById<Button>(R.id.button_3).setOnClickListener { appendDigit("3") }
        view.findViewById<Button>(R.id.button_4).setOnClickListener { appendDigit("4") }
        view.findViewById<Button>(R.id.button_5).setOnClickListener { appendDigit("5") }
        view.findViewById<Button>(R.id.button_6).setOnClickListener { appendDigit("6") }
        view.findViewById<Button>(R.id.button_7).setOnClickListener { appendDigit("7") }
        view.findViewById<Button>(R.id.button_8).setOnClickListener { appendDigit("8") }
        view.findViewById<Button>(R.id.button_9).setOnClickListener { appendDigit("9") }
        view.findViewById<Button>(R.id.button_0).setOnClickListener { appendDigit("0") }
        view.findViewById<Button>(R.id.button_back).setOnClickListener { clearLastDigit() }

        checkAvailableAuth() // 지문 인증이 가능한지 확인 후 시작

        // SharedPreferences에서 저장된 비밀번호를 가져옵니다.
        val savedPassword = sharedPreferences.getString("userPassword", null)
        if (savedPassword.isNullOrEmpty()) {
            Toast.makeText(context, "저장된 비밀번호가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun appendDigit(digit: String) {
        if (password.length < maxPasswordLength) {
            password.append(digit)
            updatePasswordDisplay()
        }
        if (password.length == maxPasswordLength) {
            validatePassword()
        }
    }

    private fun clearLastDigit() {
        if (password.isNotEmpty()) {
            password.deleteCharAt(password.length - 1)
            updatePasswordDisplay()
        }
    }

    private fun updatePasswordDisplay() {
        for (i in 0 until maxPasswordLength) {
            charViews[i].text = if (i < password.length) "*" else "_"
        }
    }

    private fun validatePassword() {
        val savedPassword = sharedPreferences.getString("userPassword", null)
        if (savedPassword != null && password.toString() == savedPassword) {
            (activity as? LoginActivity)?.onAuthenticated()
        } else {
            Toast.makeText(context, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            password.clear()
            updatePasswordDisplay()
        }
    }

    private fun checkAvailableAuth() {
        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // 지문 인증 가능
                promptBiometricAuth()
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(context, "기기에서 생체 인증을 지원하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(context, "생체 인증 하드웨어를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(context, "생체 인식 정보가 등록되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(context, "생체 인증을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun promptBiometricAuth() {
        val executor: Executor = ContextCompat.getMainExecutor(requireContext())

        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                (activity as? LoginActivity)?.onAuthenticated()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(context, "지문 인증 실패", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(context, "지문 인증 오류: $errString", Toast.LENGTH_SHORT).show()
            }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("지문 인증")
            .setSubtitle("지문을 인식하여 로그인하세요")
            .setNegativeButtonText("취소")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}

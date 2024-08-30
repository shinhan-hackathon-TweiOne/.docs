package com.example.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.shinhantime.R
import com.example.shinhantime.activity.LoadingActivity
import com.example.shinhantime.activity.LoginActivity
import com.example.shinhantime.activity.SignUpActivity

class LoginFragment : Fragment() {

    private lateinit var editPhoneNumber: EditText
    private lateinit var editPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonSignUp: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // View 요소들 초기화
        editPhoneNumber = view.findViewById(R.id.edit_phonenumber)
        editPassword = view.findViewById(R.id.edit_password)
        buttonLogin = view.findViewById(R.id.button_login)
        buttonSignUp = view.findViewById(R.id.button_sign_up)

        // 로그인 버튼 클릭 리스너 설정
        buttonLogin.setOnClickListener {
            handleLogin()
        }

        // 회원가입 버튼 클릭 리스너 설정
        buttonSignUp.setOnClickListener {
            // SignUpFragment로 이동하는 NavController 설정 (Navigation 사용 시)
            goSignUp()
        }

        return view
    }

    private fun goSignUp() {
        val intent = Intent(requireContext(), SignUpActivity::class.java)
        startActivity(intent)
    }

    // 로그인 버튼 클릭 시 처리 로직
    private fun handleLogin() {
        val phoneNumber = editPhoneNumber.text.toString()
        val password = editPassword.text.toString()

        // 전화번호, 비밀번호 검증
        if (phoneNumber.isNotEmpty() && password.isNotEmpty()) {
            // 임시 전화번호, 비밀번호
            // 맞추면 로그인 성공, 메인 페이지로 이동
            if (phoneNumber == "01012345678" && password == "123456") {
                // 이 방식이 나을까 아니면 여기서 바로 화면 전환해버리는게 나을까
                (activity as? LoginActivity)?.onAuthenticated()
            }
        }

        return

        // 간단한 유효성 검사를 추가할 수 있습니다.
//        if (phoneNumber.isEmpty()) {
//            Toast.makeText(requireContext(), "휴대폰 번호를 입력하세요.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if (password.isEmpty()) {
//            Toast.makeText(requireContext(), "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        // 이 부분에서 DB를 사용한 로그인 검증 로직을 추가할 수 있습니다.
//        Toast.makeText(requireContext(), "로그인 시도 중...", Toast.LENGTH_SHORT).show()
    }
}

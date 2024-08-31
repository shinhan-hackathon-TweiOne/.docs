package com.example.shinhantime.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.shinhantime.Account
import com.example.shinhantime.R
import com.example.shinhantime.RetrofitInstances
import com.example.shinhantime.SendOneWonRequest
import com.example.shinhantime.SendOneWonResponse
import com.example.shinhantime.VerifyAuthResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainAccountNoneFragment : Fragment() {

    private lateinit var constraintLayoutAccount: ConstraintLayout
    private lateinit var constraintLayoutAccountVerification: ConstraintLayout
    private lateinit var buttonEnroll: Button
    private lateinit var buttonSubmit: Button
    private lateinit var editTextAccount: EditText
    private lateinit var editTextBank: EditText
    private lateinit var editTextVerification: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_acouunt_none, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        constraintLayoutAccount = view.findViewById(R.id.constraint_layout_account)
        constraintLayoutAccountVerification = view.findViewById(R.id.constraint_layout_account_verification)
        buttonEnroll = view.findViewById(R.id.button_enroll)
        buttonSubmit = view.findViewById(R.id.button_submit)
        editTextAccount = view.findViewById(R.id.edittext_account)
        editTextBank = view.findViewById(R.id.edittext_bank)
        editTextVerification = view.findViewById(R.id.edittext_verification)

        // 등록하기 버튼 클릭 리스너
        buttonEnroll.setOnClickListener {
            constraintLayoutAccount.visibility = View.VISIBLE
        }

        // 계좌번호와 은행명 입력 시 1원 보내기 버튼 활성화
        editTextAccount.addTextChangedListener {
            updateSubmitButtonState()
        }
        editTextBank.addTextChangedListener {
            updateSubmitButtonState()
        }

        // 1원 보내기 버튼 클릭 리스너
        buttonSubmit.setOnClickListener {
            sendOneWon()
        }

        // 인증하기 버튼 클릭 리스너
        view.findViewById<Button>(R.id.button_verify).setOnClickListener {
            verifyAccount()
        }
    }

    private fun updateSubmitButtonState() {
        val isAccountValid = editTextAccount.text.toString().isNotEmpty()
        val isBankValid = editTextBank.text.toString().isNotEmpty()
        buttonSubmit.isEnabled = isAccountValid && isBankValid
        buttonSubmit.setBackgroundResource(if (buttonSubmit.isEnabled) R.drawable.small_rounded_blue_button else R.drawable.small_rounded_gray_button)
    }

    private fun sendOneWon() {
        constraintLayoutAccount.visibility = View.GONE
        constraintLayoutAccountVerification.visibility = View.VISIBLE

        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", -1)
        val accountNo = editTextAccount.text.toString()
        val bankName = editTextBank.text.toString()

        if (userId == -1) {
            Toast.makeText(requireContext(), "유저 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val requestBody = SendOneWonRequest(
            accountNo = accountNo,
            bankName = bankName
        )

        RetrofitInstances.userApiService.sendOneWon(userId, requestBody)
            .enqueue(object : Callback<SendOneWonResponse> {
                override fun onResponse(call: Call<SendOneWonResponse>, response: Response<SendOneWonResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        // 1원 송금 성공 시 처리
                        Toast.makeText(requireContext(), "1원 송금 성공", Toast.LENGTH_SHORT).show()
                        constraintLayoutAccount.visibility = View.GONE
                        constraintLayoutAccountVerification.visibility = View.VISIBLE
                    } else {
                        // 실패 처리
                        Toast.makeText(requireContext(), "1원 송금 실패: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<SendOneWonResponse>, t: Throwable) {
                    // 네트워크 오류 등 처리
                    Toast.makeText(requireContext(), "1원 송금 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun verifyAccount() {
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", -1)
        val accountNo = editTextAccount.text.toString()
        val bankName = editTextBank.text.toString()
        val authCode = editTextVerification.text.toString()

        if (userId == -1) {
            Toast.makeText(requireContext(), "유저 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val requestBody = mapOf(
            "accountNo" to accountNo,
            "bankName" to bankName,
            "authCode" to authCode
        )

        RetrofitInstances.userApiService.verifyAccount(userId, requestBody)
            .enqueue(object : Callback<VerifyAuthResponse> {
                override fun onResponse(call: Call<VerifyAuthResponse>, response: Response<VerifyAuthResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "계좌 인증 성공", Toast.LENGTH_SHORT).show()
                        fetchAccountInfo(userId)
                    } else {
                        Toast.makeText(requireContext(), "계좌 인증 실패", Toast.LENGTH_SHORT).show()
                        constraintLayoutAccountVerification.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<VerifyAuthResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "계좌 인증 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                    constraintLayoutAccountVerification.visibility = View.GONE
                }
            })
    }

    private fun fetchAccountInfo(userId: Int) {
        RetrofitInstances.userApiService.getUserAccounts(userId)
            .enqueue(object : Callback<List<Account>> {
                override fun onResponse(call: Call<List<Account>>, response: Response<List<Account>>) {
                    if (response.isSuccessful) {
                        val accounts = response.body()
                        if (!accounts.isNullOrEmpty()) {
                            val mainAccount = accounts[0] // Assuming the first account is the main account
                            setMainAccount(userId, mainAccount.id)
                        } else {
                            Toast.makeText(requireContext(), "계좌 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "계좌 정보 가져오기 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Account>>, t: Throwable) {
                    Toast.makeText(requireContext(), "계좌 정보 가져오기 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
    private fun setMainAccount(userId: Int, accountId: Int) {
        RetrofitInstances.userApiService.setMainAccount(userId, accountId)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "메인 계좌 설정 성공", Toast.LENGTH_SHORT).show()
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, MainAccountExistFragment())
                            .commit()
                    } else {
                        Toast.makeText(requireContext(), "메인 계좌 설정 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "메인 계좌 설정 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}

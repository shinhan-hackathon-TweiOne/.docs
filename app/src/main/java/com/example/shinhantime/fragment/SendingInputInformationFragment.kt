package com.example.shinhantime.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.shinhantime.R
import com.example.shinhantime.activity.LoadingActivity
import com.example.shinhantime.activity.SendingActivity

class SendingInputInformationFragment : Fragment() {

    private lateinit var textAmount: TextView
    private lateinit var buttonNext: Button
    private lateinit var buttonRemain: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sending_input_information, container, false)

        // UI 요소 초기화
        textAmount = view.findViewById(R.id.text_amount)
        buttonRemain = view.findViewById(R.id.button_remain)
        buttonNext = view.findViewById(R.id.button_next)

        // 여기서 버튼 클릭 리스너 설정
        setupNumberButtonListeners(view)

        view.findViewById<Button>(R.id.button_next).setOnClickListener { onNextButtonClicked()}

        return view
    }

    private fun onNextButtonClicked() {
        val intent = Intent(requireContext(), LoadingActivity::class.java)
        intent.putExtra("pageName", "SendWitch")
        intent.putExtra("loadType", "send")
        startActivity(intent)
    }

    private fun setupNumberButtonListeners(view: View) {
        // 숫자 버튼들 설정
        val numberButtons = listOf(
            view.findViewById<Button>(R.id.button_0),
            view.findViewById<Button>(R.id.button_1),
            view.findViewById<Button>(R.id.button_2),
            view.findViewById<Button>(R.id.button_3),
            view.findViewById<Button>(R.id.button_4),
            view.findViewById<Button>(R.id.button_5),
            view.findViewById<Button>(R.id.button_6),
            view.findViewById<Button>(R.id.button_7),
            view.findViewById<Button>(R.id.button_8),
            view.findViewById<Button>(R.id.button_9)
        )

        // 각 숫자 버튼에 클릭 리스너 설정
        for (button in numberButtons) {
            button.setOnClickListener { onNumberButtonClicked(button, textAmount, buttonRemain, buttonNext) }
        }

        // button_00 및 button_back 클릭 리스너 설정
        view.findViewById<Button>(R.id.button_00).setOnClickListener { onButton00Clicked(view.findViewById<Button>(R.id.button_00), textAmount, buttonRemain, buttonNext) }
        view.findViewById<Button>(R.id.button_back).setOnClickListener { onButtonBackClicked(view.findViewById<Button>(R.id.button_back), textAmount, buttonRemain, buttonNext) }
    }

    private fun onButton00Clicked(
        button: Button,
        textAmount: TextView,
        buttonRemain: Button,
        buttonNext: Button
    ) {
        // 현재 텍스트에서 숫자만 추출하고 "원"을 제거
        val currentText = textAmount.text.toString().replace("[^0-9]".toRegex(), "").trim()

        // "얼마를 보낼까요?" 또는 빈 문자열인 경우 버튼 클릭을 무시
        if (currentText.isNotEmpty() && currentText != "얼마를 보낼까요?") {
            // 현재 금액에 "00"을 추가
            val newAmount = currentText + "00"
            // 새로운 금액에 "원"을 붙여서 텍스트로 표시
            textAmount.text = "$newAmount 원"
            updateButtonStates(newAmount, buttonRemain, buttonNext)
        }
    }

    private fun onButtonBackClicked(
        button: Button,
        textAmount: TextView,
        buttonRemain: Button,
        buttonNext: Button
    ) {
        // 현재 텍스트에서 숫자만 추출하고 "원"을 제거
        val currentText = textAmount.text.toString().replace("[^0-9]".toRegex(), "").trim()

        // "얼마를 보낼까요?" 또는 빈 문자열인 경우 버튼 클릭을 무시
        if (currentText.isNotEmpty() && currentText != "얼마를 보낼까요?") {
            // 마지막 숫자 하나 제거
            val newAmount = if (currentText.length > 1) {
                currentText.dropLast(1)
            } else {
                ""
            }

            // 새로운 금액에 "원"을 붙여서 텍스트로 표시
            if (newAmount.isEmpty()) {
                textAmount.text = "얼마를 보낼까요?"
            } else {
                textAmount.text = "$newAmount 원"
            }

            updateButtonStates(newAmount, buttonRemain, buttonNext)
        }
    }

    private fun onNumberButtonClicked(
        button: Button,
        textAmount: TextView,
        buttonRemain: Button,
        buttonNext: Button
    ) {
        // 현재 텍스트에서 숫자만 추출하고 "원"을 제거
        val currentText = textAmount.text.toString().replace("[^0-9]".toRegex(), "").trim()

        // "얼마를 보낼까요?" 라면, 초기화
        if (currentText == "얼마를 보낼까요?" || currentText.isEmpty()) {
            textAmount.text = ""
        }

        // 현재 금액에 버튼에서 눌린 숫자 추가
        val newAmount = currentText + button.text.toString()

        // 새로운 금액에 "원"을 붙여서 텍스트로 표시
        textAmount.text = "$newAmount 원"

        // 금액 입력에 따라 버튼 활성화 상태 업데이트
        updateButtonStates(newAmount, buttonRemain, buttonNext)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textto = view.findViewById<TextView>(R.id.text_to)
        val imagelogo = view.findViewById<ImageView>(R.id.image_to_logo)

        // 전달된 사용자 정보 받기
        val userName = arguments?.getString("userName")
        val userImageRes = arguments?.getInt("userImage")

        // TextView에 사용자 이름 설정
        if (userName != null) {
            textto.text = userName
        }

        // ImageView에 사용자 이미지 설정
        if (userImageRes != null && userImageRes != -1) {
            imagelogo.setImageResource(userImageRes)
        }

        // UI 요소 연결
        val textAmount = view.findViewById<TextView>(R.id.text_amount)
        val buttonRemain = view.findViewById<Button>(R.id.button_remain)
        val buttonNext = view.findViewById<Button>(R.id.button_next)

        // 초기 상태 설정
        textAmount.text = "얼마를 보낼까요?"
        buttonNext.visibility = View.INVISIBLE

    }

    private fun updateButtonStates(
        amountText: String,
        buttonRemain: Button,
        buttonNext: Button
    ) {
        // 현재 텍스트에서 숫자만 추출하고 "원"을 제거
        val currentAmount = amountText.replace("[^0-9]".toRegex(), "").trim()

        if (currentAmount.isEmpty()) {
            // 텍스트가 없으면 "얼마를 보낼까요?"로 변경
            textAmount.text = "얼마를 보낼까요?"
            // buttonRemain은 활성화, buttonNext는 비활성화
            buttonRemain.visibility = View.VISIBLE
            buttonNext.visibility = View.INVISIBLE // 다음 버튼을 화면에서 숨김
        } else {
            // 숫자가 입력된 경우, 금액에 "원"을 붙여 표시
            textAmount.text = "$currentAmount 원"
            // buttonRemain은 비활성화, buttonNext는 활성화
            buttonNext.visibility = View.VISIBLE // 다음 버튼을 화면에 표시
            buttonRemain.visibility = View.INVISIBLE
        }
    }
}

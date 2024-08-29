package com.example.shinhantime.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.example.shinhantime.R
import com.example.shinhantime.activity.LoadingActivity
import com.example.shinhantime.activity.SendingActivity
import org.w3c.dom.Text

class FindingWithAroundFragment : Fragment() {

    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var centralImage: ImageView
    private val handler = Handler(Looper.getMainLooper())
    private var buttonIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        
        // fragment view 설정
        val view = inflater.inflate(R.layout.fragment_finding_with_around, container, false)

        val pageName = arguments?.getString("pageName").toString()

        if (pageName == "SendingActivity") view.findViewById<TextView>(R.id.text_description).text = "주변 사람을 찾는 중이에요 ..."
        else if (pageName == "FleaMarketActivity") view.findViewById<TextView>(R.id.text_description).text = "주변 가게를 찾는 중이에요 ..."

        constraintLayout = view.findViewById(R.id.constraintLayout)
        centralImage = view.findViewById(R.id.image_me)

        // 우선은 일정 시간마다 추가되는 것처럼 보이게 해놓음
        // 비동기적으로 탐색하는 함수를 돌려야 할듯
        // 플리마켓 버전과 송금 버전의 차이가 거의 없음. 플리마켓은 업종별로 다르게 띄워준다 정도 ? 이것도 뺄 수도 있으니 사실상 같은 기능이라고 보면 될 거 같음
        // 우선은 pageName에 맞춰서 글씨만 바뀌도록 설정 그 외 기능 동일
        startAddingFaceButtons()

        return view
    }

    private fun startAddingFaceButtons() {
        // 3초에 하나씩 추가
        handler.postDelayed(object : Runnable {
            override fun run() {
                addFaceButton(buttonIndex)
                buttonIndex++
                handler.postDelayed(this, 3000)
            }
        }, 3000)
    }

    // 인자로 이름, 얼굴 등을 받아와서 적용해줄 필요가 있음
    private fun addFaceButton(index: Int) {

        val buttonView = layoutInflater.inflate(R.layout.component_sending_with_around_face, null)

        val imageButton = buttonView.findViewById<ImageButton>(R.id.button_face)
        val textView = buttonView.findViewById<TextView>(R.id.text_name)

        // 기본 이미지랑 텍스트 전환
        // 유저 이름, 유저 캐릭터 이미지로 바꿔야 함
        imageButton.setImageResource(R.drawable.face0)
        textView.text = "User $index"

        imageButton.setOnClickListener {
            // Intent에 정보 추가 후 send페이지로 전환할 수 있도록 진행
            val intent = Intent(requireContext(), SendingActivity::class.java)
            intent.putExtra("userName", "User $index")  // 예시로 사용자 이름 전달
            intent.putExtra("userImage", R.drawable.face0)  // 사용자 이미지 전달
            startActivity(intent)
        }

        
        val buttonId = View.generateViewId()
        buttonView.id = buttonId
        constraintLayout.addView(buttonView)

        // constraintlayout에 적용
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        // 위치 계산, 내 캐릭터 주변으로 설정해둠
        val angle = Math.toRadians((index * 60).toDouble())
        val radius = 300

        val centerX = centralImage.x + centralImage.width / 2
        val centerY = centralImage.y + centralImage.height / 2

        val buttonX = centerX + radius * Math.cos(angle)
        val buttonY = centerY + radius * Math.sin(angle)

        constraintSet.connect(buttonId, ConstraintSet.START, constraintLayout.id, ConstraintSet.START, buttonX.toInt())
        constraintSet.connect(buttonId, ConstraintSet.TOP, constraintLayout.id, ConstraintSet.TOP, buttonY.toInt())

        constraintSet.applyTo(constraintLayout)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }
}

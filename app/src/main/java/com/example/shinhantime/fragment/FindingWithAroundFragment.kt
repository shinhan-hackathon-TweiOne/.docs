package com.example.shinhantime.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.shinhantime.R
import com.example.shinhantime.activity.LoadingActivity
import com.example.shinhantime.activity.SendingActivity
import org.w3c.dom.Text

class FindingWithAroundFragment : Fragment() {

    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var centralImage: ImageView
    private val handler = Handler(Looper.getMainLooper())
    private var buttonIndex = 0

    private val categoryMap = mutableMapOf<String, MutableList<View>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // fragment view 설정
        val view = inflater.inflate(R.layout.fragment_finding_with_around, container, false)

        // constraintLayout 초기화
        constraintLayout = view.findViewById(R.id.constraintLayout)
        centralImage = view.findViewById(R.id.image_me)

        val pageName = arguments?.getString("pageName").toString()

//        if (pageName == "SendingActivity") {
//            view.findViewById<TextView>(R.id.text_description).text = "주변 사람을 찾는 중이에요 ..."
//            startAddingFaceButtons()
//        }

        view.findViewById<TextView>(R.id.text_description).text = "주변 사람을 찾는 중이에요 ..."
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

    private fun addFaceButton(index: Int) {
        val buttonView = layoutInflater.inflate(R.layout.component_sending_with_around_face, null)

        val imageButton = buttonView.findViewById<ImageButton>(R.id.button_face)
        val textView = buttonView.findViewById<TextView>(R.id.text_name)

        imageButton.setImageResource(R.drawable.face0)
        textView.text = "User $index"

        imageButton.setOnClickListener {
            activity?.finish()
            val intent = Intent(requireContext(), SendingActivity::class.java)
            intent.putExtra("userName", "User $index")
            intent.putExtra("userImage", R.drawable.face0)
            startActivity(intent)
        }

        val buttonId = View.generateViewId()
        buttonView.id = buttonId
        constraintLayout.addView(buttonView)

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

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



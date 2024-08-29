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

class FindingWithAroundFragment : Fragment() {

    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var centralImage: ImageView
    private val handler = Handler(Looper.getMainLooper())
    private var buttonIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment layout
        val view = inflater.inflate(R.layout.fragment_finding_with_around, container, false)

        constraintLayout = view.findViewById(R.id.constraintLayout)
        centralImage = view.findViewById(R.id.image_me)

        // Start adding face buttons with delay
        startAddingFaceButtons()

        return view
    }

    private fun startAddingFaceButtons() {
        // Start adding buttons with a delay of 3 seconds
        handler.postDelayed(object : Runnable {
            override fun run() {
                Log.d("SendingWithAroundFragment", "Adding face button: $buttonIndex")
                addFaceButton(buttonIndex)
                buttonIndex++
                // Schedule the next button addition after 3 seconds
                handler.postDelayed(this, 3000)
            }
        }, 3000)
    }

    // 인자로 이름, 얼굴 등을 받아와서 적용해줄 필요가 있음
    private fun addFaceButton(index: Int) {
        // Inflate the button component XML
        val buttonView = layoutInflater.inflate(R.layout.component_sending_with_around_face, null)

        // Find Button and TextView and set image and text dynamically
        val imageButton = buttonView.findViewById<ImageButton>(R.id.button_face)
        val textView = buttonView.findViewById<TextView>(R.id.text_name)

        // Example image and text changes
        imageButton.setImageResource(R.drawable.face0)
        textView.text = "User $index"

        imageButton.setOnClickListener {
            // Intent에 정보 추가
            val intent = Intent(requireContext(), SendingActivity::class.java)
            intent.putExtra("userName", "User $index")  // 예시로 사용자 이름 전달
            intent.putExtra("userImage", R.drawable.face0)  // 사용자 이미지 전달
            startActivity(intent)
        }

        // Add the button to the layout
        val buttonId = View.generateViewId()
        buttonView.id = buttonId
        constraintLayout.addView(buttonView)

        // Use ConstraintSet to position the button
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        // Calculate position in a circular pattern
        val angle = Math.toRadians((index * 60).toDouble())
        val radius = 300 // Distance from the center (px)

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
        // Remove any pending posts of Runnable when Fragment view is destroyed
        handler.removeCallbacksAndMessages(null)
    }
}

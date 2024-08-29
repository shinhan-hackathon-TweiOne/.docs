import android.animation.Animator
import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.shinhantime.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoadingSendWitchFragment : Fragment() {

    private lateinit var type: String
    private lateinit var boxes: List<View>
    private val delayMillis = 500L
    private val animationDuration = 500L
    private val boxIds = listOf(
        R.id.box1, R.id.box2, R.id.box3, R.id.box4, R.id.box5, R.id.box6
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loading_sendwitch, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 로드 타입이 뭔지 intent로 받아옴
        type = arguments?.getString("loadType").toString()

        // send에서 넘어온 경우
        if (type == "send")
        {
            // send 용으로 페이지 전환
            view.findViewById<TextView>(R.id.text_action).text = "보내는 중 ..."
            view.findViewById<View>(R.id.box1).alpha = 1.0f
            view.findViewById<View>(R.id.box1).background = ColorDrawable(Color.parseColor("#0046ff"))

            view.findViewById<View>(R.id.box2).alpha = 1.0f
            view.findViewById<View>(R.id.box2).background = ColorDrawable(Color.parseColor("#5CCFD3"))

            view.findViewById<View>(R.id.box3).alpha = 1.0f
            view.findViewById<View>(R.id.box3).background = ColorDrawable(Color.parseColor("#F5B265"))

            view.findViewById<View>(R.id.box4).alpha = 0.5f
            view.findViewById<View>(R.id.box4).background = ColorDrawable(Color.parseColor("#0046ff"))

            view.findViewById<View>(R.id.box5).alpha = 0.5f
            view.findViewById<View>(R.id.box5).background = ColorDrawable(Color.parseColor("#5CCFD3"))

            view.findViewById<View>(R.id.box6).alpha = 0.5f
            view.findViewById<View>(R.id.box6).background = ColorDrawable(Color.parseColor("#F5B265"))
        }
        else if (type == "receive")
        {
            // receive 용으로 전환
            view.findViewById<TextView>(R.id.text_action).text = "받는 중 ..."
            view.findViewById<View>(R.id.box1).alpha = 0.5f
            view.findViewById<View>(R.id.box1).background = ColorDrawable(Color.parseColor("#F5B265"))

            view.findViewById<View>(R.id.box2).alpha = 0.5f
            view.findViewById<View>(R.id.box2).background = ColorDrawable(Color.parseColor("#5CCFD3"))

            view.findViewById<View>(R.id.box3).alpha = 0.5f
            view.findViewById<View>(R.id.box3).background = ColorDrawable(Color.parseColor("#0046ff"))

            view.findViewById<View>(R.id.box4).alpha = 1.0f
            view.findViewById<View>(R.id.box4).background = ColorDrawable(Color.parseColor("#F5B265"))

            view.findViewById<View>(R.id.box5).alpha = 1.0f
            view.findViewById<View>(R.id.box5).background = ColorDrawable(Color.parseColor("#5CCFD3"))

            view.findViewById<View>(R.id.box6).alpha = 1.0f
            view.findViewById<View>(R.id.box6).background = ColorDrawable(Color.parseColor("#0046ff"))

        }
            
        boxes = boxIds.map { view.findViewById<View>(it) }
        startAnimation()
    }

    private fun startAnimation() {
        CoroutineScope(Dispatchers.Main).launch {
            // send일 때는 박스들이 날아가는 것 처럼
            if (type == "send") {
                while (true) {
                    for (i in boxes.indices) {
                        val box = boxes[i]
                        box.visibility = View.VISIBLE

                        val animator =
                            ObjectAnimator.ofFloat(box, "translationY", 0f, -box.height.toFloat())
                                .apply {
                                    duration = animationDuration
                                    interpolator = AccelerateInterpolator()
                                }

                        animator.addListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(p0: Animator) {

                            }

                            override fun onAnimationEnd(p0: Animator) {

                                box.visibility = View.INVISIBLE
                            }

                            override fun onAnimationCancel(p0: Animator) {

                            }

                            override fun onAnimationRepeat(p0: Animator) {

                            }
                        })

                        animator.start()

                        delay(delayMillis)
                    }

                    delay(animationDuration + delayMillis)

                    // 박스들 초기화
                    for (box in boxes) {
                        box.clearAnimation()
                        box.translationY = 0f
                        box.visibility = View.VISIBLE
                    }

                    delay(delayMillis)
                }
            }
            // receive일 때
            else if (type == "receive")
            {
                while (true) {
                    // 모든 박스들이 안보이는 상태에서 시작해야 함
                    for(i in boxes.indices.reversed()) boxes[i].visibility = View.INVISIBLE
                    for (i in boxes.indices.reversed()) {
                        val box = boxes[i]
                        box.visibility = View.INVISIBLE

                        box.translationY = -box.height.toFloat()

                        val appearAnimator = ObjectAnimator.ofFloat(box, "translationY", -box.height.toFloat(), 0f).apply {
                            duration = animationDuration
                            interpolator = AccelerateInterpolator()
                        }

                        appearAnimator.addListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator) {
                                box.visibility = View.VISIBLE
                            }

                            override fun onAnimationEnd(animation: Animator) {
                            }

                            override fun onAnimationCancel(animation: Animator) {}

                            override fun onAnimationRepeat(animation: Animator) {}
                        })

                        appearAnimator.start()

                        delay(delayMillis)
                    }

                    delay(animationDuration + delayMillis)
                }
            }
        }
    }
}

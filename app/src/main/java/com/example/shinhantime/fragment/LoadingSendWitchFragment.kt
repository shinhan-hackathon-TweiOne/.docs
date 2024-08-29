import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
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
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.example.shinhantime.R
import com.example.shinhantime.activity.ConfirmActivity
import com.example.shinhantime.activity.LoadingActivity
import com.example.shinhantime.activity.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
    private var navigationJob: Job? = null  // 코루틴 작업을 관리하는 Job 객체

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

        // 홈 버튼 설정
        view.findViewById<Button>(R.id.button_home).setOnClickListener{ goHome() }

        boxes = boxIds.map { view.findViewById<View>(it) }
        startAnimation()

        // 5초 후 함수 실행
        // 실제 환경에서는 무한루프로 여기서 서로 요청? 컨펌?을 기다리게 하다가 신호가 오면 호출되도록 하면 될 듯
        CoroutineScope(Dispatchers.Main).launch {
            delay(5000L)
            if (type == "send") {
                completeSending()
            } else if (type == "receive") {
                completeReceiving()
            }
        }
    }

    private fun goHome() {
        // 홈(Main)으로 이동
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun completeSending() {
        if (isAdded) {
            val intent = Intent(requireContext(), ConfirmActivity::class.java)
            intent.putExtra("pageName", "MainActivity")
            startActivity(intent)
        }
    }

    private fun completeReceiving() {
        if (isAdded) {
            val intent = Intent(requireContext(), ConfirmActivity::class.java)
            intent.putExtra("pageName", "LoadingActivity")
            intent.putExtra("fragmentName", "SendWitch")
            intent.putExtra("loadType", "receive")
            startActivity(intent)
        }
    }

    private fun startAnimation() {
        CoroutineScope(Dispatchers.Main).launch {
            if (type == "send") {
                animateSend()
            } else if (type == "receive") {
                animateReceive()
            }
        }
    }

    private suspend fun animateSend() {
        while (true) {
            if (!isAdded) break
            for (i in boxes.indices) {
                val box = boxes[i]
                box.visibility = View.VISIBLE

                val animator =
                    ObjectAnimator.ofFloat(box, "translationY", 0f, -box.height.toFloat()).apply {
                        duration = animationDuration
                        interpolator = AccelerateInterpolator()
                    }

                animator.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator) {}

                    override fun onAnimationEnd(p0: Animator) {
                        box.visibility = View.INVISIBLE
                    }

                    override fun onAnimationCancel(p0: Animator) {}

                    override fun onAnimationRepeat(p0: Animator) {}
                })

                animator.start()

                delay(delayMillis)
            }

            delay(animationDuration + delayMillis)

            for (box in boxes) {
                box.clearAnimation()
                box.translationY = 0f
                box.visibility = View.VISIBLE
            }

            delay(delayMillis)
        }
    }

    private suspend fun animateReceive() {
        while (true) {
            if (!isAdded) break
            for (i in boxes.indices.reversed()) boxes[i].visibility = View.INVISIBLE
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

                    override fun onAnimationEnd(animation: Animator) {}

                    override fun onAnimationCancel(animation: Animator) {}

                    override fun onAnimationRepeat(animation: Animator) {}
                })

                appearAnimator.start()

                delay(delayMillis)
            }

            delay(animationDuration + delayMillis)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        navigationJob?.cancel()  // 뷰가 파괴될 때 코루틴 취소
    }
}
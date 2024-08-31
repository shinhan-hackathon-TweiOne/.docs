import android.Manifest
import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.shinhantime.R
import com.example.shinhantime.activity.ConfirmActivity
import com.example.shinhantime.networks.BLEDeviceConnection
import com.example.shinhantime.networks.BLEScanner
import com.example.shinhantime.networks.DeviceInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoadingSendWitchFragment : Fragment() {

    private val permissions = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private fun checkPermissions() {
        if (permissions.all { ActivityCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED }) {
            startBLEProcess() // All permissions are granted, start BLE process
        } else {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startBLEProcess() // All permissions granted, start BLE process
            } else {
                // Handle the case where the user denies the permissions
                Toast.makeText(requireContext(), "Permissions required for BLE scanning", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var bleScanner: BLEScanner
    private var bleDeviceConnection: BLEDeviceConnection? = null

    private lateinit var type: String
    private lateinit var boxes: List<View>
    private val delayMillis = 500L
    private val animationDuration = 500L
    private val boxIds = listOf(
        R.id.box1, R.id.box2, R.id.box3, R.id.box4, R.id.box5, R.id.box6
    )
    private var navigationJob: Job? = null  // 코루틴 작업을 관리하는 Job 객체
    private lateinit var lastActivity: String
    private val triedDevices = mutableSetOf<String>() // Set to track tried devices

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loading_sendwitch, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lastActivity = arguments?.getString("pageName").toString()
        type = arguments?.getString("loadType").toString()

        println("LAST ACTIVITY TO " + lastActivity)

        if (type == "send") {
            view.findViewById<TextView>(R.id.text_action).text = "보내는 중 ..."
            setupSendBoxes(view)
            checkPermissions()
        } else if (type == "receive") {
            view.findViewById<TextView>(R.id.text_action).text = "받는 중 ..."
            setupReceiveBoxes(view)
        }

        boxes = boxIds.map { view.findViewById<View>(it) }
        startAnimation()
    }

    private fun setupSendBoxes(view: View) {
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

    private fun setupReceiveBoxes(view: View) {
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

    private fun startBLEProcess() {
        // Initialize BLEScanner
        bleScanner = BLEScanner(requireContext())

        CoroutineScope(Dispatchers.Main).launch {
            bleScanner.startScanning()

            bleScanner.foundDevices.collectLatest { devices ->
                for (deviceInfo in devices) {
                    if (!triedDevices.contains(deviceInfo.device.address)) {
                        connectToDeviceSequentially(deviceInfo)
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun connectToDeviceSequentially(deviceInfo: DeviceInfo) {
        // Add device to the tried list
        triedDevices.add(deviceInfo.device.address)

        bleDeviceConnection = BLEDeviceConnection(requireContext(), deviceInfo)

        withContext(Dispatchers.Main) {
            bleDeviceConnection?.connect()
            bleDeviceConnection?.isConnected?.collectLatest { isConnected ->
                if (isConnected) {
                    Log.w("BLEConnection", "Connected to device: ${deviceInfo.device.name}")
                    bleDeviceConnection?.discoverServicesWithDelay()
                    bleDeviceConnection?.readPassword()
                    bleDeviceConnection?.writeName()
                    // Add any additional steps here

                    // Optional: Add a delay or further operations between connections
                } else {
                    Log.d("BLEConnection", "Failed to connect to device: ${deviceInfo.device.name}")
                }
            }
        }
    }

    private fun goBack() {
        if (isAdded && context != null) {
            try {
                val className = Class.forName("com.example.shinhantime.activity.$lastActivity")
                activity?.finish()
                val intent = Intent(requireContext(), className)
                startActivity(intent)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    private fun completeSending() {
        if (isAdded) {
            val className = Class.forName("com.example.shinhantime.activity.$lastActivity")
            activity?.finish()
            val intent = Intent(requireContext(), ConfirmActivity::class.java)
            intent.putExtra("pageName", lastActivity)
            startActivity(intent)
        }
    }

    private fun completeReceiving() {
        if (isAdded) {
            val className = Class.forName("com.example.shinhantime.activity.$lastActivity")
            activity?.finish()
            val intent = Intent(requireContext(), ConfirmActivity::class.java)
            intent.putExtra("pageName", lastActivity)
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

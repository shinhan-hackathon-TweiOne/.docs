package com.example.shinhantime.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.Button
import com.example.shinhantime.R
import com.example.shinhantime.activity.LoginActivity

class FingerprintFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_fingerprint_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 지문 인증을 시뮬레이션하는 버튼 예제
       // view.findViewById<View>(R.id.authenticate_button).setOnClickListener {
       //     (activity as? LoginActivity)?.onFingerprintAuthenticated()
        //}

        view.findViewById<View>(R.id.button_cancel).setOnClickListener {
            (activity as? LoginActivity)?.onFingerprintCancelled()
        }
    }
}
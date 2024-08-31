package com.example.shinhantime.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.shinhantime.R
import com.example.shinhantime.activity.FindingActivity
import com.example.shinhantime.activity.LoadingActivity
import com.example.shinhantime.activity.SendingActivity

class MainAccountExistFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // fragment view 정보
        val view = inflater.inflate(R.layout.fragment_main_acoount_exist, container, false)

        // button 정보
        val sendButton: Button = view.findViewById(R.id.button_send)
        val receiveButton: Button = view.findViewById(R.id.button_receive)  // Find the receive button

        // button에 함수 할당
        sendButton.setOnClickListener{
            // send 는 findingactivity로 전환
//            val intent = Intent(requireContext(), FindingActivity::class.java)
//            startActivity(intent)
            activity?.finish()
            val intent = Intent(requireContext(), SendingActivity::class.java)
            intent.putExtra("pageName", "MainActivity")
            intent.putExtra("fragmentName", "SendWitch")
            intent.putExtra("loadType", "send")
            startActivity(intent)
        }
        receiveButton.setOnClickListener{
            // receive는 바로 loading페이지로 전환
            // 이 때 loading페이지는 sendwitch 로딩페이지이며 type은 recevie임을 명시
            activity?.finish()
            val intent = Intent(requireContext(), LoadingActivity::class.java)
            intent.putExtra("pageName", "MainActivity")
            intent.putExtra("fragmentName", "SendWitch")
            intent.putExtra("loadType", "receive")
            startActivity(intent)
        }
        return view
    }
}

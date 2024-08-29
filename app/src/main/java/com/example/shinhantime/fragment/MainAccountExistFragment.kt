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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main_acoount_exist, container, false)


        val sendButton: Button = view.findViewById(R.id.button_send)
        val receiveButton: Button = view.findViewById(R.id.button_receive)  // Find the receive button

        // Find the button and set click listener
 // Use your actual button ID here
        sendButton.setOnClickListener{(sendpage())}
        receiveButton.setOnClickListener{receivepage()}


        return view
    }

    private fun sendpage()
    {
        val intent = Intent(requireContext(), FindingActivity::class.java)
        startActivity(intent)
    }

    private fun receivepage()
    {
        val intent = Intent(requireContext(), LoadingActivity::class.java)
        intent.putExtra("pageName", "SendWitch")
        intent.putExtra("loadType", "receive")
        startActivity(intent)
    }
}

package com.nagz.money_manager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nagz.money_manager.R

class AiFragment: Fragment(R.layout.fragment_ai_chat) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ai_send = view.findViewById<ImageButton>(R.id.ai_send)
        ai_send.setOnClickListener {
            Toast.makeText(requireContext(), "Mmmmmmmh AI", Toast.LENGTH_SHORT).show()
        }
    }}

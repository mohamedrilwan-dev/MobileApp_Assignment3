package com.example.doctorfinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText

class InputFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_input, container, false)

        val textInput = view.findViewById<TextInputEditText>(R.id.textInputEditText)
        val numberInput = view.findViewById<TextInputEditText>(R.id.numberInputEditText)
        val sendButton = view.findViewById<Button>(R.id.sendButton)

        sendButton.setOnClickListener {
            val text = textInput.text.toString()
            val number = numberInput.text.toString()

            val bundle = Bundle().apply {
                putString("EXTRA_TEXT", text)
                putString("EXTRA_NUMBER", number)
            }

            val resultFragment = ResultFragment()
            resultFragment.arguments = bundle

            fragmentManager?.beginTransaction()
                ?.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                ?.replace(R.id.fragment_container, resultFragment)
                ?.addToBackStack(null)
                ?.commit()
        }

        return view
    }
}

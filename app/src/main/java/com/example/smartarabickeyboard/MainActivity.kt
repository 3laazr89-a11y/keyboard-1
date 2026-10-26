package com.example.smartarabickeyboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 32, 28, 32)
            setBackgroundColor(0xFF211710.toInt())
        }

        val title = TextView(this).apply {
            text = getString(R.string.welcome_message)
            textSize = 24f
            gravity = Gravity.CENTER
            setTextColor(0xFFFFE8D0.toInt())
            setPadding(8, 16, 8, 28)
        }

        val description = TextView(this).apply {
            text = "العربية • English\n\nخصوصية محلية وتصميم قابل للتطوير"
            textSize = 16f
            gravity = Gravity.CENTER
            setTextColor(0xFFD8C2AE.toInt())
            setPadding(8, 8, 8, 24)
        }

        val enableButton = Button(this).apply {
            text = getString(R.string.enable_keyboard)
            isAllCaps = false
            setOnClickListener {
                startActivity(
                    Intent("android.settings.INPUT_METHOD_SETTINGS")
                )
            }
        }

        val chooseButton = Button(this).apply {
            text = getString(R.string.choose_keyboard)
            isAllCaps = false
            setOnClickListener {
                val manager = getSystemService(
                    Context.INPUT_METHOD_SERVICE
                ) as InputMethodManager
                manager.showInputMethodPicker()
            }
        }

        root.addView(title)
        root.addView(description)

        root.addView(
            enableButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(
            chooseButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 12
            }
        )

        setContentView(root)
    }
}
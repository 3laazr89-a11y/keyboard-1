
package com.example.smartarabickeyboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : Activity() {

    private val prefs by lazy {
        getSharedPreferences("keyboard_settings", MODE_PRIVATE)
    }

    private val gold = 0xFFE8BC77.toInt()
    private val dark = 0xFF191614.toInt()
    private val light = 0xFFFFE9CF.toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(24, 36, 24, 24)
            setBackgroundColor(dark)
        }

        fun text(value: String, size: Float, color: Int): TextView {
            return TextView(this).apply {
                text = value
                textSize = size
                setTextColor(color)
                gravity = Gravity.CENTER
                setPadding(8, 12, 8, 12)
            }
        }

        root.addView(
            text("✦ SMART ARABIC KEYBOARD ✦", 21f, gold)
        )

        root.addView(
            text("الإعدادات والمظهر", 18f, light)
        )

        root.addView(
            text("ارتفاع لوحة المفاتيح", 17f, gold)
        )

        val currentHeight = prefs.getInt("keyboard_height", 300)

        val heightLabel = text("$currentHeight dp", 16f, light)

        val seekBar = SeekBar(this).apply {
            max = 140
            progress = currentHeight.coerceIn(240, 380) - 240
            progressTintList = android.content.res.ColorStateList.valueOf(gold)
            thumbTintList = android.content.res.ColorStateList.valueOf(gold)
        }

        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    heightLabel.text = "${240 + progress} dp"
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            }
        )

        root.addView(heightLabel)
        root.addView(
            seekBar,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val saveButton = Button(this).apply {
            text = "حفظ ارتفاع الكيبورد"
            isAllCaps = false
            setOnClickListener {
                prefs.edit()
                    .putInt("keyboard_height", 240 + seekBar.progress)
                    .apply()

                heightLabel.text =
                    "تم حفظ الارتفاع: ${240 + seekBar.progress} dp"
            }
        }

        root.addView(
            saveButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val enableButton = Button(this).apply {
            text = "تفعيل لوحة المفاتيح"
            isAllCaps = false
            setOnClickListener {
                startActivity(
                    Intent("android.settings.INPUT_METHOD_SETTINGS")
                )
            }
        }

        root.addView(
            enableButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val chooseButton = Button(this).apply {
            text = "اختيار لوحة المفاتيح"
            isAllCaps = false
            setOnClickListener {
                val manager = getSystemService(
                    Context.INPUT_METHOD_SERVICE
                ) as InputMethodManager

                manager.showInputMethodPicker()
            }
        }

        root.addView(
            chooseButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(
            text(
                "الخصوصية أولاً • إعدادات محفوظة محلياً",
                13f,
                gold
            )
        )

        setContentView(root)
    }
}

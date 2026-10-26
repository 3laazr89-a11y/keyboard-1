
package com.example.smartarabickeyboard

import android.graphics.Color
import android.graphics.Typeface
import android.inputmethodservice.InputMethodService
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class SmartKeyboardService : InputMethodService() {

    private var isArabic = true
    private lateinit var keyboardRoot: LinearLayout

    private val gold = Color.rgb(232, 188, 119)
    private val keyboardBackgroundColor = Color.rgb(25, 22, 20)
    private val keyBackground = Color.rgb(57, 43, 34)

    private val arabicRows = listOf(
        listOf("ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح", "ج"),
        listOf("ش", "س", "ي", "ب", "ل", "ا", "ت", "ن", "م", "ك"),
        listOf("ئ", "ء", "ؤ", "ر", "لا", "ى", "ة", "و", "ز", "ظ", "د")
    )

    private val englishRows = listOf(
        listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
        listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
        listOf("z", "x", "c", "v", "b", "n", "m")
    )

    override fun onCreateInputView(): View {
        keyboardRoot = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(4, 6, 4, 6)
            setBackgroundColor(background)
        }

        refreshKeyboard()
        return keyboardRoot
    }

    private fun keyboardHeight(): Int {
        return getSharedPreferences("keyboard_settings", MODE_PRIVATE)
            .getInt("keyboard_height", 300)
            .coerceIn(240, 380)
    }

    private fun refreshKeyboard() {
        if (!::keyboardRoot.isInitialized) return

        keyboardRoot.removeAllViews()

        val totalHeight = keyboardHeight()
        val headerHeight = 38
        val controlsHeight = 50
        val rowHeight = ((totalHeight - headerHeight - controlsHeight - 12) / 3)
            .coerceIn(48, 100)

        keyboardRoot.layoutParams = keyboardRoot.layoutParams?.apply {
            height = dp(totalHeight)
        } ?: LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(totalHeight)
        )

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val brand = TextView(this).apply {
            text = "SMART  •  ✦"
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(gold)
            gravity = Gravity.CENTER_VERTICAL
            setPadding(8, 0, 8, 0)
        }

        header.addView(
            brand,
            LinearLayout.LayoutParams(0, dp(headerHeight), 1f)
        )

        header.addView(
            createKey(if (isArabic) "العربية" else "EN") {
                isArabic = !isArabic
                refreshKeyboard()
            },
            LinearLayout.LayoutParams(
                dp(76), dp(headerHeight)
            )
        )

        keyboardRoot.addView(header)
        addCharacterRows(rowHeight)
        addControlRow(controlsHeight)
    }

    private fun addCharacterRows(rowHeight: Int) {
        val rows = if (isArabic) arabicRows else englishRows

        rows.forEach { keys ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
            }

            keys.forEach { key ->
                row.addView(
                    createKey(key) {
                        if (!isPasswordField()) {
                            currentInputConnection?.commitText(key, 1)
                        } else {
                            currentInputConnection?.commitText(key, 1)
                        }
                    },
                    LinearLayout.LayoutParams(
                        0, dp(rowHeight), 1f
                    ).apply {
                        setMargins(dp(2), dp(3), dp(2), dp(3))
                    }
                )
            }

            keyboardRoot.addView(
                row,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(rowHeight)
                )
            )
        }
    }

    private fun addControlRow(height: Int) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        fun addControl(label: String, weight: Float, action: () -> Unit) {
            row.addView(
                createKey(label, action),
                LinearLayout.LayoutParams(0, dp(height), weight).apply {
                    setMargins(dp(2), dp(2), dp(2), dp(2))
                }
            )
        }

        addControl("?123", 1.1f) {
            currentInputConnection?.commitText("123", 1)
        }

        addControl("🌐", 0.9f) {
            isArabic = !isArabic
            refreshKeyboard()
        }

        addControl("مسافة", 2.6f) {
            currentInputConnection?.commitText(" ", 1)
        }

        addControl("⌫", 1f) {
            currentInputConnection?.deleteSurroundingText(1, 0)
        }

        addControl("↵", 1f) {
            val connection = currentInputConnection
            val info = currentInputEditorInfo

            if (connection != null && info != null) {
                val action = info.imeOptions and EditorInfo.IME_MASK_ACTION
                if (action != EditorInfo.IME_ACTION_NONE &&
                    action != EditorInfo.IME_ACTION_UNSPECIFIED
                ) {
                    connection.performEditorAction(action)
                } else {
                    connection.commitText("\n", 1)
                }
            }
        }

        keyboardRoot.addView(
            row,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(height)
            )
        )
    }

    private fun createKey(label: String, action: () -> Unit): Button {
        return Button(this).apply {
            text = label
            isAllCaps = false
            textSize = if (label.length > 3) 12f else 18f
            setTextColor(if (label == "↵" || label == "⌫") gold else Color.WHITE)
            setBackgroundColor(keyBackground)
            setPadding(0, 0, 0, 0)
            stateListAnimator = null
            setOnClickListener { action() }
        }
    }

    private fun isPasswordField(): Boolean {
        val info = currentInputEditorInfo ?: return false
        val inputClass = info.inputType and android.text.InputType.TYPE_MASK_CLASS
        val variation = info.inputType and android.text.InputType.TYPE_MASK_VARIATION

        return inputClass == android.text.InputType.TYPE_CLASS_TEXT &&
            variation in setOf(
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD,
                android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD,
                android.text.InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
            )
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        if (::keyboardRoot.isInitialized) refreshKeyboard()
    }
}

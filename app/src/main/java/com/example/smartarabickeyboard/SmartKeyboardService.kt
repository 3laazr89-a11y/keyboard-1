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
    private lateinit var languageButton: Button

    private val arabicRows = listOf(
        listOf("ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح", "ج"),
        listOf("ش", "س", "ي", "ب", "ل", "ا", "ت", "ن", "م", "ك", "ط"),
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
            setPadding(4, 8, 4, 8)
            setBackgroundColor(Color.rgb(35, 27, 23))
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val brand = TextView(this).apply {
            text = "SMART KEYBOARD"
            textSize = 12f
            setTextColor(Color.rgb(226, 190, 154))
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER_VERTICAL
            setPadding(8, 4, 8, 4)
        }

        languageButton = Button(this).apply {
            text = if (isArabic) "العربية" else "English"
            isAllCaps = false
            textSize = 12f
            setOnClickListener {
                isArabic = !isArabic
                refreshKeyboard()
            }
        }

        header.addView(
            brand,
            LinearLayout.LayoutParams(0, 44, 1f)
        )
        header.addView(
            languageButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                44
            )
        )

        keyboardRoot.addView(header)
        addCharacterRows()
        addControlRow()

        return keyboardRoot
    }

    private fun refreshKeyboard() {
        if (!::keyboardRoot.isInitialized) return

        keyboardRoot.removeAllViews()

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val brand = TextView(this).apply {
            text = "SMART KEYBOARD"
            textSize = 12f
            setTextColor(Color.rgb(226, 190, 154))
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER_VERTICAL
            setPadding(8, 4, 8, 4)
        }

        languageButton = Button(this).apply {
            text = if (isArabic) "العربية" else "English"
            isAllCaps = false
            textSize = 12f
            setOnClickListener {
                isArabic = !isArabic
                refreshKeyboard()
            }
        }

        header.addView(
            brand,
            LinearLayout.LayoutParams(0, 44, 1f)
        )
        header.addView(
            languageButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                44
            )
        )

        keyboardRoot.addView(header)
        addCharacterRows()
        addControlRow()
    }

    private fun addCharacterRows() {
        val rows = if (isArabic) arabicRows else englishRows

        rows.forEach { keys ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
            }

            keys.forEach { key ->
                val button = createKey(key) {
                    currentInputConnection?.commitText(key, 1)
                }

                row.addView(
                    button,
                    LinearLayout.LayoutParams(
                        0,
                        48,
                        1f
                    ).apply {
                        setMargins(2, 3, 2, 3)
                    }
                )
            }

            keyboardRoot.addView(
                row,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
        }
    }

    private fun addControlRow() {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        row.addView(
            createKey("?123") {
                currentInputConnection?.commitText("123", 1)
            },
            weightedParams(1.2f)
        )

        row.addView(
            createKey("🌐") {
                isArabic = !isArabic
                refreshKeyboard()
            },
            weightedParams(1f)
        )

        row.addView(
            createKey("،") {
                currentInputConnection?.commitText(
                    if (isArabic) "،" else ",",
                    1
                )
            },
            weightedParams(0.8f)
        )

        row.addView(
            createKey("مسافة") {
                currentInputConnection?.commitText(" ", 1)
            },
            weightedParams(2.5f)
        )

        row.addView(
            createKey("⌫") {
                currentInputConnection?.deleteSurroundingText(1, 0)
            },
            weightedParams(1f)
        )

        row.addView(
            createKey("↵") {
                val connection = currentInputConnection
                val info = currentInputEditorInfo

                if (info != null && connection != null) {
                    val action = info.imeOptions and
                        EditorInfo.IME_MASK_ACTION

                    if (action != EditorInfo.IME_ACTION_NONE &&
                        action != EditorInfo.IME_ACTION_UNSPECIFIED
                    ) {
                        connection.performEditorAction(action)
                    } else {
                        connection.commitText("\n", 1)
                    }
                }
            },
            weightedParams(1f)
        )

        keyboardRoot.addView(
            row,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
    }

    private fun weightedParams(weight: Float): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            0,
            48,
            weight
        ).apply {
            setMargins(2, 3, 2, 3)
        }
    }

    private fun createKey(
        label: String,
        action: () -> Unit
    ): Button {
        return Button(this).apply {
            text = label
            isAllCaps = false
            textSize = if (label.length > 2) 12f else 18f
            setTextColor(Color.rgb(255, 245, 236))
            setBackgroundColor(Color.rgb(76, 57, 46))
            setPadding(0, 0, 0, 0)
            stateListAnimator = null
            setOnClickListener { action() }
        }
    }

    override fun onStartInputView(
        info: EditorInfo?,
        restarting: Boolean
    ) {
        super.onStartInputView(info, restarting)

        if (info?.inputType != null) {
            val variation = info.inputType and
                android.text.InputType.TYPE_MASK_VARIATION

            if (variation ==
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                variation ==
                android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                variation ==
                android.text.InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
            ) {
                // لا نضيف هنا أي ميزة لحفظ النصوص.
            }
        }
    }
}
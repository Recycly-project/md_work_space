package com.koaladev.recycly.ui.custombutton
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class CustomEmailField: AppCompatEditText {

    constructor(context: Context): super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                error = if (!s.isNullOrEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    "Format email salah"
                } else {
                    null
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }
}
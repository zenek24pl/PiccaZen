package com.example.picca

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.annotation.ColorRes

class SpanUtils private constructor(private val tw: TextView,val context: Context) {
    var builder: SpannableStringBuilder
    var length = 0
    fun convertToMoney(money: Double) {
        tw.setText(
            java.lang.String.valueOf(
                Util.decimPlace(
                    money,
                    2
                )
            ) + "zł"
        )
    }

    fun space(): SpanUtils {
        return normalText(" ")
    }

    fun clickableText(text: String, onClick: ClickableSpan?): SpanUtils {
        builder.append(text)
        builder.setSpan(onClick, length, length + text.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        builder.setSpan(
            ForegroundColorSpan(color(R.color.l_blue_text)),
            length,
            length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        length += text.length
        return this
    }

    fun normalText(text: String?): SpanUtils {
        if (text == null) {
            return this

        }
        builder.append(text)
        length += text.length
        return this
    }

    fun done() {
        tw.text = builder
        tw.movementMethod = LinkMovementMethod.getInstance()
    }
    fun color(@ColorRes colorRes: Int): Int {
        return context.getResources().getColor(colorRes)
    }
    companion object {
        fun on(textView: TextView,context: Context): SpanUtils {
            return SpanUtils(textView,context)
        }


    }

    init {
        builder = SpannableStringBuilder()
    }
}
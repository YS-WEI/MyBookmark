package com.example.mybookmark.bindings

import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.mybookmark.util.ShareFun

object ViewBindings {

    @BindingAdapter("imageUrl")
    @JvmStatic
    fun imageUrl(imageView: ImageView, url: String) {
        if(TextUtils.isEmpty(url)) {
            return
        }

        val context = imageView.context
        Glide.with(context)
            .load(url)
            .into(imageView)
    }

    @BindingAdapter(value = ["lastTimeDate", "updateDate"])
    @JvmStatic
    fun showDates(textView: TextView, lastTimeDate: String, updateDate: String) {
        textView.text = "上次閱讀: ${ShareFun.formatDate(lastTimeDate)}\n更新時間: ${ShareFun.formatDate(updateDate)}"
    }
}
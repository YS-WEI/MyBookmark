package com.example.mybookmark.bindings

import android.text.TextUtils
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

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
}
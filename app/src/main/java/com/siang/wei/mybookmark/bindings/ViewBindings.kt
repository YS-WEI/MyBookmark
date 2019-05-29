package com.siang.wei.mybookmark.bindings

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.siang.wei.mybookmark.R
import com.siang.wei.mybookmark.db.model.Mark
import com.siang.wei.mybookmark.util.ShareFun

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


    @BindingAdapter("isVisibility")
    @JvmStatic
    fun isVisibility(view: View, mark: Mark) {

        if (mark.updateDate.toInt() > mark.lastTimeDate!!.toInt()) {
            view.visibility = View.VISIBLE
        } else if(TextUtils.isEmpty(mark.readEpisode) || !(mark.totalEpisode!!.indexOf(mark.readEpisode!!) != -1 || mark.readEpisode!!.indexOf(mark.totalEpisode!!) != -1)) {
            view.setBackgroundResource(R.drawable.green_point)
            view.visibility = View.VISIBLE
        } else {

            view.visibility = View.GONE
        }

    }
}
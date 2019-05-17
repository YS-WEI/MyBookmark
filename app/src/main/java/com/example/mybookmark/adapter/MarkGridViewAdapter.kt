package com.example.mybookmark.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import com.example.mybookmark.databinding.AdapterMarkItemBinding
import com.example.mybookmark.db.model.Mark



class MarkGridViewAdapter: BaseAdapter() {

    var mList: List<Mark>? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(parent!!.context)
        var binding: AdapterMarkItemBinding
        if (convertView == null) {
            binding = AdapterMarkItemBinding.inflate(layoutInflater, parent, false)
        } else {
            binding = (DataBindingUtil.getBinding(convertView) as AdapterMarkItemBinding?)!!
        }
        if(mList != null) {
            val data = mList!!.get(position)
            binding.markData = data
        }


        return binding.root
    }

    override fun getItem(position: Int): Mark? {
        if(mList != null) {
            return mList!![position]
        } else {
            return null
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        if(mList != null) {
            return mList!!.size
        } else {
            return 0
        }
    }

    fun updateList(list: List<Mark>) {
//        mList.clear();
//        mList.addAll(list)
        mList = list
        notifyDataSetChanged()
    }

}
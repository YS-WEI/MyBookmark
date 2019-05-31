package com.siang.wei.mybookmark.adapter


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.siang.wei.mybookmark.databinding.AdapterImageItemBinding

class ImageRecyclerViewAdapter(imagelist: ArrayList<String>) : RecyclerView.Adapter<ImageRecyclerViewAdapter.ContentViewHolder>() {

    private var list : ArrayList<String> = imagelist



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AdapterImageItemBinding.inflate(layoutInflater, parent, false)
        return ContentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        if(this.list != null)
            return this.list.size
        else
            return 0
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        Log.d("adapter", this.list.toString())
        if(this.list == null || this.list.size == 0) {
            return
        }

        if (holder is ContentViewHolder) {
            holder.bind(this.list[position])
        }

    }

    fun setList(list: ArrayList<String>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    /**
     * Content類型的ViewHolder
     */
    class ContentViewHolder( val binding: AdapterImageItemBinding): RecyclerView.ViewHolder(binding.getRoot()) {

        fun bind(url: String) {
            binding.imageUrl = url
        }
    }
}
package com.hadimas.distories.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hadimas.distories.databinding.ListStoryBinding
import com.hadimas.distories.response.ListStoryItem

class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.ListViewHolder>(diffCallback) {

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = ListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(view)
    }

    interface  OnItemClickCallback{
        fun onItemClicked(data: ListStoryItem)
    }


    inner class ListViewHolder(private var binding: ListStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(storyData: ListStoryItem) {
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(storyData)
            }
            with(binding) {
                Glide.with(ivFoto)
                    .load(storyData.photoUrl)
                    .centerCrop()
                    .into(ivFoto)
                tvUsername.text = storyData.name
                tvDesc.text = storyData.description
            }
        }
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame( oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}

package com.hadimas.distories.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hadimas.distories.databinding.ListStoryBinding
import com.hadimas.distories.response.ListStoryItem

class ListStoryAdapter: RecyclerView.Adapter<ListStoryAdapter.ListViewHolder>() {
    private val listStory = ArrayList<ListStoryItem>()

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    fun setStory(story: List<ListStoryItem>){
        listStory.clear()
        listStory.addAll(story)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = ListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listStory[position])
    }

    override fun getItemCount(): Int = listStory.size

    interface  OnItemClickCallback{
        fun onItemClicked(data: ListStoryItem)
    }

    inner class ListViewHolder(private var binding: ListStoryBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(story: ListStoryItem){
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(story)
            }

            with(binding){
                Glide.with(ivFoto)
                    .load(story.photoUrl)
                    .centerCrop()
                    .into(ivFoto)
                tvUsername.text = story.name
                tvDesc.text = story.description
            }
        }
    }
}

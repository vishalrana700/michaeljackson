package com.song.michaeljackson.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.song.michaeljackson.R
import com.song.michaeljackson.adapter.BaseAdapter
import com.song.michaeljackson.databinding.ItemSongBinding
import com.song.michaeljackson.model.SongsResponseModel

class SongAdapter(
        ctx: Context?, private val items: MutableList<SongsResponseModel.Result>) : BaseAdapter(ctx) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder
        vh = when (viewType) {
            Companion.VIEW_ITEM -> {
                val binding: ItemSongBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context), R.layout.item_song,
                        parent, false
                )
                ViewHolder(binding)
            }
            else -> {
                getFullSpanProgressViewHolder(parent)
            }
        }
        return vh
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        if (holder is ViewHolder) {
            (holder as ViewHolder).bindData(item, position)
        }
    }

    inner class ViewHolder(var binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: SongsResponseModel.Result, position: Int) {

            binding.tvSongName.text = data.trackName
            ctx?.let {
                Glide
                    .with(it)
                    .load(data.artworkUrl100)
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher_round)
                    .into(binding.imgSongThumbnail)
            }

            binding.cardView.setOnClickListener {
                mOnItemClickListener?.onItemClick(it, data, position)
            }

        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return  Companion.VIEW_ITEM
    }

    fun insertData(items: List<SongsResponseModel.Result>) {
//        setLoaded()
        val positionStart = itemCount
        val itemCount: Int = items.size
        this.items.addAll(items)
        notifyItemRangeInserted(positionStart, itemCount)
    }

    fun replaceList(items: List<SongsResponseModel.Result>) {
//        setLoaded()
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size)
    }

}
package com.song.michaeljackson.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.song.michaeljackson.R
import com.song.michaeljackson.databinding.LoaderItemBinding
import com.song.michaeljackson.databinding.LoaderItemFullSpanBinding

abstract class BaseAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder?> {
    private var itemPerDisplay: Int
    protected var loading = false
    private var onLoadMoreListener: OnLoadMoreListener? = null
    var ctx: Context?
    var mOnItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(view: View?, obj: Any?, position: Int)
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = mItemClickListener
    }

    constructor(context: Context?) {
        itemPerDisplay = 10000 // Set A Page Limit of 10000 if Not Specified (Vary Long List)
        ctx = context
    }

    constructor(context: Context?, itemPerDisplay: Int) {
        this.itemPerDisplay = itemPerDisplay
        ctx = context
    }

    fun getProgressViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding: LoaderItemBinding = DataBindingUtil.inflate (
                LayoutInflater.from(parent.context), R.layout.loader_item,
                parent,false
        )
        return ProgressViewHolder(binding)
    }

    inner class ProgressViewHolder(var binding: LoaderItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: Any, position: Int) { }
    }

    fun getFullSpanProgressViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding: LoaderItemFullSpanBinding = DataBindingUtil.inflate (
                LayoutInflater.from(parent.context), R.layout.loader_item_full_span,
                parent,false
        )
        return FullSpanProgressViewHolder(binding)
    }

    inner class FullSpanProgressViewHolder(var binding: LoaderItemFullSpanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: Any, position: Int) { }
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        lastItemViewDetector(recyclerView)
        super.onAttachedToRecyclerView(recyclerView)
    }

    fun setOnLoadMoreListener(onLoadMoreListener: OnLoadMoreListener?) {
        this.onLoadMoreListener = onLoadMoreListener
    }

    private fun lastItemViewDetector(recyclerView: RecyclerView) {
        if (recyclerView.layoutManager is StaggeredGridLayoutManager) {
            val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager?
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    layoutManager?.let {
                        val lastPos = getLastVisibleItem(it.findLastVisibleItemPositions(null))

                        checkAndLoadMore(lastPos)
                    }
                }
            })
        } else if (recyclerView.layoutManager is GridLayoutManager) {
            val layoutManager = recyclerView.layoutManager as GridLayoutManager?
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    checkAndLoadMore(layoutManager?.findLastVisibleItemPosition())
                }
            })
        }
    }

    private fun checkAndLoadMore(lastPos : Int?) {
        if (!loading && lastPos == itemCount - 1 && onLoadMoreListener != null) {
            val currentPage = (itemCount / itemPerDisplay) - 1
            Log.e("Page State", "$lastPos , $currentPage , $itemCount , $itemPerDisplay")
            onLoadMoreListener?.onLoadMore(currentPage)
            loading = true
        }
    }

    interface OnLoadMoreListener {
        fun onLoadMore(current_page: Int)
    }

    private fun getLastVisibleItem(into: IntArray): Int {
        var lastIdx = into[0]
        for (i in into) {
            if (lastIdx < i) lastIdx = i
        }
        return lastIdx
    }

    companion object {
        const val VIEW_PROGRESS = 0
        const val VIEW_HEADER = 1
        const val VIEW_ITEM = 2
    }
}
package com.song.michaeljackson.ui.main

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.song.michaeljackson.adapter.BaseAdapter
import com.song.michaeljackson.databinding.ActivityMainBinding
import com.song.michaeljackson.model.SongsResponseModel
import com.song.michaeljackson.ui.video_play.VideoPlayActivity

class MainActivity : AppCompatActivity() {

    //binding
    private lateinit var binding:ActivityMainBinding
    private lateinit var viewModel: SongsListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvSongList.layoutManager = LinearLayoutManager(this)

        viewModelObserver()

        callSongListApi()
    }

    /**
     * Call songs api
     */
    private fun callSongListApi() {
        if (isConnected) {
            binding.progressBar.visibility = View.VISIBLE
            binding.tvError.visibility = View.GONE
            viewModel.getSongList()
        } else{
            binding.tvError.visibility = View.VISIBLE
        }
    }
    
    /**
     * Check internet
     */
    val isConnected: Boolean
        get() {
            val connectivityManager =
                    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = connectivityManager.activeNetworkInfo
            return netInfo != null && netInfo.isConnected
        }

    /**
     * View model observer's
     */
    private fun viewModelObserver() {
        viewModel = ViewModelProvider(this, SongsListViewModelFactory(applicationContext))
                .get(SongsListViewModel::class.java)

        viewModel.songsList.observe(this, {
            if (it.resultCount == 50) {
                binding.progressBar.visibility = View.GONE
                setResult(it.results)
            }
        })

        viewModel.error.observe(this, {
            binding.progressBar.visibility = View.GONE
            showToast(it,this)
        })

        viewModel.exception.observe(this, {
            binding.progressBar.visibility = View.GONE
            showToast(getString(it),this)
        })

    }

    /**
     * Set api data to adapter
     */
    private fun setResult(dataList: List<SongsResponseModel.Result>) {
        var mSongAdapter: SongAdapter? = null
        if(mSongAdapter != null) {
            mSongAdapter?.replaceList(dataList)
            return
        }

        //set data and list adapter
        mSongAdapter = SongAdapter(this,  dataList.toMutableList())
        binding.rvSongList.adapter = mSongAdapter

        mSongAdapter?.setOnItemClickListener(object : BaseAdapter.OnItemClickListener {
            override fun onItemClick(view: View?, obj: Any?, position: Int) {
                handleItemClick(obj as SongsResponseModel.Result, position)
            }
        })
    }

    private fun handleItemClick(result: SongsResponseModel.Result, position: Int) {
        startActivity(Intent(this, VideoPlayActivity::class.java)
                .putExtra(VideoPlayActivity.VIDEO_DETAILS,result))
    }

    /**
     * show toast
     */
    fun showToast(msg:String,context: Context){
        Toast.makeText(context,msg, Toast.LENGTH_SHORT).show()
    }
}
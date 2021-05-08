package com.song.michaeljackson.repositories

import android.content.Context
import com.song.michaeljackson.model.SongsResponseModel
import com.song.michaeljackson.retrofit.RetrofitInterface
import com.song.michaeljackson.retrofit.RetrofitUtil
import retrofit2.Response


/**
 * Class that requests school list from the remote data source
 */

class FanRepository(private val retrofitInterface: RetrofitInterface) {

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: FanRepository? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: FanRepository(RetrofitUtil.callRetrofit(context)).also { instance = it }
            }
    }

    suspend fun getSongsList(): Response<SongsResponseModel> {
        return retrofitInterface.getSongsList()
    }

}



package com.song.michaeljackson.retrofit

import com.song.michaeljackson.constant.ServerConstant
import com.song.michaeljackson.model.SongsResponseModel
import retrofit2.Response
import retrofit2.http.*


interface RetrofitInterface {

    @GET(ServerConstant.SONGS_SEARCH)
    suspend fun getSongsList(): Response<SongsResponseModel>
}

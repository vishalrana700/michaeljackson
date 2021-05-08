package com.song.michaeljackson.retrofit

import android.content.Context
import com.google.gson.GsonBuilder
import com.song.michaeljackson.constant.ServerConstant
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitUtil {
    
    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private fun getHttpClient(appContext: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(200, TimeUnit.SECONDS) // connect timeout
            .readTimeout(5, TimeUnit.MINUTES) // read timeout
            .writeTimeout(5, TimeUnit.MINUTES) // write timeout
            .addInterceptor(interceptor)
            .addInterceptor(Interceptor { chain ->
                val newRequest: Request = chain.request().newBuilder()
                    //                        .addHeader("token", "AppUtils.getAuthToken(appContext).toString()")
                    .build()
                chain.proceed(newRequest)
            })
            .addInterceptor(NetworkConnectionInterceptor(appContext))
            .build()
    }

    private val builder = Retrofit.Builder()
        .baseUrl(ServerConstant.BASE_URL)

    fun callRetrofit(appContext: Context): RetrofitInterface {
        val gson = GsonBuilder().setLenient().create()
        val retrofit = builder
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(getHttpClient(appContext))
            .build()
        return retrofit.create(RetrofitInterface::class.java)
    }
}
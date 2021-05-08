package com.song.michaeljackson.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.song.michaeljackson.R
import com.song.michaeljackson.model.ErrorResponse
import com.song.michaeljackson.retrofit.NoConnectivityException
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.IOException


open class BaseViewModel : ViewModel() {

    private val _error = MutableLiveData<String>()
    private val _exception = MutableLiveData<Int>()
    private val _logout = MutableLiveData<Boolean>()
    val error: LiveData<String> = _error
    val exception: LiveData<Int> = _exception
    val logout: LiveData<Boolean> = _logout

    fun parseError(responseBody: ResponseBody?) {
        val errorResponse: ErrorResponse = Gson().fromJson(
            responseBody?.charStream(),
            ErrorResponse::class.java
        )
        if (errorResponse.status == 500) {
            _error.postValue(errorResponse.message)
        } else if (errorResponse.status == 401) {
            _error.postValue(errorResponse.error)
            _logout.postValue(true)
        } else
            _error.postValue(errorResponse.error)
    }

    fun catchException(exception: Exception, isCelebRequest: Boolean = false) {
        exception.printStackTrace() // TODO Removed this line if No Exception Print Needed
        this.catchThrowable(exception, isCelebRequest)
    }

    fun catchThrowable( throwable: Throwable, isCelebRequest: Boolean = false) {
        when (throwable) {
            is HttpException -> {
                _exception.postValue(R.string.no_internet)
            }
            is NoConnectivityException -> _exception.postValue(R.string.no_internet)
            is IOException -> {
                if (!isCelebRequest)
                    _exception.postValue(R.string.something_went_wrong)
                else
                    _error.postValue("")
            }
            else -> {
                _error.postValue(throwable.message)
            }
        }
    }
}
package com.song.michaeljackson.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.song.michaeljackson.model.SongsResponseModel
import com.song.michaeljackson.repositories.BaseViewModel
import com.song.michaeljackson.repositories.FanRepository
import kotlinx.coroutines.*

class SongsListViewModel(private val fanRepository: FanRepository): BaseViewModel() {
    private val _songsList = MutableLiveData<SongsResponseModel>()
    val songsList: LiveData<SongsResponseModel> = _songsList

    private val scope = CoroutineScope(Dispatchers.IO)
    private var job: Job? = null

    /**
     * Get songs list from api
     */
    fun getSongList() {
        job = scope.launch {
            try {
                val response = fanRepository.getSongsList()
                when (response.isSuccessful) {
                    true -> {
                        _songsList.postValue(response.body())
                    }
                    false -> parseError(response.errorBody())
                }
            } catch (e: Exception) {
                catchException(e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
        scope.coroutineContext.cancel()
    }
}
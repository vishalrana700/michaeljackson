package com.song.michaeljackson.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.song.michaeljackson.repositories.FanRepository


/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class SongsListViewModelFactory(val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SongsListViewModel::class.java)) {
            return SongsListViewModel(FanRepository.getInstance(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

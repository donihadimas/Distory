package com.hadimas.distories.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hadimas.distories.api.ApiConfig
import com.hadimas.distories.api.ApiService
import com.hadimas.distories.data.StoryPagingSource
import com.hadimas.distories.preferences.LoginPreference
import com.hadimas.distories.response.ListStoryItem
import kotlinx.coroutines.flow.Flow

class MainViewModel(private val pref: LoginPreference): ViewModel() {
    private var apiService: ApiService = ApiConfig.instanceRetro

    fun getListStory(): Flow<PagingData<ListStoryItem>>{
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, pref)
            }
        ).flow.cachedIn(viewModelScope)
    }
}
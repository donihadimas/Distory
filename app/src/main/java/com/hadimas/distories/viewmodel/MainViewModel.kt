package com.hadimas.distories.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.hadimas.distories.api.ApiConfig
import com.hadimas.distories.preferences.LoginPreference
import com.hadimas.distories.response.ListStoryItem
import com.hadimas.distories.response.StoryResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel() : ViewModel() {
    val listStory = MutableLiveData<List<ListStoryItem>>()
    fun setListStory(token: String) {
        viewModelScope.launch {
            val client = ApiConfig.instanceRetro.getStory("Bearer $token")
            client.enqueue(object : Callback<StoryResponse> {
                override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                    if (response.isSuccessful){
                        listStory.postValue(response.body()?.listStory)
                    }
                }

                override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                    Log.e(ContentValues.TAG, "onFailure: ${t.message}")
                }

            })
        }
    }

    fun getListStory(): LiveData<List<ListStoryItem>> {
        return listStory
    }
}
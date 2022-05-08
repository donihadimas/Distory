package com.hadimas.distories.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadimas.distories.api.ApiConfig
import com.hadimas.distories.response.ListMapStory
import com.hadimas.distories.response.MapResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapStoryViewModel: ViewModel() {
    val mapStory = MutableLiveData<List<ListMapStory>>()
    fun setMapStory(token: String) {
        viewModelScope.launch {
            val client = ApiConfig.instanceRetro.getStoryMap("Bearer $token")
            client.enqueue(object : Callback<MapResponse> {
                override fun onResponse(call: Call<MapResponse>, response: Response<MapResponse>) {
                    if (response.isSuccessful){
                        mapStory.postValue(response.body()?.mapStory)
                    }
                }

                override fun onFailure(call: Call<MapResponse>, t: Throwable) {
                    Log.e(ContentValues.TAG, "onFailure: ${t.message}")
                }
            })
        }
    }
    fun getMapStory(): LiveData<List<ListMapStory>> {
        return mapStory
    }
}
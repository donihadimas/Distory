package com.hadimas.distories.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadimas.distories.api.ApiConfig
import com.hadimas.distories.response.RegisterResponse
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel: ViewModel(){
    private val resultRes = MutableLiveData<Boolean>()
    private val messageRes = MutableLiveData<String>()

    fun postRegis(name: String, email: String, password: String){
        val client = ApiConfig.instanceRetro.sendRegis(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful){
                    resultRes.value = true
                }else{
                    resultRes.value = false
                    val jsonObject = JSONTokener(response.errorBody()!!.string()).nextValue() as JSONObject
                    val message = jsonObject.getString("message")
                    messageRes.value = message
                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun getResultResponse(): LiveData<Boolean> {
        return resultRes
    }
}
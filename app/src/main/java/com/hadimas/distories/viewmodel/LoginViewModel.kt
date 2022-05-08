package com.hadimas.distories.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadimas.distories.api.ApiConfig
import com.hadimas.distories.preferences.DataLoginModel
import com.hadimas.distories.preferences.LoginPreference
import com.hadimas.distories.response.LoginResponse
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel (private val pref: LoginPreference): ViewModel() {
    private val resultRes = MutableLiveData<Boolean>()
    private val messageRes = MutableLiveData<String>()
    private lateinit var dataLoginModel: DataLoginModel


    fun postLogin(email: String, password: String) {
        val client = ApiConfig.instanceRetro.getStatus(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val res = response.body()
                if (response.isSuccessful) {
                    if (res != null) {
                        saveDataLogin(res.loginResult.userId, res.loginResult.name, res.loginResult.token)
                        resultRes.value = true
                    }
                } else {
                    resultRes.value = false
                    val jsonObject = JSONTokener(response.errorBody()!!.string()).nextValue() as JSONObject
                    val message = jsonObject.getString("message")
                    messageRes.value = message
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun saveDataLogin(userid: String, name:String, token: String ){
        dataLoginModel = DataLoginModel()
        dataLoginModel.userId = userid
        dataLoginModel.name = name
        dataLoginModel.token = token
        dataLoginModel.isLogin = true

        pref.setDataLogin(dataLoginModel)
    }

    fun getResultResponse(): LiveData<Boolean> {
        return resultRes
    }
}
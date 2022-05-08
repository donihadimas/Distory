package com.hadimas.distories.api

import androidx.viewbinding.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    private const val BaseURL = "https://story-api.dicoding.dev/v1/"
    private val client: Retrofit
        get() {
            val gson = GsonBuilder()
                .setLenient()
                .create()
            val loggingInterceptor = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
            return Retrofit.Builder()
                .baseUrl(BaseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
        }
    val instanceRetro: ApiService
        get() = client.create(ApiService::class.java)
}
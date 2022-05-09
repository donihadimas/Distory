package com.hadimas.distories.api

import com.hadimas.distories.response.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("login")
    fun getStatus(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register")
    fun sendRegis(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @Multipart
    @POST("stories")
    fun sendImage(
        @Header("Authorization") token: String,
        @Part("description") description: String,
        @Part file: MultipartBody.Part,
    ): Call<AddStoryResponse>

    @GET("stories?location=1")
    fun getStoryMap(
        @Header("Authorization") token: String,
    ): Call<MapResponse>

    @GET("stories")
    suspend fun getStoryWithPaging(
        @Header("Authorization") token: String,
        @Query("page") page:Int
    ): StoryResponse

}
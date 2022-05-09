package com.hadimas.distories.response

import com.google.gson.annotations.SerializedName

data class MapResponse(

	@field:SerializedName("listStory")
	val mapStory: List<ListMapStory>,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class ListMapStory(

	@field:SerializedName("photoUrl")
	val photoUrl: String,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("lon")
	val lon: Double,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("lat")
	val lat: Double
)

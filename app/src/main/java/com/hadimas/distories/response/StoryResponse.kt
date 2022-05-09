package com.hadimas.distories.response

import com.google.gson.annotations.SerializedName

data class StoryResponse(

	@field:SerializedName("listStory")
	val listStory: List<ListStoryItem>,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ListStoryItem(
	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("name")
	var name: String? = null,

	@field:SerializedName("photoUrl")
	var photoUrl: String? = null,

	@field:SerializedName("description")
	var description: String? = null,

	@field:SerializedName("createdAt")
	var createdAt: String
)

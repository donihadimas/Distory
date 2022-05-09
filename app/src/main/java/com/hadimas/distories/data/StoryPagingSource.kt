package com.hadimas.distories.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hadimas.distories.api.ApiService
import com.hadimas.distories.preferences.LoginPreference
import com.hadimas.distories.response.ListStoryItem

class StoryPagingSource(private val apiService: ApiService, private val pref: LoginPreference): PagingSource<Int, ListStoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try{
            val token = pref.getDataLogin().token.toString()
            val page: Int = params.key ?: INITIAL_PAGE
            val resData = apiService.getStoryWithPaging("Bearer $token", page)
            LoadResult.Page(
                data = resData.listStory,
                prevKey = if (page == 1) null else page -1,
                nextKey = if (resData.listStory.isNullOrEmpty()) null else page + 1
            )
        }catch (e: Exception){
            LoadResult.Error(e)
        }
    }

    companion object{
        private const val INITIAL_PAGE = 1
    }
}
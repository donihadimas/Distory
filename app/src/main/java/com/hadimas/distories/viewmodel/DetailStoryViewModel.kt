package com.hadimas.distories.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class DetailStoryViewModel : ViewModel(){

    @RequiresApi(Build.VERSION_CODES.O)
    fun setUploadTime(uploadtime: String?): String{
        val parser = Instant.parse(uploadtime)
        val formatter = DateTimeFormatter.ofPattern("HH:mm, dd/HH/yyyy")
            .withZone((ZoneId.of(TimeZone.getDefault().id)))
        return formatter.format(parser)
    }
}
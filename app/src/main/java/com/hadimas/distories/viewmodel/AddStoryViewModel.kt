package com.hadimas.distories.viewmodel

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadimas.distories.api.ApiConfig
import com.hadimas.distories.preferences.DataLoginModel
import com.hadimas.distories.response.AddStoryResponse
import okhttp3.MultipartBody
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class AddStoryViewModel: ViewModel() {
    private val resultRes = MutableLiveData<Boolean>()
    private val messageRes = MutableLiveData<String>()

    fun sendImage(data: DataLoginModel, desc: String, imgRsrc : MultipartBody.Part){
        val service = ApiConfig.instanceRetro.sendImage("Bearer ${data.token}", desc, imgRsrc )
        service.enqueue(object : Callback<AddStoryResponse> {
            override fun onResponse(
                call: Call<AddStoryResponse>,
                response: Response<AddStoryResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        resultRes.value = true
                        messageRes.value = responseBody.message
                    }
                } else {
                    resultRes.value = false
                    val jsonObject = JSONTokener(response.errorBody()?.string()).nextValue() as JSONObject
                    val message = jsonObject.getString("message")
                    messageRes.value = message
                }
            }
            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")
                messageRes.value = t.message
            }
        })
    }

    fun rotateBitmapOrientation(photoFilePath: String?): Bitmap? {
        val bounds = BitmapFactory.Options()
        bounds.inJustDecodeBounds = true
        BitmapFactory.decodeFile(photoFilePath, bounds)
        val opts = BitmapFactory.Options()
        val bm = BitmapFactory.decodeFile(photoFilePath, opts)
        // Read EXIF Data
        var exif: ExifInterface? = null
        try {
            exif = ExifInterface(photoFilePath.toString())
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val orientString: String? = exif?.getAttribute(ExifInterface.TAG_ORIENTATION)
        val orientation =
            orientString?.toInt() ?: ExifInterface.ORIENTATION_NORMAL
        var rotationAngle = 0
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270
        // Rotate Bitmap
        val matrix = Matrix()
        matrix.setRotate(rotationAngle.toFloat(), bm.width.toFloat() / 2, bm.height.toFloat() / 2)
        // Return result
        return Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true)
    }

    fun compressImg(file: File): File {
        val imgbitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val imgbmpStream = ByteArrayOutputStream()
            imgbitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, imgbmpStream)
            val bmpPicByteArray = imgbmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)
        imgbitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }


    fun getResultResponse(): LiveData<Boolean>{
        return resultRes
    }

}
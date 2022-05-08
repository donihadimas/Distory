package com.hadimas.distories

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hadimas.distories.databinding.ActivityAddStoryScreenBinding
import com.hadimas.distories.preferences.DataLoginModel
import com.hadimas.distories.preferences.LoginPreference
import com.hadimas.distories.viewmodel.AddStoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nl.joery.animatedbottombar.AnimatedBottomBar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryScreenBinding
    private lateinit var mLoginPref: LoginPreference
    private lateinit var dataLoginModel: DataLoginModel
    private lateinit var viewModel: AddStoryViewModel
    private lateinit var bottomNav: AnimatedBottomBar
    private lateinit var currentPhotoPath: String
    private lateinit var pgbar: ProgressBar
    private var getImg: File? = null
    private val timeStamp: String = SimpleDateFormat(
        FILENAME_FORMAT,
        Locale.US
    ).format(System.currentTimeMillis())

    //Request Permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.not_granted),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Inisialisasi
        mLoginPref = LoginPreference(this)
        dataLoginModel = mLoginPref.getDataLogin()
        viewModel = ViewModelProvider(this)[AddStoryViewModel::class.java]
        bottomNav = findViewById(R.id.bottom_bar)
        pgbar = findViewById(R.id.pgBarAdd)
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        btnAction()
        navigateNav()
    }

    private val launcherKamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getImg = myFile
            val result = viewModel.rotateBitmapOrientation(myFile.path)
            binding.fotoPreview.setImageBitmap(result)
        }
    }

    private val launcherGaleri = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = converterUri(selectedImg, this@AddStoryActivity)
            getImg = myFile
            binding.fotoPreview.setImageURI(selectedImg)
        }
    }

    private fun btnAction() {
        binding.apply {
            btnCamera.setOnClickListener {
                ambilFoto()
            }
            btnGaleri.setOnClickListener {
                bukaGallery()
            }
            btnUpload.setOnClickListener {
                sendImage()
            }
        }
    }

    private fun navigateNav() {
        bottomNav.onTabSelected = {
            when (it.title) {
                "Home" -> {
                    val intToAdd = Intent(this, MainActivity::class.java)
                    startActivity(
                        intToAdd,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this@AddStoryActivity)
                            .toBundle()
                    )
                }
                "Add Story" -> {
                    Toast.makeText(this, R.string.menu_add, Toast.LENGTH_SHORT).show()
                }
                "Logout" -> {
                    mLoginPref.delDataLogin()
                    Toast.makeText(this, getString(R.string.logout_success), Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intToLogin = Intent(this, LoginScreenActivity::class.java)
                        startActivity(
                            intToLogin,
                            ActivityOptionsCompat.makeSceneTransitionAnimation(this@AddStoryActivity)
                                .toBundle()
                        )
                        finishAffinity()
                    }, delay)
                }
                "Map Story" -> {
                    val intToMap = Intent(this, MapStoryActivity::class.java)
                    startActivity(
                        intToMap,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this@AddStoryActivity).toBundle()
                    )
                }
            }
        }
    }

    private fun ambilFoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        tempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.hadimas.distories",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherKamera.launch(intent)
        }
    }

    private fun bukaGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherGaleri.launch(chooser)
    }

    private fun tempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    private fun converterUri(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = tempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    private fun sendImage() {
        if (getImg != null) {
            val file = viewModel.compressImg(getImg as File)

            val description = binding.tvDesc.text.toString()
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            pgbar.visibility = View.VISIBLE
            if(description.isNotEmpty()){
                lifecycleScope.launch(Dispatchers.Default){
                    viewModel.sendImage(dataLoginModel, description, imageMultipart)
                    withContext(Dispatchers.Main){
                        viewModel.getResultResponse().observe(this@AddStoryActivity) {
                            if (it == true) {
                                Toast.makeText(this@AddStoryActivity, getString(R.string.story_uploaded), Toast.LENGTH_SHORT).show()
                                pgbar.visibility = View.GONE
                                Handler(Looper.getMainLooper()).postDelayed({
                                    val intToMain = Intent(this@AddStoryActivity, MainActivity::class.java)
                                    startActivity(
                                        intToMain,
                                        ActivityOptionsCompat.makeSceneTransitionAnimation(this@AddStoryActivity)
                                            .toBundle()
                                    )
                                    finishAffinity()
                                }, delay)
                            } else {
                                Toast.makeText(this@AddStoryActivity, getString(R.string.failed_story), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }else{
                Toast.makeText(this, R.string.empty_desc, Toast.LENGTH_LONG).show()
                binding.tvDesc.requestFocus()
                pgbar.visibility = View.GONE
            }
        } else {
            Toast.makeText(
                this@AddStoryActivity,
                getString(R.string.empty_img),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        const val delay = 500L
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val FILENAME_FORMAT = "dd-MMM-yyyy"

    }
}
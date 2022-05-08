package com.hadimas.distories

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.hadimas.distories.databinding.ActivityDetailStoryBinding
import com.hadimas.distories.preferences.DataLoginModel
import com.hadimas.distories.preferences.LoginPreference
import com.hadimas.distories.viewmodel.DetailStoryViewModel
import nl.joery.animatedbottombar.AnimatedBottomBar

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    private lateinit var mLoginPref: LoginPreference
    private lateinit var dataLoginModel: DataLoginModel
    private lateinit var bottomNav : AnimatedBottomBar
    private lateinit var viewModel : DetailStoryViewModel

    companion object {
        const val EXTRA_URL = "extra_url"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_TIME= "extra_time"
        const val EXTRA_DESC = "extra_desc"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //getintent
        val url = intent.getStringExtra(EXTRA_URL)
        val name = intent.getStringExtra(EXTRA_NAME)
        val time = intent.getStringExtra(EXTRA_TIME)
        val desc = intent.getStringExtra(EXTRA_DESC)

        //inisialisasi
        viewModel = ViewModelProvider(this)[DetailStoryViewModel::class.java]
        mLoginPref = LoginPreference(this)
        dataLoginModel = mLoginPref.getDataLogin()
        bottomNav = findViewById(R.id.bottom_bar)

        if (url != null && name != null && time != null && desc != null) {
            setUI(url, name, time, desc)
        }
        navAction()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUI(url: String, name: String, time : String, desc: String){
        binding.apply{
            Glide.with(this@DetailStoryActivity)
                .load(url)
                .centerCrop()
                .into(ivFotodetail)
            tvUploadtime.text = binding.root.resources.getString(R.string.uploadtime, viewModel.setUploadTime(time))
            tvUsername.text = name
            tvDesc.text = desc
        }
    }

    private fun navAction() {
        bottomNav.onTabSelected = {
            when(it.title){
                "Home" -> {
                    val intToAdd = Intent(this, MainActivity::class.java)
                    startActivity(
                        intToAdd,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this@DetailStoryActivity).toBundle()
                    )
                }
                "Add Story" -> {
                    val intToAdd = Intent(this, AddStoryActivity::class.java)
                    startActivity(
                        intToAdd,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this@DetailStoryActivity).toBundle()
                    )
                }
                "Logout" -> {
                    mLoginPref.delDataLogin()
                    Toast.makeText(this, getString(R.string.logout_success), Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intToLogin = Intent(this, LoginScreenActivity::class.java)
                        startActivity(
                            intToLogin,
                            ActivityOptionsCompat.makeSceneTransitionAnimation(this@DetailStoryActivity).toBundle()
                        )
                        finishAffinity()
                    }, MainActivity.delay)
                }
                "Map Story" -> {
                    val intToMap = Intent(this, MapStoryActivity::class.java)
                    startActivity(
                        intToMap,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this@DetailStoryActivity).toBundle()
                    )
                }
            }
        }
    }
}
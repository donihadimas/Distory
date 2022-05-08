package com.hadimas.distories

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import com.hadimas.distories.databinding.ActivitySplashScreenBinding
import com.hadimas.distories.preferences.DataLoginModel
import com.hadimas.distories.preferences.LoginPreference

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var mLoginPref: LoginPreference
    private lateinit var dataLoginModel: DataLoginModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mLoginPref = LoginPreference(this)
        dataLoginModel = mLoginPref.getDataLogin()

        Handler(Looper.getMainLooper()).postDelayed({
            isLogin()
        }, delay)
        splashAnim()
    }

    private fun splashAnim() {
        ObjectAnimator.ofFloat(binding.ivLogo, View.TRANSLATION_X, -50f, 50f).apply {
            duration = 5000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    private fun isLogin(){
        if (dataLoginModel.isLogin){
            val intToMain = Intent(this@SplashScreenActivity, MainActivity::class.java)
            startActivity(intToMain, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle())
            finish()
        }else{
            val intToLogin = Intent(this, LoginScreenActivity::class.java)
            startActivity(intToLogin, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle())
            finish()
        }
    }

    companion object {
        const val delay = 2000L
    }
}
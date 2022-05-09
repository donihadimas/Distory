package com.hadimas.distories

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hadimas.distories.databinding.ActivityLoginScreenBinding
import com.hadimas.distories.preferences.DataLoginModel
import com.hadimas.distories.preferences.LoginPreference
import com.hadimas.distories.viewmodel.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginScreenBinding
    private lateinit var btnLogin: Button
    private lateinit var btnSignup: Button
    private lateinit var viewModel: LoginViewModel
    private lateinit var dataLoginModel: DataLoginModel
    private lateinit var loginPref: LoginPreference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        btnLogin = binding.btnLogin
        btnSignup = binding.btnSignup
        loginPref = LoginPreference(this)
        dataLoginModel = DataLoginModel()

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(loginPref)
        )[LoginViewModel::class.java]

        loginAnim()
        btnAction()
    }


    private fun btnAction() {

        btnLogin.setOnClickListener {
            binding.pgBarLogin.visibility = View.VISIBLE
            if (binding.edtEmail.text.toString().isNotEmpty() && binding.edtPass.text.toString()
                    .isNotEmpty()
            ) {
                binding.pgBarLogin.visibility = View.VISIBLE
                lifecycleScope.launch(Dispatchers.Default) {
                    viewModel.postLogin(
                        binding.edtEmail.text.toString(),
                        binding.edtPass.text.toString()
                    )
                    withContext(Dispatchers.Main) {
                        viewModel.getResultResponse().observe(this@LoginScreenActivity) {
                            if (it == true) {
                                val intToMain = Intent(this@LoginScreenActivity, MainActivity::class.java)
                                startActivity(
                                    intToMain,
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(this@LoginScreenActivity)
                                        .toBundle()
                                )
                            } else {
                                Toast.makeText(
                                    this@LoginScreenActivity,
                                    getString(R.string.not_valid),
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.apply {
                                    pgBarLogin.visibility = View.GONE
                                    edtEmail.requestFocus()
                                }
                            }
                        }
                    }
                }


            } else {
                Toast.makeText(this, getString(R.string.empty_password), Toast.LENGTH_LONG)
                    .show()
                if (binding.edtEmail.text.toString().isEmpty()) {
                    binding.edtEmail.requestFocus()
                } else if (binding.edtPass.text.toString().isEmpty()) {
                    binding.edtPass.requestFocus()
                }

            }
        }

        btnSignup.setOnClickListener {
            val intToRegis = Intent(this, RegisterScreenActivity::class.java)
            startActivity(
                intToRegis,
                ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
            )
        }
    }

    private fun loginAnim() {
        val edtmail = ObjectAnimator.ofFloat(binding.edtEmail, View.ALPHA, 1f).setDuration(500)
        val edtpass = ObjectAnimator.ofFloat(binding.edtPass, View.ALPHA, 1f).setDuration(500)
        val btnlogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val btnsignup = ObjectAnimator.ofFloat(binding.btnSignup, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(edtmail, edtpass, btnlogin, btnsignup)
            start()
        }
    }
}

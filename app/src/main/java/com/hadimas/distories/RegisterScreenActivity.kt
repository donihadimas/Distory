package com.hadimas.distories

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hadimas.distories.databinding.ActivityRegisterScreenBinding
import com.hadimas.distories.viewmodel.RegisterViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterScreenBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        btnAction()
    }

    private fun btnAction(){
        binding.btnSignup.setOnClickListener {
            if (binding.edtNama.text.toString().isNotEmpty() && binding.edtEmail.text.toString()
                    .isNotEmpty() && binding.edtPass.text.toString().isNotEmpty()
            ) {
                binding.pgBarRegis.visibility = View.VISIBLE
                lifecycleScope.launch(Dispatchers.Default){
                    viewModel.postRegis(binding.edtNama.text.toString(), binding.edtEmail.text.toString(), binding.edtPass.text.toString())
                    withContext(Dispatchers.Main){
                        viewModel.getResultResponse().observe(this@RegisterScreenActivity){
                            if(it == true){
                                Toast.makeText(this@RegisterScreenActivity, getString(R.string.account_created), Toast.LENGTH_LONG).show()
                                Handler(Looper.getMainLooper()).postDelayed({
                                    val intToLogin = Intent(this@RegisterScreenActivity, LoginScreenActivity::class.java)
                                    startActivity(
                                        intToLogin,
                                        ActivityOptionsCompat.makeSceneTransitionAnimation(this@RegisterScreenActivity).toBundle())
                                    finishAffinity()
                                }, delay)
                            }else{
                                Toast.makeText(this@RegisterScreenActivity, getString(R.string.already_exist), Toast.LENGTH_LONG).show()
                                binding.pgBarRegis.visibility = View.GONE
                            }
                        }
                    }
                }

            } else {
                Toast.makeText(
                    this,
                    getString(R.string.field_empty),
                    Toast.LENGTH_LONG
                )
                    .show()
                when {
                    binding.edtEmail.text.toString().isEmpty() -> {
                        binding.edtEmail.requestFocus()
                    }
                    binding.edtPass.text.toString().isEmpty() -> {
                        binding.edtPass.requestFocus()
                    }
                    else -> {
                        binding.edtNama.requestFocus()
                    }
                }
            }
        }
    }

    companion object{
        const val delay = 100L
    }
}
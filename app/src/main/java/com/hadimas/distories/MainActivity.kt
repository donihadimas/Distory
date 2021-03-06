package com.hadimas.distories

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import android.widget.ToggleButton
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hadimas.distories.adapter.LoadingStateAdapter
import com.hadimas.distories.adapter.StoryAdapter
import com.hadimas.distories.databinding.ActivityMainBinding
import com.hadimas.distories.preferences.DataLoginModel
import com.hadimas.distories.preferences.LoginPreference
import com.hadimas.distories.response.ListStoryItem
import com.hadimas.distories.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import nl.joery.animatedbottombar.AnimatedBottomBar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mLoginPref: LoginPreference
    private lateinit var dataLoginModel: DataLoginModel
    private lateinit var viewModel: MainViewModel
    private lateinit var adapterList: StoryAdapter
    private lateinit var bottomNav : AnimatedBottomBar
    private lateinit var btnLang: ToggleButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mLoginPref = LoginPreference(this)
        dataLoginModel = mLoginPref.getDataLogin()
        btnLang = findViewById(R.id.lang_toggle)
        bottomNav = findViewById(R.id.bottom_bar)

        bottomNav.onTabSelected = {
            when(it.title){
                "Home" -> {
                    Toast.makeText(this, R.string.menu_home, Toast.LENGTH_SHORT).show()
                }
                "Add Story" -> {
                    val intToAdd = Intent(this, AddStoryActivity::class.java)
                    startActivity(
                        intToAdd,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity).toBundle()
                    )
                }
                "Logout" -> {
                    mLoginPref.delDataLogin()
                    Toast.makeText(this, getString(R.string.logout_success), Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intToLogin = Intent(this, LoginScreenActivity::class.java)
                        startActivity(
                            intToLogin,
                            ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity).toBundle()
                        )
                        finishAffinity()
                    }, delay)
                }
                "Map Story" -> {
                    val intToMap = Intent(this, MapStoryActivity::class.java)
                    startActivity(
                        intToMap,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity).toBundle()
                    )
                }
            }

        }

        binding.pgBarMain.visibility = View.VISIBLE
        setRView()

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(mLoginPref)
        )[MainViewModel::class.java]

        lifecycleScope.launchWhenCreated {
            binding.rvListStories.adapter = adapterList.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    adapterList.retry()
                }
            )
            viewModel.getListStory().collectLatest {
                adapterList.submitData(it)
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            binding.pgBarMain.visibility = View.GONE
        }, delay)

        btnLang.setOnClickListener{
            if (btnLang.isChecked){
                Toast.makeText(this, getString(R.string.not_implement), Toast.LENGTH_LONG).show()
            }
        }

        adapterList.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem) {
                val intToDetail = Intent(this@MainActivity,  DetailStoryActivity::class.java)
                intToDetail.putExtra(DetailStoryActivity.EXTRA_URL, data.photoUrl)
                intToDetail.putExtra(DetailStoryActivity.EXTRA_NAME, data.name)
                intToDetail.putExtra(DetailStoryActivity.EXTRA_TIME, data.createdAt)
                intToDetail.putExtra(DetailStoryActivity.EXTRA_DESC, data.description)
                startActivity(
                    intToDetail,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity).toBundle()
                )
            }
        })
    }

    private fun setRView(){
        binding.rvListStories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapterList = StoryAdapter()
            adapter = adapterList
        }
    }

    companion object{
        const val delay = 1000L
    }

}


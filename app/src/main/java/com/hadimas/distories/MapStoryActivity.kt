package com.hadimas.distories

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.hadimas.distories.databinding.ActivityMapStoryBinding
import com.hadimas.distories.preferences.DataLoginModel
import com.hadimas.distories.preferences.LoginPreference
import com.hadimas.distories.viewmodel.MapStoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nl.joery.animatedbottombar.AnimatedBottomBar

class MapStoryActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapStoryBinding
    private lateinit var bottomNav: AnimatedBottomBar
    private lateinit var mLoginPref: LoginPreference
    private lateinit var dataLoginModel: DataLoginModel
    private lateinit var viewModel: MapStoryViewModel

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mLoginPref = LoginPreference(this)
        dataLoginModel = mLoginPref.getDataLogin()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        bottomNav = findViewById(R.id.bottom_bar)

        navigateNav()

        supportActionBar?.title = getString(R.string.story_map_text)

        viewModel = ViewModelProvider(this)[MapStoryViewModel::class.java]

    }

    override fun onMapReady(googleMap: GoogleMap) {

        setUiMap(googleMap)

        lifecycleScope.launch(Dispatchers.Default) {
            viewModel.setMapStory(dataLoginModel.token.toString())
            withContext(Dispatchers.Main){
                showStoryMap()
            }
        }

        mMap.setOnInfoWindowClickListener(this)
        getMyLocation()
        setStyleMap()
    }

    private fun navigateNav() {
        bottomNav.onTabSelected = {
            when (it.title) {
                "Home" -> {
                    val intToMain = Intent(this, MainActivity::class.java)
                    startActivity(
                        intToMain,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this@MapStoryActivity)
                            .toBundle()
                    )
                }
                "Add Story" -> {
                    val intToAdd = Intent(this, AddStoryActivity::class.java)
                    startActivity(
                        intToAdd,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this@MapStoryActivity)
                            .toBundle()
                    )
                }
                "Logout" -> {
                    mLoginPref.delDataLogin()
                    Toast.makeText(this, getString(R.string.logout_success), Toast.LENGTH_SHORT)
                        .show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intToLogin = Intent(this, LoginScreenActivity::class.java)
                        startActivity(
                            intToLogin,
                            ActivityOptionsCompat.makeSceneTransitionAnimation(this@MapStoryActivity)
                                .toBundle()
                        )
                        finishAffinity()
                    }, MainActivity.delay)
                }
                "Map Story" -> {
                    val intToMap = Intent(this, MapStoryActivity::class.java)
                    startActivity(
                        intToMap,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this@MapStoryActivity)
                            .toBundle()
                    )
                }
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun showStoryMap() {
        val boundsBuilder = LatLngBounds.Builder()
        val iconMarker = BitmapDescriptorFactory.fromResource(R.drawable.markericon)
        viewModel.getMapStory().observe(this@MapStoryActivity) {
            if (it.isNotEmpty()) {
                it.forEach { dataStory ->
                    val position = LatLng(dataStory.lat, dataStory.lon)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(position)
                            .title(dataStory.name)
                            .snippet(dataStory.description)
                            .icon(iconMarker)
                    )
                    boundsBuilder.include(position)
                    val bounds: LatLngBounds = boundsBuilder.build()
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10))
                }
            }
        }
    }

    override fun onInfoWindowClick(marker: Marker) {
        Toast.makeText(this, getString(R.string.notimplement), Toast.LENGTH_SHORT).show()
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setUiMap(googleMap: GoogleMap){
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
    }

    private fun setStyleMap() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }
}
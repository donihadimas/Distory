package com.hadimas.distories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hadimas.distories.preferences.LoginPreference
import com.hadimas.distories.viewmodel.LoginViewModel
import com.hadimas.distories.viewmodel.MainViewModel

class ViewModelFactory(private val pref: LoginPreference, private val context: Context) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }
//            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
//                MainViewModel(Injection.provideRepository(context), pref) as T
//            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}
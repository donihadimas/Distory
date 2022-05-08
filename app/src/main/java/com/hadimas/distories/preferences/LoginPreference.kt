package com.hadimas.distories.preferences

import android.content.Context

class LoginPreference(context: Context) {
    private val pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setDataLogin(data: DataLoginModel){
        val editor = pref.edit()

        editor.apply {
            putString(USER_ID, data.userId)
            putString(NAME, data.name)
            putString(TOKEN, data.token)
            putBoolean(IS_LOGIN, data.isLogin)
            apply()
        }
    }

    fun getDataLogin(): DataLoginModel{
        val model = DataLoginModel()

        model.apply {
            userId = pref.getString(USER_ID, "")
            name = pref.getString(NAME, "")
            token = pref.getString(TOKEN, "")
            isLogin = pref.getBoolean(IS_LOGIN, false)

            return model
        }
    }
    fun delDataLogin(){
        val editor = pref.edit()
        editor.clear().apply()
    }

    companion object{
        private const val PREFS_NAME = "login_pref"
        private const val USER_ID = "userid"
        private const val NAME = "name"
        private const val TOKEN = "token"
        private const val IS_LOGIN = "islogin"
    }

}
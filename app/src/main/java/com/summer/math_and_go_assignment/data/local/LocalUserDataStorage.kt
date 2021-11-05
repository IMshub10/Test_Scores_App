package com.summer.math_and_go_assignment.data.local

import android.content.Context

object LocalUserDataStorage {
    @JvmStatic
    fun loadUserData(context: Context): PreferenceModel {
        val sharedPreferences = context.getSharedPreferences("local_user", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("emailId", null)
        return PreferenceModel(email)
    }

    @JvmStatic
    fun saveUserData(context: Context, emailId: String) {
        val sharedPreferences = context.getSharedPreferences("local_user", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("emailId", emailId)
        editor.apply()
    }
}
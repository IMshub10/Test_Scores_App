package com.summer.math_and_go_assignment.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.summer.math_and_go_assignment.data.local.LocalUserDataStorage
import com.summer.math_and_go_assignment.databinding.ActivitySplashScreenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLoadStartingActivity()
    }

    private fun initLoadStartingActivity() {
        CoroutineScope(Dispatchers.IO).launch {
            val email = LocalUserDataStorage.loadUserData(this@SplashScreenActivity).email
            if (email == null) {
                startActivity(Intent(this@SplashScreenActivity, RegisterActivity::class.java))
            } else {
                startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
            }
        }
    }
}
package com.summer.math_and_go_assignment.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns.EMAIL_ADDRESS
import android.widget.Toast
import com.summer.math_and_go_assignment.data.local.LocalUserDataStorage
import com.summer.math_and_go_assignment.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.mbRegister.setOnClickListener {
            val emailString = binding.etEmail.text!!.trim().toString()
            val emailPatternMatches = EMAIL_ADDRESS.matcher(emailString).matches()
            if (emailPatternMatches) {
                LocalUserDataStorage.saveUserData(this, emailString)
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        val endActivity = Intent(Intent.ACTION_MAIN)
        endActivity.addCategory(Intent.CATEGORY_HOME)
        startActivity(endActivity)
    }
}
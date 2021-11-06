package com.summer.math_and_go_assignment.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.summer.math_and_go_assignment.R
import com.summer.math_and_go_assignment.data.local.LocalUserDataStorage
import com.summer.math_and_go_assignment.databinding.ActivityRegisterBinding
import com.summer.math_and_go_assignment.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBindings()
        initView()
    }

    private fun initBindings() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        binding.lifecycleOwner = this
        binding.model = registerViewModel
        setContentView(binding.root)
    }

    private fun initView() {
        registerViewModel.validEmailAddress.observe(this, {
            if (it) {
                val emailString = registerViewModel.emailAddress.value!!.trim()
                LocalUserDataStorage.saveUserData(this, emailString)
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onBackPressed() {
        val endActivity = Intent(Intent.ACTION_MAIN)
        endActivity.addCategory(Intent.CATEGORY_HOME)
        startActivity(endActivity)
    }
}
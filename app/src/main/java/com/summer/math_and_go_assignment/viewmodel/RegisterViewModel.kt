package com.summer.math_and_go_assignment.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.MutableLiveData


@HiltViewModel
class RegisterViewModel @Inject constructor() : ViewModel() {

    val emailAddress = MutableLiveData<String>()
    val validEmailAddress = MutableLiveData<Boolean>()

    fun onRegisterClick() {
        val emailString = emailAddress.value!!.trim()
        val emailPatternMatches = Patterns.EMAIL_ADDRESS.matcher(emailString).matches()
        validEmailAddress.value = emailPatternMatches
    }
}
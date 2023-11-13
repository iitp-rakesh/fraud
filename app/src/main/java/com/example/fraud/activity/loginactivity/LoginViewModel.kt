package com.example.fraud.activity.loginactivity

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.fraud.activity.MyRepository

class LoginViewModel: ViewModel() {
    private val repository = MyRepository()

    fun register(
        accountNumber: String,
        debitCardNumber: String,
        expiryDate: String,
        cvv: String,
        pin: String,
        callback: (Boolean) -> Unit
    ) {
        repository.checkRegistration(debitCardNumber, accountNumber, cvv, expiryDate, pin) {
            Log.d(TAG, "Register Model: $it")
            if (it) {
                callback(true)
            } else {
                callback(false)
            }
        }
    }

    fun createAccount(
        accountNumber: String,
        password:String
    ) {
        repository.createAccount(accountNumber,password)
    }

    fun login(accountNumber: String, password:String,callback: (Boolean) -> Unit) {
        repository.login(accountNumber,password){
            if(it){
                callback(true)
            }
            else{
                callback(false)
            }
        }
    }
}

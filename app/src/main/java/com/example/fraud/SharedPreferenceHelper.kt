package com.example.fraud

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceHelper(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val IS_LOGGED_IN = "isLoggedIn"
        private const val ACCOUNT_NUMBER = "accountNumber"
        private const val DEBIT_CARD_NUMBER = "debitCardNumber"
        private const val BALANCE="balance"
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun getAccount(): String? {
        return sharedPreferences.getString(ACCOUNT_NUMBER, null)
    }

    fun setAccount(account: String) {
        sharedPreferences.edit().putString(ACCOUNT_NUMBER, account).apply()
    }

    fun getDebitCardNumber(): String? {
        return sharedPreferences.getString(DEBIT_CARD_NUMBER, null)
    }

    fun setDebitCardNumber(debitCardNumber: String) {
        sharedPreferences.edit().putString(DEBIT_CARD_NUMBER, debitCardNumber).apply()
    }

    fun setBalance(it: Double) {
        sharedPreferences.edit().putString(BALANCE, it.toString()).apply()
    }
    fun getBalance(): Double? {
        return sharedPreferences.getString(BALANCE, null)?.toDouble()
    }
}

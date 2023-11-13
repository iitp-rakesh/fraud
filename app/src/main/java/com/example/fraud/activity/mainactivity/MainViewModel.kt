package com.example.fraud.activity.mainactivity

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fraud.SharedPreferenceHelper
import com.example.fraud.activity.MyRepository
import com.example.fraud.model.Customer
import com.example.fraud.model.Transaction
import com.google.firebase.database.DatabaseError
import kotlinx.coroutines.launch

class MainViewModel(context: Context) : ViewModel() {
    private val sharedPreferenceHelper = SharedPreferenceHelper(context)
    private val repository = MyRepository()

    private val _transactionList = MutableLiveData<List<Transaction>>()
    val transactionList: LiveData<List<Transaction>> = _transactionList

    private val _customer = MutableLiveData<Customer>()
    val customer: LiveData<Customer> = _customer

    private var _debitCardNumber: String? = null
    val debitCardNumbe: String? = _debitCardNumber

    fun setDebitCardNumber(debitCardNumber: String) {
        _debitCardNumber = debitCardNumber
    }

    fun getDebitCardNumber(): String? {
        return _debitCardNumber
    }

    fun getCustomerDetails(accountNumber: String) {
        repository.getCustomerDetails(accountNumber) {
            _customer.value = it
        }
    }

    fun updateCustomerDetails(customer: Customer) {
        repository.updateCustomerDetails(customer)
    }

    fun loadTransactions(debitCardNumber: String) {
        repository.getTransactions(debitCardNumber, object : MyRepository.TransactionCallback {
            override fun onTransactionDataReceived(transactions: List<Transaction>) {
                _transactionList.value = transactions
            }

            override fun onTransactionDataError(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    fun pushTransaction(transaction: Transaction,accountNumber: String, debitCardNumber: String,balance: Double) {
        Log.d("Transaction", "Pushing transaction $transaction to $debitCardNumber")
        viewModelScope.launch {
            repository.pushTransaction(transaction,accountNumber, debitCardNumber,balance)
        }
    }
    fun updateBalance(debitCardNumber: String, balance: Double) {
        viewModelScope.launch {
            repository.updateBalance(debitCardNumber, balance)
        }
    }
    fun getBalance(debitCardNumber: String, callback: (Double) -> Unit) {
        repository.getBalance(debitCardNumber) {
            callback(it)
        }
    }
}

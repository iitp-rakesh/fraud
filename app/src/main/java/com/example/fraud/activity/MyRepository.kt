package com.example.fraud.activity

import android.content.ContentValues.TAG
import android.util.Log
import com.example.fraud.model.Customer
import com.example.fraud.model.Transaction
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MyRepository {
    private val database = Firebase.database
    fun getTransactions(debitCardNumber: String, callback: TransactionCallback) {
        val myRef = database.getReference("Transactions/debitCardTransaction/$debitCardNumber")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val transactionList = mutableListOf<Transaction>()
                for (childSnapshot in snapshot.children) {
                    Log.d("Transactions", "Transaction $childSnapshot")
                    val data = childSnapshot.value as HashMap<*, *>
                    val amt = if (data["amt"] is Long) {
                        val x = data["amt"] as Long
                        x.toDouble()
                    } else {
                        data["amt"] as Double
                    }

                    val merch_lat = if (data["merch_lat"] is Long) {
                        val x = data["merch_lat"] as Long
                        x.toDouble()
                    } else {
                        data["merch_lat"] as Double
                    }
                    val merch_long = if (data["merch_long"] is Long) {
                        val x = data["merch_long"] as Long
                        x.toDouble()
                    } else {
                        data["merch_long"] as Double
                    }
                    val transaction = Transaction(
                        data["transactionID"] as String,
                        data["zip"] as Long,
                        data["gender"] as Long,
                        data["city"] as Long,
                        data["state"] as Long,
                        data["category"] as Long,
                        amt.toDouble(),
                        data["merchant"] as String,
                        data["lat"] as Double,
                        data["long"] as Double,
                        merch_lat,
                        merch_long,
                        data["city_pop"] as Long,
                        data["unix_time"] as Long,
                        data["transTime"] as String
                    )
                    transactionList.add(transaction)
                }
                // Sort the transactionList in descending order based on unix_time
                val sortedTransactionList = transactionList.sortedByDescending { it.unix_time }
                callback.onTransactionDataReceived(sortedTransactionList)
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onTransactionDataError(error)
            }
        })
    }

    fun getCustomerDetails(accountNumber: String, callback: (Customer?) -> Unit) {
        database.reference.child("Customer").child(accountNumber)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val firstName =
                        dataSnapshot.child("firstName").getValue(String::class.java) ?: ""
                    val lastName = dataSnapshot.child("lastName").getValue(String::class.java) ?: ""
                    val dateOfBirth =
                        dataSnapshot.child("dateOfBirth").getValue(String::class.java) ?: ""
                    val debitCardNumber =
                        dataSnapshot.child("debitCardNumber").getValue(String::class.java) ?: ""
                    val gender = dataSnapshot.child("gender").getValue(String::class.java) ?: ""
                    val email = dataSnapshot.child("email").getValue(String::class.java) ?: ""
                    val phone = dataSnapshot.child("phone").getValue(String::class.java) ?: ""
                    val address = dataSnapshot.child("address").getValue(String::class.java) ?: ""
                    val city = dataSnapshot.child("city").getValue(String::class.java) ?: ""
                    val state = dataSnapshot.child("state").getValue(String::class.java) ?: ""
                    val zip = dataSnapshot.child("zip").getValue(String::class.java) ?: ""
                    val country = dataSnapshot.child("country").getValue(String::class.java) ?: ""
                    val occupation =
                        dataSnapshot.child("occupation").getValue(String::class.java) ?: ""
                    val balance = dataSnapshot.child("balance").getValue(Double::class.java) ?: 0.0

                    // Create a Customer object
                    val customer = Customer(
                        accountNumber,
                        firstName,
                        lastName,
                        dateOfBirth,
                        debitCardNumber,
                        gender,
                        email,
                        phone,
                        address,
                        city,
                        state,
                        zip,
                        country,
                        occupation,
                        balance
                    )
                    Log.d("Customer Details", "$customer")
                    callback(customer)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Firebase", "Error fetching data", databaseError.toException())
                    callback(null)
                }
            })
    }

    fun updateCustomerDetails(customer: Customer) {
        database.reference.child("Customer").child(customer.accountNumber).setValue(customer)
    }

    fun checkRegistration(
        debitCardNumber: String,
        accountNumber: String,
        cvv: String,
        expiryDate: String,
        pin: String,
        callback: (Boolean) -> Unit
    ) {
        database.reference.child("debitCard/$debitCardNumber").get()
            .addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val dataAccountNumber =
                        dataSnapshot.child("accountNumber").getValue(String::class.java) ?: ""
                    val dataCvv = dataSnapshot.child("cvv").getValue(String::class.java) ?: ""
                    val dataExpiryDate =
                        dataSnapshot.child("expiryDate").getValue(String::class.java) ?: ""
                    val dataPin = dataSnapshot.child("pin").getValue(String::class.java) ?: ""

                    Log.d(
                        TAG,
                        "Checking Registration: $dataAccountNumber $dataCvv $dataExpiryDate $dataPin"
                    )
                    Log.d(TAG, "Checking Registration: $accountNumber $cvv $expiryDate $pin")

                    if (dataAccountNumber == accountNumber && dataCvv == cvv && dataExpiryDate == expiryDate && dataPin == pin) {
                        Log.d(TAG, "Checking Success")
                        callback(true)
                    } else {
                        Log.d(TAG, "Checking False")
                        callback(false)
                    }
                }
            }

    }

    fun createAccount(accountNumber: String, password: String) {
        database.reference.child("CustomerAccount").child(accountNumber).child("password")
            .setValue(password)
    }

    fun login(accountNumber: String, password: String, callback: (Boolean) -> Unit) {
        database.reference.child("CustomerAccount/$accountNumber").get().addOnSuccessListener {
            if (it.exists()) {
                val pass = it.child("password").getValue(String::class.java) ?: ""
                if (password == pass) {
                    callback(true)
                } else {
                    callback(false)
                }
            } else {
                callback(false)
            }
        }
    }

    fun pushTransaction(transaction: Transaction, accountNumber: String, debitCardNumber: String,balance: Double) {
        database.reference.child("Transactions/debitCardTransaction/${debitCardNumber}/${transaction.transactionID}")
            .setValue(transaction).addOnSuccessListener {
            Log.d(TAG, "Transaction successful $debitCardNumber")
            updateBalance(accountNumber, balance - transaction.amt)
        }.addOnFailureListener {
            Log.d(TAG, "Transaction failed $debitCardNumber")
        }
    }

    fun updateBalance(accountNumber: String, amount: Double) {
        database.reference.child("Customer/$accountNumber/balance").setValue(amount)
    }

    fun getBalance(debitCardNumber: String, callback: (Double) -> Unit) {
        database.reference.child("Customer").child(debitCardNumber).child("balance")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val balance = dataSnapshot.getValue(Double::class.java) ?: 0.0
                    Log.d("Balance", "$balance")
                    callback(balance)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Firebase", "Error fetching data", databaseError.toException())
                }
            })
    }

    interface TransactionCallback {
        fun onTransactionDataReceived(transactions: List<Transaction>)
        fun onTransactionDataError(error: DatabaseError)
    }

}

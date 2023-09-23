package com.example.fraud

import com.example.fraud.model.RegistrationResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RetrofitInterface {
    @FormUrlEncoded
    @POST("/register")
    fun register(
        @Field("account_number") accountNumber: String,
        @Field("debit_card_number") debitCardNumber: String,
        @Field("expiry_date") expiryDate: String,
        @Field("cvv") cvv: String,
        @Field("pin") pin: String
    ): Call<RegistrationResponse>

    @FormUrlEncoded
    @POST("/createAccount")
    fun createAccount(
        @Field("account_number") accountNumber: String,
        @Field("password") password: String
    ): Call<RegistrationResponse>

    @FormUrlEncoded
    @POST("/login")
    fun login(
        @Field("account_number") accountNumber: String,
        @Field("password") password: String
    ): Call<RegistrationResponse>
}

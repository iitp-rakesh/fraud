package com.example.fraud.activity.loginactivity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.fraud.MainActivity
import com.example.fraud.R
import com.example.fraud.RetrofitInterface
import com.example.fraud.databinding.FragmentLoginBinding
import com.example.fraud.model.RegistrationResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var registrationService : RetrofitInterface
//    private val baseurl = "http://fraudetect.onrender.com"
    private val baseurl = "http://10.0.2.2:3000"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentLoginBinding.bind(view)
        val signUpButton = binding.signupButton

        signUpButton.setOnClickListener {
            view.findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }
        //retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(baseurl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        registrationService = retrofit.create(RetrofitInterface::class.java)

        binding.loginButton.setOnClickListener {
            val accountNumber = binding.accountNumberEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            checkLoginCredentials(accountNumber, password)
        }
    }

    private fun checkLoginCredentials(accountNumber: String, password: String) {
        Log.d(TAG, "$accountNumber $password")
        val call= registrationService.login(accountNumber, password)
        call.enqueue(object : Callback<RegistrationResponse> {
            override fun onResponse(
                call: Call<RegistrationResponse>,
                response: Response<RegistrationResponse>
            ) {
                if (response.isSuccessful) {
                    val registrationResponse = response.body()
                    if (registrationResponse != null) {
                        if (registrationResponse.success) {
//                            view?.findNavController()?.navigate(R.id.action_loginFragment_to_homeFragment)
                            Toast.makeText(context, registrationResponse.message, Toast.LENGTH_LONG).show()
                            startActivity(Intent(context, MainActivity::class.java))
                        } else {
                            Toast.makeText(context, registrationResponse.message, Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Error occurred", Toast.LENGTH_LONG).show()
                    Log.e("Error", response.message())
                }
            }

            override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }
}

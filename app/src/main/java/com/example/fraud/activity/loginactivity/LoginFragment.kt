package com.example.fraud.activity.loginactivity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.fraud.R
import com.example.fraud.RetrofitInterface
import com.example.fraud.SharedPreferenceHelper
import com.example.fraud.activity.mainactivity.MainActivity
import com.example.fraud.databinding.FragmentLoginBinding
import com.example.fraud.model.RegistrationResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var registrationService: RetrofitInterface
    private lateinit var loginViewModel: LoginViewModel

//        private val baseurl = "https://fraudetect.onrender.com"
    private val baseurl = "http://10.0.2.2:3000"
    private lateinit var sharedPreferencesHelper: SharedPreferenceHelper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        loginViewModel=LoginViewModel()
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

        sharedPreferencesHelper = SharedPreferenceHelper(requireContext())
        binding.loginButton.setOnClickListener {
            val accountNumber = binding.accountNumberEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
//            checkLoginCredentials(accountNumber, password)
            loginViewModel.login(accountNumber, password){
                if(it){
                    sharedPreferencesHelper.setLoggedIn(true)
                    val intent=Intent(context,MainActivity::class.java)
                    intent.putExtra("accountNumber",accountNumber)
                    Toast.makeText(context,"Login Successful", Toast.LENGTH_LONG).show()
                    startActivity(intent)
                }
                else{
                    Toast.makeText(context,"Incorrect Credentials", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun checkLoginCredentials(accountNumber: String, password: String) {
        Log.d(TAG, "$accountNumber $password")
        val call = registrationService.login(accountNumber, password)
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
                            Toast.makeText(context, registrationResponse.message, Toast.LENGTH_LONG)
                                .show()
                            sharedPreferencesHelper.setLoggedIn(true)
                            startActivity(Intent(context, MainActivity::class.java))
                        } else {
                            Toast.makeText(context, registrationResponse.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                } else {
                    Toast.makeText(context,"Incorrect Credentials", Toast.LENGTH_LONG).show()
                    Log.e("Error","d"+ response.code().toString()+response.message().toString())
                }
            }

            override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                Toast.makeText(context, "d"+t.message, Toast.LENGTH_LONG).show()
            }
        })
    }
}

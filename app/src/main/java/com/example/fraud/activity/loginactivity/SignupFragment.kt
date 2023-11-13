package com.example.fraud.activity.loginactivity
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.fraud.R
import com.example.fraud.RetrofitInterface
import com.example.fraud.model.RegistrationResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignupFragment : Fragment() {

    private lateinit var accountNumberEditText: EditText
    private lateinit var debitCardNumberEditText: EditText
    private lateinit var expiryDateEditText: EditText
    private lateinit var cvvEditText: EditText
    private lateinit var pinEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var registrationService : RetrofitInterface
    private lateinit var loginViewModel: LoginViewModel
//    private val baseurl = "https://fraudetect.onrender.com"
    private val baseurl = "http://10.0.2.2:3000"
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)
        loginViewModel= LoginViewModel()


        accountNumberEditText = view.findViewById(R.id.accountNumberEditText)
        debitCardNumberEditText = view.findViewById(R.id.debitCardNumberEditText)
        expiryDateEditText = view.findViewById(R.id.expiryDateEditText)
        cvvEditText = view.findViewById(R.id.cvvEditText)
        pinEditText = view.findViewById(R.id.pinEditText)
        registerButton = view.findViewById(R.id.registerButton)

        //retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(baseurl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        registrationService = retrofit.create(RetrofitInterface::class.java)

        //registerButton
        registerButton.setOnClickListener {
            val accountNumber = accountNumberEditText.text.toString()
            val debitCardNumber = debitCardNumberEditText.text.toString()
            val expiryDate = expiryDateEditText.text.toString()
            val cvv = cvvEditText.text.toString()
            val pin = pinEditText.text.toString()
            // Call a function to make the HTTP request
//            makeRegistrationRequest(accountNumber, debitCardNumber, expiryDate, cvv, pin)
           loginViewModel.register(accountNumber,debitCardNumber,expiryDate,cvv,pin){
               Toast.makeText(context, "Register Clicked.", Toast.LENGTH_LONG).show()
                if(it){
                    Toast.makeText(context, "Registration Successful. Please create a password.", Toast.LENGTH_LONG).show()
                    showPasswordTextBox(accountNumber)
                }
                else{
                    Toast.makeText(context, "Registration failed. Please check your details.", Toast.LENGTH_LONG).show()
                }
            }
        }
        //Click here button to Login Page
        val clickHereButton = view.findViewById<TextView>(R.id.alreadyAccountTextView)
        clickHereButton.setOnClickListener {
            view.findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }
        return view
    }

    //retrofit function to make the HTTP request to check existing account
//    private fun makeRegistrationRequest(accountNumber: String, debitCardNumber: String, expiryDate: String, cvv: String, pin: String) {
//        //retrofit
//
//        val call = registrationService.register(accountNumber, debitCardNumber, expiryDate, cvv, pin)
//
//        call.enqueue(object : Callback<RegistrationResponse> {
//            override fun onResponse(call: Call<RegistrationResponse>, response: Response<RegistrationResponse>) {
//                if (response.isSuccessful) {
//                    val registrationResponse = response.body()
//                    if (registrationResponse != null && registrationResponse.success) {
//                        showPasswordTextBox(accountNumber)
//                    } else {
//                        Toast.makeText(context, registrationResponse?.message, Toast.LENGTH_LONG).show()
//                        // Registration failed, display an error message
//                        // You can update the UI here with the error message
//                    }
//                } else {
//                    Log.e("Registration Error", response.code().toString())
//                }
//            }
//
//            override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
//                // Log the error message for debugging
//                Log.e("Registration Error", t.message ?: "Unknown error")
//
//                // Display a user-friendly message
//                Toast.makeText(context, "Registration failed. Please check your internet connection.", Toast.LENGTH_LONG).show()
//            }
//
//        })
//    }

    //retrofit function to make the HTTP request to create account if exist
    private fun showPasswordTextBox(accountNumber: String) {
        val passwordInput = view?.findViewById<View>(R.id.passwordTextInputLayout)!!
        val confirmPasswordInput = view?.findViewById<View>(R.id.confirmPasswordTextInputLayout)!!
        passwordEditText= view?.findViewById(R.id.passwordEditText)!!
        confirmPasswordEditText= view?.findViewById(R.id.confirmPasswordEditText)!!
        passwordInput.visibility = View.VISIBLE
        confirmPasswordInput.visibility = View.VISIBLE
        registerButton.text= "Create Account"
        registerButton.setOnClickListener {
            val password= passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()
            if(password.length<8){
                Toast.makeText(context, "Password should be at least 8 characters", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (password == confirmPassword) {
                // Call a function to make the HTTP request
                Log.d("password", "$password $confirmPassword")

//                makeCreateAccountRequest(accountNumber,password)
                loginViewModel.createAccount(accountNumber,password)
                Toast.makeText(context, "Account Created Successfully. Please Login", Toast.LENGTH_LONG).show()
                requireView().findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
            } else {
                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun makeCreateAccountRequest(accountNumber: String,password: String) {
//        //retrofit
        val call = registrationService.createAccount(accountNumber,password)

        call.enqueue(object : Callback<RegistrationResponse> {
            override fun onResponse(call: Call<RegistrationResponse>, response: Response<RegistrationResponse>) {
                if (response.isSuccessful) {
                    val registrationResponse = response.body()
                    if (registrationResponse != null && registrationResponse.success) {
                        Toast.makeText(context, registrationResponse.message, Toast.LENGTH_LONG).show()
                        view!!.findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
                        // Registration successful, display a message
                        // You can update the UI here with the success message
                    } else {
                        Toast.makeText(context, registrationResponse?.message, Toast.LENGTH_LONG).show()
                        // Registration failed, display an error message
                        // You can update the UI here with the error message
                    }
                } else {
                    Log.e("Registration Error", response.code().toString())
                }
            }

            override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                // Log the error message for debugging
                Log.e("Registration Error", t.message ?: "Unknown error")

                // Display a user-friendly message
                Toast.makeText(context, "Registration failed. Please check your internet connection.", Toast.LENGTH_LONG).show()
            }

        })
    }
}

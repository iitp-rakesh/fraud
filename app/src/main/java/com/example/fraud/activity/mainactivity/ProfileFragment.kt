
package com.example.fraud.activity.mainactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fraud.R
import com.example.fraud.SharedPreferenceHelper
import com.example.fraud.databinding.FragmentPredictBinding
import com.example.fraud.model.Customer

class ProfileFragment : Fragment() {
    private val TAG = "ProfileFragment"
    private lateinit var binding: FragmentPredictBinding
    private lateinit var mainViewModel:MainViewModel
    private lateinit var sharedPreferenceHelper: SharedPreferenceHelper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Enable the options menu for this fragment

        return inflater.inflate(R.layout.fragment_predict, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPredictBinding.bind(view)
        mainViewModel=MainViewModel(requireContext())
        sharedPreferenceHelper=SharedPreferenceHelper(requireContext())
        binding.accountNumberInput.isEnabled=false
        binding.balanceInput.isEnabled=false
        setEditTextEnabled(false)
        val accountNumber=sharedPreferenceHelper.getAccount()
//        Toast.makeText(requireContext(),accountNumber.toString(), Toast.LENGTH_SHORT).show()
        var debitCardNumber:String?=null
        mainViewModel.getCustomerDetails(accountNumber.toString())
        mainViewModel.customer.observe(viewLifecycleOwner) {
            binding.accountNumberInput.setText(it.accountNumber)
            binding.firstNameInput.setText(it.firstName)
            binding.lastNameInput.setText(it.lastName)
            binding.dateOfBirthInput.setText(it.dateOfBirth)
            debitCardNumber=it.debitCardNumber
            binding.genderInput.setText(it.gender)
            binding.emailInput.setText(it.email)
            binding.phoneInput.setText(it.phone)
            binding.addressInput.setText(it.address)
            binding.cityInput.setText(it.city)
            binding.stateInput.setText(it.state)
            binding.zipInput.setText(it.zip)
            binding.countryInput.setText(it.country)
            binding.occupationInput.setText(it.occupation)
            binding.balanceInput.setText(it.balance.toString())
        }
        binding.saveButton.setOnClickListener {
            val accountNumber = binding.accountNumberInput.text.toString()
            val firstName = binding.firstNameInput.text.toString()
            val lastName = binding.lastNameInput.text.toString()
            val dateOfBirth = binding.dateOfBirthInput.text.toString()
            val gender = binding.genderInput.text.toString()
            val email = binding.emailInput.text.toString()
            val phone = binding.phoneInput.text.toString()
            val address = binding.addressInput.text.toString()
            val city = binding.cityInput.text.toString()
            val state = binding.stateInput.text.toString()
            val zip = binding.zipInput.text.toString()
            val country = binding.countryInput.text.toString()
            val occupation = binding.occupationInput.text.toString()
            val balance = binding.balanceInput.text.toString().toDoubleOrNull() ?: 0.0
            val customer = Customer(
                accountNumber,
                firstName,
                lastName,
                dateOfBirth,
                debitCardNumber!!,
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
            val btn = binding.saveButton.text.toString()
            if (btn == "Edit") {
                binding.saveButton.text = "Save"
                setEditTextEnabled(true)
            } else {
                binding.saveButton.text = "Edit"
                setEditTextEnabled(false)
                mainViewModel.updateCustomerDetails(customer)
            }
        }

    }

    private fun setEditTextEnabled(enabled: Boolean) {
        binding.firstNameInput.isEnabled = enabled
        binding.lastNameInput.isEnabled = enabled
        binding.dateOfBirthInput.isEnabled = enabled
        binding.genderInput.isEnabled = enabled
        binding.emailInput.isEnabled = enabled
        binding.phoneInput.isEnabled = enabled
        binding.addressInput.isEnabled = enabled
        binding.cityInput.isEnabled = enabled
        binding.stateInput.isEnabled = enabled
        binding.zipInput.isEnabled = enabled
        binding.countryInput.isEnabled = enabled
        binding.occupationInput.isEnabled = enabled
    }
}

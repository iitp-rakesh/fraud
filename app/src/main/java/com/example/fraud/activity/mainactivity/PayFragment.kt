package com.example.fraud.activity.mainactivity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fraud.R
import com.example.fraud.SharedPreferenceHelper
import com.example.fraud.databinding.FragmentHomeBinding
import com.example.fraud.model.Customer
import com.example.fraud.model.Transaction
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Calendar
import java.util.UUID

class PayFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double = 0.1
    private var longitude: Double = 0.1
    private lateinit var sharedPreferenceHelper: SharedPreferenceHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        sharedPreferenceHelper = SharedPreferenceHelper(requireContext())
        mainViewModel = MainViewModel(requireContext())
        var customer: Customer? = null
        //location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        mainViewModel.customer.observe(viewLifecycleOwner) {
            customer = it
        }
        mainViewModel.getCustomerDetails(sharedPreferenceHelper.getAccount().toString())
        getLastLocation()
        binding.payButton.setOnClickListener {
            if (customer != null) {
                val transaction = Transaction(
                    UUID.randomUUID().toString(),
                    customer!!.zip.toLong(),
                    if (customer!!.gender == "Male") 1L else 0L,
                    1L, 1L,
                    1L,
                    binding.amtEditText.text.toString().toDouble(),
                    binding.merchantEditText.text.toString(),
                    latitude,
                    longitude,
                    binding.merchLatEditText.text.toString().toDouble(),
                    binding.merchLongEditText.text.toString().toDouble(),
                    464665L,
                    Calendar.getInstance().timeInMillis,
                    Calendar.getInstance().time.toString()
                )
                Log.d(TAG, "onCreateView: $transaction")
                mainViewModel.pushTransaction(
                    transaction,
                    customer!!.accountNumber,
                    customer!!.debitCardNumber,
                    sharedPreferenceHelper.getBalance()!!
                )
            }
            else{
                Log.d(TAG, "onCreateView: customer is null")
            }
            findNavController().navigate(R.id.mainFragment)
        }
        return binding.root
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some situations, this can be null.
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                    // Use latitude and longitude as needed
                    // e.g., display on the UI, send to server, etc.
                }
            }
    }

    companion object {
        private const val TAG = "PayFragment"
    }
}

package com.example.fraud.activity.mainactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fraud.SharedPreferenceHelper
import com.example.fraud.databinding.FragmentTransactionInfoBinding
import com.example.fraud.model.Transaction

class TransactionInfo : Fragment() {
    private lateinit var binding: FragmentTransactionInfoBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var sharedPreferenceHelper: SharedPreferenceHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentTransactionInfoBinding.inflate(inflater,container,false)
        sharedPreferenceHelper=SharedPreferenceHelper(requireContext())
        mainViewModel = MainViewModel(requireContext())
        val transaction=arguments?.getSerializable("transaction") as Transaction
        binding.transactionId.text=transaction.transactionID
        binding.transactionAmount.text="$"+transaction.amt.toString()
        binding.transactionDate.text= transaction.transTime.removeRange(19,29)
        binding.merchantName.text="Paid to\n"+transaction.merchant
        binding.transactionMode.text="Credit Card\n${sharedPreferenceHelper.getDebitCardNumber()}"
        return binding.root
    }
}

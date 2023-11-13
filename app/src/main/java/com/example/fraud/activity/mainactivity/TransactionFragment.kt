package com.example.fraud.activity.mainactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fraud.R
import com.example.fraud.SharedPreferenceHelper

class TransactionFragment : Fragment() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var sharedPreferenceHelper: SharedPreferenceHelper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = MainViewModel(requireContext())
        sharedPreferenceHelper=SharedPreferenceHelper(requireContext())


        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        mainViewModel.transactionList.observe(viewLifecycleOwner) { transactions ->
            val adapter = RecyclerViewAdapter(context, transactions)
            recyclerView.adapter = adapter
        }
        mainViewModel.transactionList.observe(viewLifecycleOwner) { transactions ->
            val adapter = RecyclerViewAdapter(context, transactions)
            recyclerView.adapter = adapter
        }

        mainViewModel.customer.observe(viewLifecycleOwner) {
            if (it != null) {
                mainViewModel.setDebitCardNumber(it.debitCardNumber)
//                Toast.makeText(requireContext(), "M"+it.debitCardNumber, Toast.LENGTH_LONG).show()
                mainViewModel.loadTransactions(it.debitCardNumber)
            }
        }
        mainViewModel.getCustomerDetails(sharedPreferenceHelper.getAccount().toString())
    }
}

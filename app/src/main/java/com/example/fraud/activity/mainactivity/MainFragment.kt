package com.example.fraud.activity.mainactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fraud.R
import com.example.fraud.SharedPreferenceHelper
import com.example.fraud.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private lateinit var binding:FragmentMainBinding
    private lateinit var mainViewModel:MainViewModel
    private lateinit var sharedPreferenceHelper:SharedPreferenceHelper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentMainBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel= MainViewModel(requireContext())
        setHasOptionsMenu(true)

        binding.btnPay.setOnClickListener(){
            findNavController().navigate(R.id.homeFragment)
        }
        binding.tvSeeAll.setOnClickListener(){
            findNavController().navigate(R.id.transactionFragment)
        }
        sharedPreferenceHelper= SharedPreferenceHelper(requireContext())
        binding.rvTransaction.layoutManager= LinearLayoutManager(context)
        mainViewModel.getBalance(sharedPreferenceHelper.getAccount().toString()){
            binding.tvBalanceValue.text="$"+it.toString()
            sharedPreferenceHelper.setBalance(it)
        }
        mainViewModel.transactionList.observe(viewLifecycleOwner) { transactions ->
            transactions.take(7)
            val adapter = RecyclerViewAdapter(context, transactions)
            binding.rvTransaction.adapter = adapter
        }
        binding.rvTransaction.setHasFixedSize(true)
        mainViewModel.customer.observe(viewLifecycleOwner) {
            if (it != null) {
                sharedPreferenceHelper.setDebitCardNumber(it.debitCardNumber)
                mainViewModel.setDebitCardNumber(it.debitCardNumber)
                mainViewModel.loadTransactions(it.debitCardNumber)
            }
        }
        mainViewModel.getCustomerDetails(sharedPreferenceHelper.getAccount().toString())
    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.profile)
        item?.isVisible = true
    }
}

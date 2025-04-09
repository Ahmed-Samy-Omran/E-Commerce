package com.example.ui.account.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.e_commerce.R




class AccountFragment : Fragment() {

    var dataInitialized = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }


    override fun onResume() {
        // that make me control the fragment is visible or not to avoid the fragment to be initialized more than one time
        super.onResume()
        if (isVisible&& !dataInitialized){
            dataInitialized=true
            Log.d("onViewCreated","AccountFragment")
//            initViews()
//            initViewModel()
        }
    }

//    private fun initViewModel() {
//        TODO("Not yet implemented")
//    }
//
//    private fun initViews() {
//        TODO("Not yet implemented")
//    }
}
package com.example.ui.login.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.data.repository.user.UserDataStoreRepositoryImpl

import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentLoginBinding
import com.example.ui.login.viewmodel.LoginViewModel


class LoginFragment : Fragment() {

    val  loginViewModel: LoginViewModel by lazy {
        LoginViewModel(userPrefs =UserDataStoreRepositoryImpl(requireActivity()))
    }

    lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }



    companion object {
        private const val TAG = "LoginFragment"
    }
}
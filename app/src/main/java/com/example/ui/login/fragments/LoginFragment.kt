package com.example.ui.login.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.data.repository.user.UserPreferenceRepositoryImpl

import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentLoginBinding
import com.example.ui.login.viewmodel.AuthViewModel
import com.example.ui.login.viewmodel.LoginViewModel


class LoginFragment : Fragment() {

    val  loginViewModel: LoginViewModel by lazy {
        LoginViewModel(userPrefs =UserPreferenceRepositoryImpl(requireActivity()))
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
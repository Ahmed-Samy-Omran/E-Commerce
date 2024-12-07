package com.example.ui.login.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.data.datasource.datastore.UserPreferencesDataSource
import com.example.data.repository.user.UserDataStoreRepositoryImpl

import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentLoginBinding
import com.example.ui.login.viewmodel.LoginViewModel


class LoginFragment : Fragment() {



    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModel.LoginViewModelFactory(
            userPrefs = UserDataStoreRepositoryImpl(
                UserPreferencesDataSource(
                    requireActivity()
                )
            )
        )
    }

    private var _binding: FragmentLoginBinding?=null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding=FragmentLoginBinding.inflate(inflater,container,false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // to avoid memory leak
    }



    companion object {
        private const val TAG = "LoginFragment"
    }
}
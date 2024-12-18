package com.example.ui.auth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.data.datasource.datastore.UserPreferencesDataSource
import com.example.data.repository.auth.FirebaseAuthRepositoryImpl
import com.example.data.repository.user.UserDataStoreRepositoryImpl

import com.example.e_commerce.databinding.FragmentLoginBinding
import com.example.ui.auth.viewmodel.LoginViewModel


class LoginFragment : Fragment() {



    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModel.LoginViewModelFactory(
            userPrefs = UserDataStoreRepositoryImpl(
                UserPreferencesDataSource(
                    requireActivity()
                )
            ),
            authRepository = FirebaseAuthRepositoryImpl()
        )
    }

    private var _binding: FragmentLoginBinding?=null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding=FragmentLoginBinding.inflate(inflater,container,false)
        binding.lifecycleOwner=viewLifecycleOwner
        binding.viewmodel=loginViewModel

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        binding.loginBtn.setOnClickListener {
            loginViewModel.login()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // to avoid memory leak
    }



    companion object {
        private const val TAG = "LoginFragment"
    }
}
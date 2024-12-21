package com.example.ui.auth.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.data.datasource.datastore.UserPreferencesDataSource
import com.example.data.models.Resource
import com.example.data.repository.auth.FirebaseAuthRepositoryImpl
import com.example.data.repository.user.UserDataStoreRepositoryImpl

import com.example.e_commerce.databinding.FragmentLoginBinding
import com.example.ui.auth.viewmodel.LoginViewModel
import com.example.ui.common.views.ProgressDialog
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {

    private val progressDialog by lazy {
        ProgressDialog.createProgressDialog(requireActivity())
    }

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
        initViewModel()
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            loginViewModel.loginState.collect {state->
                Log.d(TAG,"initViewModel:$state")
                state.let {resource->
                    when(resource){
                       is Resource.Loading -> {
                            progressDialog.show()
                        }
                        is Resource.Success -> {
                            progressDialog.dismiss()
                            Toast.makeText(requireContext(),resource.data,Toast.LENGTH_SHORT).show()
                        }
                        is Resource.Error -> {
                            progressDialog.dismiss()
                            Toast.makeText(requireContext(),resource.exception?.message,Toast.LENGTH_SHORT).show()
                        }

                    }
                }

            }
        }
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
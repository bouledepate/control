package com.amir.ctrl.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.amir.ctrl.MainActivity
import com.amir.ctrl.R
import com.amir.ctrl.databinding.FragmentLoginBinding
import com.bouledepate.caffy.core.storage.MainStorage
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.RecursiveTask

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val mBinding get() = _binding!!
    private val parentActivity get() = requireActivity() as MainActivity
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        mBinding.loginFormSubmitButton.setOnClickListener { handleFormSubmit() }
    }

    private fun handleFormSubmit() {
        if (validateForm()) {
            mAuth.signInWithEmailAndPassword(
                mBinding.loginFormEmail.text.toString(),
                mBinding.loginFormPassword.text.toString()
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    MainStorage.email(
                        parentActivity.applicationContext,
                        mBinding.loginFormEmail.text.toString()
                    )
                    MainStorage.password(
                        parentActivity.applicationContext,
                        mBinding.loginFormPassword.text.toString()
                    )
                    parentActivity.login()
                } else {
                    parentActivity.showToast(
                        resources.getString(R.string.l_login_failed),
                        Toast.LENGTH_SHORT
                    )
                }
            }
        } else {
            parentActivity.showToast(
                resources.getString(R.string.l_validation_not_empty),
                Toast.LENGTH_SHORT
            )
        }
    }

    private fun validateForm(): Boolean {
        return mBinding.loginFormEmail.text.isNotBlank() && mBinding.loginFormPassword.text.isNotBlank()
    }
}
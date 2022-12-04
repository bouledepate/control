package com.amir.ctrl.fragments.supervisor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.amir.ctrl.MainActivity
import com.amir.ctrl.R
import com.amir.ctrl.data.User
import com.amir.ctrl.databinding.FragmentSupervisorMainBinding
import com.google.firebase.auth.FirebaseAuth

class SupervisorMainFragment : Fragment() {

    private var _binding: FragmentSupervisorMainBinding? = null
    private val mBinding get() = _binding!!
    private val currentUser get() = FirebaseAuth.getInstance().currentUser
    private val parentActivity get() = requireActivity() as MainActivity

    private var email: String? = null
    private var uuid: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSupervisorMainBinding.inflate(inflater, container, false)
        email = arguments?.getString("email")
        uuid = arguments?.getString("uuid")
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.ordersButton.setOnClickListener {
            findNavController().navigate(R.id.action_supervisorMainFragment_to_reportsFragment2)
        }

        mBinding.registerUsersButton.setOnClickListener {
            findNavController().navigate(R.id.action_supervisorMainFragment_to_registerUserFragment2)
        }

        mBinding.logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.action_supervisorMainFragment_to_loginFragment2)
        }

        getUserInformation()
    }

    private fun setUserInformation(user: User?) {
        if (user != null) {
            mBinding.infoUsername.text = "${user.lastName} ${user.firstName} ${user.secondName}"
            mBinding.infoEmail.text = user.email
            mBinding.infoIin.text = user.iin
            mBinding.infoAddress.text = user.address
            mBinding.infoBirthday.text = user.birthday
            mBinding.infoPhone.text = user.phone
        } else {
            mBinding.userInfoCard.visibility = View.GONE
            mBinding.infoEmail.text = currentUser?.email
        }

    }

    private fun getUserInformation() {
        var userData: User? = null
        parentActivity.database.reference.child("users").child(uuid!!).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    toggleLoadingPlaceholder()
                }
                setUserInformation(it.getValue(User::class.java))
            }
    }

    private fun toggleLoadingPlaceholder() {
        mBinding.userInfoLoading.visibility = View.GONE
        mBinding.userInfoCardBody.visibility = View.VISIBLE
    }
}
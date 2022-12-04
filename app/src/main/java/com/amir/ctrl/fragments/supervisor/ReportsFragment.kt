package com.amir.ctrl.fragments.supervisor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.navigateUp
import androidx.recyclerview.widget.LinearLayoutManager
import com.amir.ctrl.MainActivity
import com.amir.ctrl.data.Report
import com.amir.ctrl.data.User
import com.amir.ctrl.databinding.FragmentReportsBinding
import com.amir.ctrl.fragments.supervisor.adapters.MembersAdapter

class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val mBinding get() = _binding!!

    private val parentActivity get() = requireActivity() as MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.backToMainScreen.setOnClickListener {
            findNavController().navigateUp()
        }

        mBinding.reports.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        mBinding.reports.adapter = MembersAdapter(listOf(), findNavController())
        getUsers()
    }

    private fun getUsers() {
        val database = parentActivity.database.reference
        val users = arrayListOf<User>()
        database.child("users").get().addOnSuccessListener {
            it.children.forEach { userData ->
                var user = userData.getValue(User::class.java)
                if (user != null && !user.supervisor) {
                    users.add(user)
                }
            }
        }.addOnCompleteListener {
            users.forEach { user ->
                user.uuid?.let { uuid ->
                    database.child("reports").child(uuid).get().addOnSuccessListener { reports ->
                        user.reports = reports.childrenCount.toInt()
                    }.addOnCompleteListener {
                        (mBinding.reports.adapter as MembersAdapter).insertDataList(users)
                        disableLoaderBar()
                    }
                }
            }
        }
    }

    private fun disableLoaderBar() {
        mBinding.loadingBar.visibility = View.GONE
        mBinding.reports.visibility = View.VISIBLE
    }
}
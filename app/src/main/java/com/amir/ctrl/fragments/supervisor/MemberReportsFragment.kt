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
import com.amir.ctrl.R
import com.amir.ctrl.data.Report
import com.amir.ctrl.data.User
import com.amir.ctrl.databinding.FragmentMemberReportsBinding
import com.amir.ctrl.databinding.FragmentReportDetailBinding
import com.amir.ctrl.fragments.supervisor.adapters.MembersAdapter
import com.amir.ctrl.fragments.supervisor.adapters.ReportsAdapter
import com.google.firebase.database.core.Repo

class MemberReportsFragment : Fragment() {

    private var _binding: FragmentMemberReportsBinding? = null
    private val mBinding get() = _binding!!
    private val parentActivity get() = requireActivity() as MainActivity
    private var uuid: String? = null
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMemberReportsBinding.inflate(inflater, container, false)
        uuid = arguments?.getString("uuid")
        user = arguments?.getParcelable("user")!!
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.reports.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        mBinding.reports.adapter = ReportsAdapter(user, arrayListOf(), findNavController())
        mBinding.backToMainScreen.setOnClickListener {
            findNavController().navigateUp()
        }
        loadReports()
        insertMemberInformation()
    }

    private fun insertMemberInformation() {
        mBinding.infoUsername.text = "${user.lastName} ${user.firstName} ${user.secondName}"
        mBinding.infoEmail.text = user.email
        mBinding.infoIin.text = user.iin
        mBinding.infoAddress.text = user.address
        mBinding.infoBirthday.text = user.birthday
        mBinding.infoPhone.text = user.phone
    }

    private fun loadReports() {
        val database = parentActivity.database.reference
        val reports = arrayListOf<Report>()
        uuid?.let { database.child("reports").child(it).get().addOnSuccessListener {
            it.children.forEach {
                val report = it.getValue(Report::class.java)
                if (report != null) {
                    reports.add(report)
                }
            }
        }.addOnCompleteListener {
            (mBinding.reports.adapter as ReportsAdapter).insertDataList(reports)
            disableProgressBar()
        }}
    }

    private fun disableProgressBar() {
        mBinding.loadingBar.visibility = View.GONE
        mBinding.reports.visibility = View.VISIBLE
    }
}
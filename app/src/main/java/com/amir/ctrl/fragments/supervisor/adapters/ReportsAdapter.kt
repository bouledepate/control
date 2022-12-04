package com.amir.ctrl.fragments.supervisor.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.amir.ctrl.R
import com.amir.ctrl.data.Report
import com.amir.ctrl.data.User

class ReportsAdapter(
    val user: User,
    var reports: MutableList<Report>,
    val navigator: NavController
) : RecyclerView.Adapter<ReportsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View, val navigator: NavController) :
        RecyclerView.ViewHolder(itemView) {
        val datetime: TextView = itemView.findViewById(R.id.datetime)
        val card: CardView = itemView.findViewById(R.id.member_report_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.member_report_element, parent, false)
        return ViewHolder(itemView, navigator)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.datetime.text = "Сформирован: ${reports[position].date}"
        holder.card.setOnClickListener {
            val bundle = bundleOf("report" to reports[position])
            holder.navigator.navigate(
                R.id.action_memberReportsFragment_to_reportDetailFragment,
                bundle
            )
        }
    }

    override fun getItemCount(): Int {
        return reports.size
    }

    fun insertDataList(list: MutableList<Report>) {
        this.reports = list
        notifyDataSetChanged()
    }
}
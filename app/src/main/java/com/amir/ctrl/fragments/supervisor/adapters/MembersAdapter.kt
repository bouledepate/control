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
import com.amir.ctrl.data.User

class MembersAdapter(
    private var list: List<User>,
    private val navigator: NavController
) : RecyclerView.Adapter<MembersAdapter.ViewHolder>() {

    class ViewHolder(itemView: View, val navigator: NavController) : RecyclerView.ViewHolder(itemView) {
        var username: TextView = itemView.findViewById(R.id.username)
        var counter: TextView = itemView.findViewById(R.id.counter)
        var card: CardView = itemView.findViewById(R.id.report_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.report_element, parent, false)
        return ViewHolder(itemView, navigator)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.username.text =
            "${list[position].lastName} ${list[position].firstName} ${list[position].secondName}"
        holder.counter.text = "Кол-во отчётов: ${list[position].reports}"
        holder.card.setOnClickListener {
            val bundle = bundleOf("uuid" to list[position].uuid, "user" to list[position])
            holder.navigator.navigate(R.id.action_reportsFragment_to_memberReportsFragment, bundle)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun insertDataList(list: List<User>) {
        this.list = list
        notifyDataSetChanged()
    }
}
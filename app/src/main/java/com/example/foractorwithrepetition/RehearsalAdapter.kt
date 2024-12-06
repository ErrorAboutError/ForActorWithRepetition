package com.example.foractorwithrepetition

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
// Адаптер для отображения репетиций
class RehearsalAdapter(private var rehearsals: MutableList<Rehearsal>) : RecyclerView.Adapter<RehearsalAdapter.RehearsalViewHolder>() {
    inner class RehearsalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.rehearsalName)
        val time: TextView = itemView.findViewById(R.id.rehearsalTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RehearsalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rehearsal, parent, false)
        return RehearsalViewHolder(view)
    }

    override fun onBindViewHolder(holder: RehearsalViewHolder, position: Int) {
        holder.name.text = rehearsals[position].name
        holder.time.text = rehearsals[position].time
    }

    override fun getItemCount(): Int {
        return rehearsals.size
    }

    fun updateRehearsals(newRehearsals: List<Rehearsal>) {
        rehearsals.clear()
        rehearsals.addAll(newRehearsals)
        notifyDataSetChanged()
    }
}
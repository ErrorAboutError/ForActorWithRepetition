package com.example.foractorwithrepetition

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.yandex.runtime.Runtime.getApplicationContext


// Адаптер для отображения репетиций
class RehearsalAdapter(private var rehearsals: MutableList<Rehearsal>) : RecyclerView.Adapter<RehearsalAdapter.RehearsalViewHolder>() {
    inner class RehearsalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.titleText)
        val time: TextView = itemView.findViewById(R.id.timeText)
        val date: TextView = itemView.findViewById(R.id.dateText)
        val switcher: Switch = itemView.findViewById(R.id.toggleSwitch)
    }

    lateinit var rehearsalViewModel: RehearsalViewModel
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RehearsalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rehearshal_item, parent, false)
        return RehearsalViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onBindViewHolder(holder: RehearsalViewHolder, position: Int) {
        holder.title.text = rehearsals[position].name
        holder.time.text = rehearsals[position].time
        if(rehearsals[position].activated)
            holder.switcher.setChecked(true)
        holder.date.text = rehearsals[position].date
        holder.switcher.setOnClickListener{
            val alManager = holder.itemView.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if(!holder.switcher.isChecked) {

                val myIntent = Intent(
                    getApplicationContext(),
                    AlarmReceiver::class.java
                )
                val pendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(), rehearsals[position].id.toInt(), myIntent, PendingIntent.FLAG_IMMUTABLE
                )

                alManager.cancel(pendingIntent)
                rehearsals[position].activated = false
            }
            else {
                rehearsals[position].activated = true
                val alarmIntent = Intent(holder.itemView.context, AlarmReceiver::class.java).apply {
                    putExtra("rehearsal_name", rehearsals[position].name)
                }.let {
                    PendingIntent.getBroadcast(holder.itemView.context, rehearsals[position].id.toInt(), it, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE )
                }
                alManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    rehearsals[position].timeInMiles,
                    alarmIntent
                )
            }
            rehearsalViewModel.updateActivation(position.toLong(), rehearsals[position].activated )
        }
    }

//    private fun turnOnOthers(holder: RehearsalAdapter.RehearsalViewHolder) {
//        Log.i("name", "Activeted")
//        val alManager = holder.itemView.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        for (position in 0..this.getItemCount()){
//            val alarmIntent = Intent(holder.itemView.context, AlarmReceiver::class.java).apply {
//                putExtra("rehearsal_name", rehearsals[position].name)
//            }.let {
//                PendingIntent.getBroadcast(holder.itemView.context, System.currentTimeMillis().toInt(), it, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE )
//            }
//            alManager.setExact(AlarmManager.RTC_WAKEUP, rehearsals[position].timeInMiles, alarmIntent)
//        }
//
//    }


    override fun getItemCount(): Int {
        return rehearsals.size
    }

    fun updateRehearsals(newRehearsals: List<Rehearsal>) {
        rehearsals.clear()
        rehearsals.addAll(newRehearsals)
        notifyDataSetChanged()
    }
}
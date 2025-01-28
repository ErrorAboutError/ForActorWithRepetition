package com.example.foractorwithrepetition

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.foractorwithrepetition.ui.gallery.GalleryFragment
import com.yandex.runtime.Runtime.getApplicationContext


// Адаптер для отображения репетиций
class RehearsalAdapter(private var rehearsals: MutableList<Rehearsal>) : RecyclerView.Adapter<RehearsalAdapter.RehearsalViewHolder>() {


    var counter = 0
    inner class RehearsalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.titleText)
        val time: TextView = itemView.findViewById(R.id.timeText)
        val date: TextView = itemView.findViewById(R.id.dateText)
        val switcher: Switch = itemView.findViewById(R.id.toggleSwitch)
        val place: TextView = itemView.findViewById(R.id.placeText)
    }

    lateinit var rehearsalViewModel: RehearsalViewModel
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RehearsalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rehearshal_item, parent, false)
        return RehearsalViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onBindViewHolder(holder: RehearsalViewHolder, position: Int) {
        // Заолнение название оповещение
        holder.title.text = rehearsals[position].name
        // Заполнение времени оповещения
        holder.time.text = rehearsals[position].time
        // Включение активных оповещений
        holder.switcher.setChecked(rehearsals[position].activated)
        // Заполнение местоположения репетиции
        holder.place.text = rehearsals[position].placeName
        // Заполнение даты оповещения
        holder.date.text = rehearsals[position].date
        holder.itemView.setOnClickListener {
            GalleryFragment.goToChangeRehearsal(rehearsals[position])
        }
        // Обработка нажатия на переключатель
        holder.switcher.setOnClickListener{
            val alManager = holder.itemView.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if(!holder.switcher.isChecked) {
                // Отмена включённого уведомления
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
                // Создание уведомления
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
            // Сохранение изменеий в БД
            rehearsalViewModel.updateActivation(position.toLong(), rehearsals[position].activated )
        }
        // Анимация появления
        if(counter != 5) {
            holder.itemView.alpha = 0f
            Log.i("Animation", position.toString())
            Log.i("Animation2", counter.toString())
            counter = counter + 1
            holder.itemView.animate()
                .alpha(1f)
                .setDuration(1000)
                .setStartDelay((30 * position).toLong())
                .start()
        }
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
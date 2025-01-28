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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.foractorwithrepetition.ui.gallery.GalleryFragment
import com.yandex.runtime.Runtime.getApplicationContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


// Адаптер для отображения событий
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
        // Получение текущих даты и времени
        val formatterDate = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val currentDate = LocalDateTime.now().format(formatterDate)
        val formatterTime = DateTimeFormatter.ofPattern("HH:mm")
        val currentTime = LocalDateTime.now().format(formatterTime)

        // Заолнение название события
        holder.title.text = rehearsals[position].name
        // Заполнение времени события
        holder.time.text = rehearsals[position].time
        if( (rehearsals[position].date < currentDate ) || (rehearsals[position].date == currentDate
                    && rehearsals[position].time <= currentTime)) {
            rehearsalViewModel.updateActivation(position.toLong(), rehearsals[position].activated)
            holder.switcher.setChecked(false)
        } else
        // Включение активных событий
        holder.switcher.setChecked(rehearsals[position].activated)
        // Заполнение местоположения события
        holder.place.text = rehearsals[position].placeName
        // Заполнение даты события
        holder.date.text = rehearsals[position].date
        holder.itemView.setOnClickListener {
            GalleryFragment.goToChangeRehearsal(rehearsals[position])
        }
        // Обработка нажатия на переключатель
        holder.switcher.setOnClickListener{
            if( (rehearsals[position].date < currentDate ) || (rehearsals[position].date == currentDate
                        && rehearsals[position].time <= currentTime)){
                Log.i("No", "Not allowed")
                Toast.makeText(getApplicationContext(), "Нельзя включать оповещение на уже прошедшее событие", Toast.LENGTH_LONG).show()
                holder.switcher.setChecked(false)
            } else {
                val alManager =
                    holder.itemView.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (!holder.switcher.isChecked) {
                    // Отмена включённого оповещения
                    val myIntent = Intent(
                        getApplicationContext(),
                        AlarmReceiver::class.java
                    )
                    val pendingIntent = PendingIntent.getBroadcast(
                        getApplicationContext(),
                        rehearsals[position].id.toInt(),
                        myIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                    alManager.cancel(pendingIntent)
                    rehearsals[position].activated = false
                } else {
                    // Создание оповещения
                    rehearsals[position].activated = true
                    val alarmIntent =
                        Intent(holder.itemView.context, AlarmReceiver::class.java).apply {
                            putExtra("rehearsal_name", rehearsals[position].name)
                        }.let {
                            PendingIntent.getBroadcast(
                                holder.itemView.context,
                                rehearsals[position].id.toInt(),
                                it,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )
                        }
                    alManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        rehearsals[position].timeInMiles,
                        alarmIntent
                    )
                }
                // Сохранение изменеий в БД
                rehearsalViewModel.updateActivation(
                    position.toLong(),
                    rehearsals[position].activated
                )
            }
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

    // Получение размера списка
    override fun getItemCount() = rehearsals.size

    // Обновление списка событий
    fun updateRehearsals(newRehearsals: List<Rehearsal>) {
        rehearsals.clear()
        rehearsals.addAll(newRehearsals)
        notifyDataSetChanged()
    }
}
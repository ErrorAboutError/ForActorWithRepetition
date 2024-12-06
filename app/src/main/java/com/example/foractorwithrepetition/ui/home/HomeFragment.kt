package com.example.foractorwithrepetition.ui.home

import android.Manifest
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.foractorwithrepetition.AlarmReceiver
import com.example.foractorwithrepetition.R
import com.example.foractorwithrepetition.Rehearsal
import com.example.foractorwithrepetition.RehearsalAdapter
import com.example.foractorwithrepetition.RehearsalViewModel
import com.example.foractorwithrepetition.databinding.FragmentHomeBinding
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.runtime.image.ImageProvider
import java.util.Calendar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val CHANNEL_ID = "channelid"
    private val SCHEDULE_EXACT_ALARM_PERMISSION_REQUEST_CODE = 1
    private lateinit var alarmManager: AlarmManager
    private lateinit var rehearsalViewModel: RehearsalViewModel
    private lateinit var rehearsalAdapter: RehearsalAdapter
    private val REQUEST_LOCATION_PERMISSION = 1

    // This property is only valid between onCreateView and
    // onDestroyView.

    private val binding get() = _binding!!
    //val binding = FragmentHomeBinding.inflate(inflater, container, false)

    private val placemarkTapListener = MapObjectTapListener { _, point ->
        Toast.makeText(
            requireContext(),
            "Tapped the point (${point.longitude}, ${point.latitude})",
            Toast.LENGTH_SHORT
        ).show()
        true
    }

        //@RequiresApi(Build.VERSION_CODES.O)
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)
        // Инициализация binding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        val imageProvider = ImageProvider.fromResource(requireContext(), R.drawable.geometka)
        val placemark = binding.mapview.map.mapObjects.addPlacemark().apply {
            val geometry = Point(55.751225, 37.62954)
            setIcon(imageProvider)
        }
        placemark.addTapListener(placemarkTapListener)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        createNotificationChannel() //Создание канала отправки уведомления
        val root: View = binding.root
        binding.timePicker.setIs24HourView(true)

        binding.rehearsalDate.setOnClickListener { showDatePickerDialog() }

        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        //
        rehearsalViewModel = ViewModelProvider(this).get(RehearsalViewModel::class.java)

        binding.addButton.setOnClickListener { onAddButtonClicked() }

        rehearsalViewModel.getAllRehearsals().observe(viewLifecycleOwner) { rehearsals ->
            rehearsalAdapter = RehearsalAdapter(rehearsals.toMutableList())
            rehearsalAdapter.updateRehearsals(rehearsals)
        }
//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    // Для карт
    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapview.onStart()
    }

    override fun onStop() {
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun onAddButtonClicked() {
        val name = binding.rehearsalName.text.toString()
        val date = binding.rehearsalDate.text.toString()

        if (name.isNotEmpty() && date.isNotEmpty()) {
            //checkAndRequestPermissions(name)
            setAlarm(name)
        }else{
            Toast.makeText(requireContext(), "Вы не заполнили все поля", Toast.LENGTH_LONG).show()
        }
    }

//    @RequiresApi(Build.VERSION_CODES.S)
//    private fun checkAndRequestPermissions(name: String) {
//        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM), SCHEDULE_EXACT_ALARM_PERMISSION_REQUEST_CODE)
//        } else {
//            if (canScheduleExactAlarms()) {
//                setAlarm(name)
//            } else {
//                Toast.makeText(requireContext(), "Точные будильники не могут быть запланированы.", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == SCHEDULE_EXACT_ALARM_PERMISSION_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                val name = binding.rehearsalName.text.toString()
//                if (name.isNotEmpty() && canScheduleExactAlarms()) {
//                    setAlarm(name)
//                } else {
//                    Toast.makeText(requireContext(), "Точные будильники не могут быть запланированы.", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                Toast.makeText(requireContext(), "Разрешение для установки будильника отклонено.", Toast.LENGTH_SHORT).show()
//            }
//        }else{
//            Toast.makeText(requireContext(), "ГАСИТСЯ ПОЧЕМУ.", Toast.LENGTH_SHORT).show()
//        }
//    }
//    // Запрос на разрешение местоположения
//    private fun checkRequestLocationPermission(){
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
//            != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(requireActivity(),
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                REQUEST_LOCATION_PERMISSION);
//        }else{
//            // Разрешение уже предоставлено, можно запросить обновления местоположения
//            //requestLocationUpdates()
//        }
//    }
//    // Обработка результата запроса разрешений
//    fun onRequestPermissionsLocationResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_LOCATION_PERMISSION) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Разрешение получено, можно запросить обновления местоположения
//                //requestLocationUpdates()
//            } else {
//                // Разрешение отклонено, обработайте это
//                Toast.makeText(this, "Разрешение на доступ к местоположению отклонено.", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    private fun setAlarm(name: String) {
        showDialog{result->
            if (result) {
                alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val calendar = Calendar.getInstance().apply {
                    val dateParts = binding.rehearsalDate.text.toString().split("/")
                    if (dateParts.size == 3) {
                        set(Calendar.YEAR, dateParts[2].toInt())
                        set(Calendar.MONTH, dateParts[1].toInt() - 1)
                        set(Calendar.DAY_OF_MONTH, dateParts[0].toInt())
                        set(Calendar.HOUR_OF_DAY, binding.timePicker.hour)
                        set(Calendar.MINUTE, binding.timePicker.minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                        if (timeInMillis < System.currentTimeMillis()) {
                            add(Calendar.DAY_OF_YEAR, 1)
                        }
                    }
                }

                val alarmIntent = Intent(requireContext(), AlarmReceiver::class.java).apply {
                    putExtra("rehearsal_name", name)
                }.let {
                    PendingIntent.getBroadcast(requireContext(), System.currentTimeMillis().toInt(), it, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE )
                }
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmIntent)
                rehearsalViewModel.insert(Rehearsal(name = name, time = "${binding.timePicker.hour}:${binding.timePicker.minute}"))
                binding.rehearsalName.text.clear()
                binding.rehearsalDate.text.clear()
                binding.coordinate.text.clear()
            }
        }
    }

//    private fun canScheduleExactAlarms(): Boolean {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
//            alarmManager.canScheduleExactAlarms()
//        } else true
//    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(CHANNEL_ID, "Rehearsal Notifications", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "Channel for rehearsal notifications"
        }
        // Получаем NotificationManager через Context.NOTIFICATION_SERVICE
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        DatePickerDialog( requireContext(),
            { _: DatePicker?, year: Int, month: Int, day: Int ->
                binding.rehearsalDate.setText("$day/${month + 1}/$year")
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Dialog окно
    private fun showDialog(callback: (Boolean) -> Unit) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Добавить элемент")
        builder.setMessage("Вы хотите добавить новый элемент?")

        builder.setPositiveButton("Да") { dialog, which ->
            callback(true) // Пользователь нажал "Да"
        }

        builder.setNegativeButton("Нет") { dialog, which ->
            callback(false) // Пользователь нажал "Нет"
            dialog.dismiss() // Закрыть диалог
        }

        // Создаем и показываем диалог
        val dialog = builder.create()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
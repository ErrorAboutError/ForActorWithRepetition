package com.example.foractorwithrepetition.ui.home


import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.foractorwithrepetition.AlarmReceiver
import com.example.foractorwithrepetition.Rehearsal
import com.example.foractorwithrepetition.RehearsalAdapter
import com.example.foractorwithrepetition.RehearsalViewModel
import com.example.foractorwithrepetition.databinding.FragmentHomeBinding
import com.yandex.mapkit.*
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.GeoObjectSelectionMetadata
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapWindow
import com.yandex.mapkit.search.Address
import java.util.Calendar
import java.util.Locale


class HomeFragment : Fragment() {


    private val CHANNEL_ID = "channelid"
    private val SCHEDULE_EXACT_ALARM_PERMISSION_REQUEST_CODE = 1
    private lateinit var alarmManager: AlarmManager
    private lateinit var rehearsalViewModel: RehearsalViewModel
    private lateinit var rehearsalAdapter: RehearsalAdapter
    private val REQUEST_LOCATION_PERMISSION = 1
    var selectedCoordinate = ""
    private lateinit var mapWindow: MapWindow

    var code = 0
    private val SMOOTH_ANIMATION = Animation(Animation.Type.SMOOTH, 0.4f)
    private lateinit var binding: FragmentHomeBinding

    companion object{
       var changingRehearsal: Rehearsal? = null
    }

    fun fillData(rehearsal: Rehearsal){
        binding.timePicker.hour = rehearsal.time.split(":")[0].toInt()
        binding.timePicker.minute = rehearsal.time.split(":")[1].toInt()
        binding.coordinate.text.append(rehearsal.placeName)
        binding.rehearsalDate.text.append(rehearsal.date)
        binding.rehearsalName.text.append(rehearsal.name)
        binding.addButton.text = "Изменить"
        Log.i("fill", "Data")
        selectedCoordinate = rehearsal.location
        binding.addButton.setOnClickListener {
            val calendar = Calendar.getInstance().apply {
                // Создание точного времени оповещения
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
            Log.i("click", "click")
            rehearsalViewModel.updateRehearsal(Rehearsal(id = changingRehearsal!!.id,name = binding.rehearsalName.text.toString(), time = "${binding.timePicker.hour}:${binding.timePicker.minute}",
                date = "${binding.rehearsalDate.text}", timeInMiles = calendar.timeInMillis, activated = changingRehearsal!!.activated,
                location = selectedCoordinate, placeName = binding.coordinate.text.toString()))
            requireActivity().onBackPressed()
        }
    }
    // Обработка нажатия на объект на карте
    private val geoObjectTapListener = GeoObjectTapListener {
        val point = it.geoObject.geometry.firstOrNull()?.point ?: return@GeoObjectTapListener true
        binding.mapview.map.cameraPosition.run {
            val position = CameraPosition(point, zoom, azimuth, tilt)
            binding.mapview.map.move(position, SMOOTH_ANIMATION, null)
        }
        val selectionMetadata =
            it.geoObject.metadataContainer.getItem(GeoObjectSelectionMetadata::class.java)
        binding.mapview.map.selectGeoObject(selectionMetadata)
        val geocoder: Geocoder
        val addresses: MutableList<android.location.Address>?
        geocoder = Geocoder(requireActivity(), Locale.getDefault())

        addresses = geocoder.getFromLocation(
            point.latitude,
            point.longitude,
            1
        )

        val address: String =
            addresses!![0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        val city: String = addresses[0].locality
        val street: String = addresses[0].getAddressLine(0).split(",")[0]
        val country: String = addresses[0].countryName
        val knownName: String = addresses[0].featureName
        Log.i("placement", "$country $city $street $knownName")
        selectedCoordinate = "${binding.mapview.map.cameraPosition.target.latitude.toString()}, ${binding.mapview.map.cameraPosition.target.longitude.toString()}"
        Log.i("placement", binding.mapview.map.cameraPosition.target.latitude.toString())
        Log.i("placement", binding.mapview.map.cameraPosition.target.longitude.toString())
        binding.coordinate.text.clear()
        binding.coordinate.append("$country $city $street $knownName")
        true
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Инициализация binding
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        createNotificationChannel() //Создание канала отправки уведомления
        val root: View = binding.root
        binding.timePicker.setIs24HourView(true)
        binding.rehearsalDate.setOnClickListener { showDatePickerDialog() }
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        rehearsalViewModel = ViewModelProvider(this).get(RehearsalViewModel::class.java)
        binding.addButton.setOnClickListener { onAddButtonClicked() }
        rehearsalViewModel.getAllRehearsals().observe(viewLifecycleOwner) { rehearsals ->
            rehearsalAdapter = RehearsalAdapter(rehearsals.toMutableList())
            rehearsalAdapter.updateRehearsals(rehearsals)
            // Получение последнего id в БД
            code = rehearsals.size + 1
        }

        mapWindow = binding.mapview.mapWindow
        binding.mapview.map.addTapListener(geoObjectTapListener)
        if(changingRehearsal != null)
            fillData(changingRehearsal!!)
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
        val coordinate = binding.coordinate.text.toString()

        if (name.isNotEmpty() && date.isNotEmpty() && coordinate.isNotEmpty()) {
            setAlarm(name, coordinate)
            addEventToGoogleCalendar(name, date)
        }else{
            Toast.makeText(requireContext(), "Вы не заполнили все поля", Toast.LENGTH_LONG).show()
        }
    }

    private fun setAlarm(name: String, coordinate: String) {
        showDialog{result->
            if (result) {
                alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val calendar = Calendar.getInstance().apply {
                    // Создание точного времени оповещения
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
                // Создание оповещения
                val alarmIntent = Intent(requireContext(), AlarmReceiver::class.java).apply {
                    putExtra("rehearsal_name", name)
                    putExtra("coordinate", selectedCoordinate)
                    Log.i("coordinate3", selectedCoordinate)
                }.let {
                    PendingIntent.getBroadcast(requireContext(), code++, it, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE )
                }

                // Добавление оповещения
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmIntent)
                rehearsalViewModel.insert(Rehearsal(name = name, time = "${binding.timePicker.hour}:${binding.timePicker.minute}",
                    date = "${binding.rehearsalDate.text}", timeInMiles = calendar.timeInMillis, activated = true,
                    location = selectedCoordinate, placeName = coordinate))
                // Очистка полей
                binding.rehearsalName.text.clear()
                binding.rehearsalDate.text.clear()
                binding.coordinate.text.clear()
            }
        }
    }


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

    private fun addEventToGoogleCalendar(name: String, date: String) {
        val dateParts = date.split("/")
        if (dateParts.size != 3) {
            Toast.makeText(requireContext(), "Неверный формат даты", Toast.LENGTH_SHORT).show()
            return
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, dateParts[2].toInt())
            set(Calendar.MONTH, dateParts[1].toInt() - 1)
            set(Calendar.DAY_OF_MONTH, dateParts[0].toInt())
            set(Calendar.HOUR_OF_DAY, binding.timePicker.hour)
            set(Calendar.MINUTE, binding.timePicker.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = android.provider.CalendarContract.Events.CONTENT_URI
            putExtra(android.provider.CalendarContract.Events.TITLE, name)
            putExtra(android.provider.CalendarContract.Events.DESCRIPTION, "Репетиция")
            putExtra(android.provider.CalendarContract.Events.EVENT_LOCATION, binding.coordinate.text.toString())
            putExtra(android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.timeInMillis)
            putExtra(android.provider.CalendarContract.EXTRA_EVENT_END_TIME, calendar.timeInMillis + 60 * 60 * 1000) // 1 час
        }

        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "Приложение Календарь не установлено", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        changingRehearsal = null
    }
}
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
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.foractorwithrepetition.AlarmReceiver
import com.example.foractorwithrepetition.R
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
import com.yandex.runtime.Runtime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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

    // Заполнение данных
    fun fillData(rehearsal: Rehearsal){
        // Заполнение полей
        binding.timePicker.hour = rehearsal.time.split(":")[0].toInt()
        binding.timePicker.minute = rehearsal.time.split(":")[1].toInt()
        binding.coordinate.text.append(rehearsal.placeName)
        binding.rehearsalDate.text.append(rehearsal.date)
        binding.rehearsalName.text.append(rehearsal.name)
        binding.addButton.text = "Изменить"
        selectedCoordinate = rehearsal.location
        // Изменение обработчика нажатия на кнопку
        binding.addButton.setOnClickListener {
            changeRehearsal()

        }
        // Вывод на экран кнопки Удалить
        binding.deleteButton.visibility = View.VISIBLE
        // Переопределение нажатия на кнопку Добавить
        binding.deleteButton.setOnClickListener {
            deleteButtonClick(rehearsal)
        }
    }

    // Обработка нажатия на кнопку удалить
    fun deleteButtonClick(rehearsal: Rehearsal){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Удаление")
        builder.setMessage("Вы уверены, что хотите удалить оповещение?")
        builder.setPositiveButton("Да") { dialog, which ->
            rehearsalViewModel.delete(rehearsal)
            requireActivity().onBackPressed()// Пользователь нажал "Да"
        }
        builder.setNegativeButton("Нет") { dialog, which ->
            // Пользователь нажал "Нет"
            dialog.dismiss() // Закрыть диалог
        }
        // Создаем и показываем диалог
        val dialog = builder.create()
        dialog.show()
    }

    fun changeRehearsal(){
        // Проверка на ввод данных
        if (binding.rehearsalName.text.isEmpty() || binding.rehearsalDate.text.isEmpty() || binding.coordinate.text.isEmpty())
            return
        // Введённое время
        val time = "${binding.timePicker.hour}" +
                if(binding.timePicker.minute <= 9)
                    ":0${binding.timePicker.minute}"
                else
                    ":${binding.timePicker.minute}"
        // Получение текущих даты и времени
        val formatterDate = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val currentDate = LocalDateTime.now().format(formatterDate)
        val formatterTime = DateTimeFormatter.ofPattern("HH:mm")
        val currentTime = LocalDateTime.now().format(formatterTime)
        Log.i("date", currentDate)
        Log.i("date2", binding.rehearsalDate.text.toString())
        Log.i("time", currentTime)
        Log.i("time2", time)
        Log.i("time2", (time <= currentTime).toString())
        // Проверка, что указана дата и время, находящиеся в будущем
        if( (binding.rehearsalDate.text.toString() < currentDate ) || (binding.rehearsalDate.text.toString() == currentDate
                    && time <= currentTime)){
            Toast.makeText(requireContext(), "Необходимо ввести предстоящее событие", Toast.LENGTH_LONG).show()
            return
        }
        val calendar = Calendar.getInstance().apply {
            // Создание точного времени события
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
        // Изменение события
        rehearsalViewModel.updateRehearsal(Rehearsal(id = changingRehearsal!!.id,name = binding.rehearsalName.text.toString(), time = time,
            date = "${binding.rehearsalDate.text}", timeInMiles = calendar.timeInMillis, activated = changingRehearsal!!.activated,
            location = selectedCoordinate, placeName = binding.coordinate.text.toString()))
        // Отмена включённого оповещения
        val myIntent = Intent(
            Runtime.getApplicationContext(),
            AlarmReceiver::class.java
        )
        val pendingIntent = PendingIntent.getBroadcast(
            Runtime.getApplicationContext(), (changingRehearsal!!.id).toInt(), myIntent, PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        // Добавление изменённого
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("rehearsal_name", binding.rehearsalName.text.toString())
            putExtra("coordinate", selectedCoordinate)
        }.let {
            PendingIntent.getBroadcast(context, (changingRehearsal!!.id).toInt(), it, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE )
        }
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            alarmIntent
        )
        // Переход ко списку событий
        requireActivity().onBackPressed()
    }

    // Обработка нажатия на объект на карте
    private val geoObjectTapListener = GeoObjectTapListener {
        val point = it.geoObject.geometry.firstOrNull()?.point ?: return@GeoObjectTapListener true
        // Переход камеры на объект на карте
        binding.mapview.map.cameraPosition.run {
            val position = CameraPosition(point, zoom, azimuth, tilt)
            binding.mapview.map.move(position, SMOOTH_ANIMATION, null)
        }
        // Получение данных об объекте на карте
        val selectionMetadata =
            it.geoObject.metadataContainer.getItem(GeoObjectSelectionMetadata::class.java)
        binding.mapview.map.selectGeoObject(selectionMetadata)
        val geocoder: Geocoder
        val addresses: MutableList<android.location.Address>?
        geocoder = Geocoder(requireActivity(), Locale.getDefault())
        // Получение адреса объекта на карте
        addresses = geocoder.getFromLocation(
            point.latitude,
            point.longitude,
            1
        )
        addresses!![0].getAddressLine(0)
        val city: String = addresses[0].locality
        val street: String = addresses[0].getAddressLine(0).split(",")[0]
        val country: String = addresses[0].countryName
        val knownName: String = addresses[0].featureName
        Log.i("placement", "$country $city $street $knownName")
        // Сохранение координат объекта
        selectedCoordinate = "${binding.mapview.map.cameraPosition.target.latitude}, ${binding.mapview.map.cameraPosition.target.longitude}"
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
        // Очистка полей
        Log.i("Clear", binding.rehearsalName.text.toString())
        binding.rehearsalName.text.clear()
        binding.rehearsalDate.text.clear()
        binding.coordinate.text.clear()
        binding.deleteButton.visibility = View.GONE
        binding.addButton.text = "Добавить"
        createNotificationChannel() //Создание канала отправки оповещений
        val root: View = binding.root
        binding.timePicker.setIs24HourView(true)
        binding.rehearsalDate.setOnClickListener { showDatePickerDialog() }
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        rehearsalViewModel = ViewModelProvider(this).get(RehearsalViewModel::class.java)
        binding.addButton.setOnClickListener { onAddButtonClicked() }
        // Загрузка данных из БД
        rehearsalViewModel.getAllRehearsals().observe(viewLifecycleOwner) { rehearsals ->
            rehearsalAdapter = RehearsalAdapter(rehearsals.toMutableList())
            rehearsalAdapter.updateRehearsals(rehearsals)
            // Получение последнего id в БД
            code = rehearsals.size + 1
        }
        mapWindow = binding.mapview.mapWindow
        binding.mapview.map.addTapListener(geoObjectTapListener)
        return root
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun onAddButtonClicked() {
        val name = binding.rehearsalName.text.toString()
        val date = binding.rehearsalDate.text.toString()
        val coordinate = binding.coordinate.text.toString()
        // Получение текущего даты и времени
        val formatterDate = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val currentDate = LocalDateTime.now().format(formatterDate)
        val formatterTime = DateTimeFormatter.ofPattern("HH:mm")
        val currentTime = LocalDateTime.now().format(formatterTime)
        // Получение введённого времени
        val time = "${binding.timePicker.hour}" +
                if(binding.timePicker.minute <= 9)
                    ":0${binding.timePicker.minute}"
                else
                    ":${binding.timePicker.minute}"
        // Проверка на заполнение полей
        if (name.isNotEmpty() && date.isNotEmpty() && coordinate.isNotEmpty()) {
            if( (binding.rehearsalDate.text.toString() < currentDate ) || (binding.rehearsalDate.text.toString() == currentDate
                        && time <= currentTime)){
                Toast.makeText(requireContext(), "Необходимо ввести предстоящее событие", Toast.LENGTH_LONG).show()
                return
            }
            // Добавление оповещения
            setAlarm(name, coordinate, date)
        }else{
            Toast.makeText(requireContext(), "Вы не заполнили все поля", Toast.LENGTH_LONG).show()
        }
    }

    private fun setAlarm(name: String, coordinate: String, date: String) {
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
                // Время с добавлением 0 в случае однозначного количества минут
                val time = "${binding.timePicker.hour}" +
                if(binding.timePicker.minute <= 9)
                    ":0${binding.timePicker.minute}"
                else
                    ":${binding.timePicker.minute}"
                rehearsalViewModel.insert(Rehearsal(name = name, time = time,
                    date = "${binding.rehearsalDate.text}", timeInMiles = calendar.timeInMillis, activated = true,
                    location = selectedCoordinate, placeName = coordinate))
                addEventToGoogleCalendar(name, date)
                // Очистка полей
                binding.rehearsalName.text.clear()
                binding.rehearsalDate.text.clear()
                binding.coordinate.text.clear()
            }
        }
    }


    // Создание канала для оповещений
    private fun createNotificationChannel() {
        val channel = NotificationChannel(CHANNEL_ID, "Rehearsal Notifications", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "Channel for rehearsal notifications"
        }
        // Получаем NotificationManager через Context.NOTIFICATION_SERVICE
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    // Диалог выбора даты
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        DatePickerDialog( requireContext(),
            { _: DatePicker?, year: Int, month: Int, day: Int ->
                // Изменение введённой даты под определённый формат
                val dateText =
                    (if(day <= 9)
                        "0$day/"
                    else
                        "$day/") +
                            ( if(month < 9)
                    "0${month + 1}/"
                else
                    "${month + 1}/") + year.toString()
                binding.rehearsalDate.setText(dateText)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Dialog окно
    private fun showDialog(callback: (Boolean) -> Unit) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Добавить оповещение")
        builder.setMessage("Вы хотите добавить новое оповещение?")
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

    // Метод открытия google календаря
    private fun addEventToGoogleCalendar(name: String, date: String) {
        val dateParts = date.split("/")
        if (dateParts.size != 3) {
            Toast.makeText(requireContext(), "Неверный формат даты", Toast.LENGTH_SHORT).show()
            return
        }
        // Создание времени оповещения
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, dateParts[2].toInt())
            set(Calendar.MONTH, dateParts[1].toInt() - 1)
            set(Calendar.DAY_OF_MONTH, dateParts[0].toInt())
            set(Calendar.HOUR_OF_DAY, binding.timePicker.hour)
            set(Calendar.MINUTE, binding.timePicker.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        // Создание intent google календаря
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = android.provider.CalendarContract.Events.CONTENT_URI
            putExtra(android.provider.CalendarContract.Events.TITLE, name)
            putExtra(android.provider.CalendarContract.Events.DESCRIPTION, "Репетиция")
            putExtra(android.provider.CalendarContract.Events.EVENT_LOCATION, binding.coordinate.text.toString())
            putExtra(android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.timeInMillis)
            putExtra(android.provider.CalendarContract.EXTRA_EVENT_END_TIME, calendar.timeInMillis + 60 * 60 * 1000) // 1 час
        }
        // Открытие google календаря
        try {
            startActivity(intent)
        } catch (exception: Exception){
            Toast.makeText(requireContext(), "Не удалось открыть календарь", Toast.LENGTH_LONG).show()
        }
    }

    // Для карт
    override fun onStart() {
        super.onStart()
        if(changingRehearsal != null)
            // Заполнение данных при наличии
            fillData(changingRehearsal!!)
        else {
            // Очистка полей
            Log.i("Clear", binding.rehearsalName.text.toString())
            binding.rehearsalName.text.clear()
            binding.rehearsalDate.text.clear()
            binding.coordinate.text.clear()
            binding.deleteButton.visibility = View.GONE
            binding.addButton.text = "Добавить"
        }
        MapKitFactory.getInstance().onStart()
        binding.mapview.onStart()
    }

    override fun onStop() {
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()
        // Очистка сохранённых данных
        changingRehearsal = null
        super.onStop()
    }
}


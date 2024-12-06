package com.example.foractorwithrepetition.ui.slideshow

import android.app.Activity.RESULT_OK
//import android.app.SearchManager
import android.app.blob.BlobStoreManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.foractorwithrepetition.AdapterDataTheatreClass
import com.example.foractorwithrepetition.DataThearteClass
import com.example.foractorwithrepetition.R
import com.example.foractorwithrepetition.databinding.FragmentSlideshowBinding
//import com.google.android.gms.common.api.Response
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.VisibleRegion
import com.yandex.mapkit.map.VisibleRegionUtils
import com.yandex.mapkit.search.BusinessObjectMetadata
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType


import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.search.Response
import com.yandex.runtime.Error
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError


class SlideshowFragment : Fragment(), AdapterDataTheatreClass.OnItemClickListener {

    private var _binding: FragmentSlideshowBinding? = null
    var adapter: AdapterDataTheatreClass? = null
    var androidData: DataThearteClass? = null
    var searchView: SearchView? = null
    private val dataList: ArrayList<DataThearteClass> = arrayListOf()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private lateinit var searchManager: SearchManager
    private var searchSession: BlobStoreManager.Session? = null

    private val binding get() = _binding!!
    companion object {
        private const val SPEECH_REQUEST_CODE = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.searchEditText.setQuery("", false) // устанавливаем нуевой запрос
        //binding.searchEditText.onActionViewCollapsed() // действие запроса сворачивается

        MapKitFactory.initialize(requireContext()) // Инициализируем MapKit
       // SearchFactory.initialize(requireContext())
        searchManager = SearchFactory.getInstance().createSearchManager(com.yandex.mapkit.search.SearchManagerType.COMBINED)

        // Очищаем dataList
        dataList.clear()

        // Обновляем адаптер
        adapter?.notifyDataSetChanged()

        // Настройка адаптера для RecyclerView
        binding.recyclerView.adapter = adapter

        binding.searchEditText.setOnTouchListener { _, event ->
//            if (event.action == MotionEvent.ACTION_UP) {
//                // Проверяем, нажали ли на drawableEnd (иконка голосового ввода)
//                if (event.rawX >= (binding.searchEditText.right.toFloat() - binding.searchEditText.compoundDrawablesRelative[2].bounds.width())) {
//                    startVoiceInput()
//                    return@setOnTouchListener true
//                }
//            }
//            false
            if (event.action == MotionEvent.ACTION_UP) {
                // Получаем доступ к вью, отвечающей за голосовой ввод
                val searchPlate = binding.searchEditText.findViewById<View>(androidx.appcompat.R.id.search_plate)
                val voiceIcon = searchPlate?.findViewById<ImageView>(androidx.appcompat.R.id.search_voice_btn)

                // Проверяем, если нажали на голосовой ввод
                if (voiceIcon != null && event.rawX >= (voiceIcon.right - voiceIcon.width)) {
                    startVoiceInput()
                    return@setOnTouchListener true
                }
            }
            false
        }
        var slideModels= ArrayList<SlideModel>()
        slideModels.add(SlideModel(R.drawable.mapkitadd, title="Яндекс MapKit",  ScaleTypes.FIT))
        slideModels.add(SlideModel(R.drawable.theareslider,  title="Театр", ScaleTypes.FIT))
        slideModels.add(SlideModel(R.drawable.den_kino, title="Кино", ScaleTypes.FIT))
        slideModels.add(SlideModel(R.drawable.spbadd, title="Отдыхай в Санкт-Петербурге",  ScaleTypes.FIT))
        slideModels.add(SlideModel(R.drawable.spbaddtheatre, title="Выбери любимый театр",  ScaleTypes.FIT))
        slideModels.add(SlideModel(R.drawable.yandexadd, title="Гуляй вместе с Яндекс",  ScaleTypes.FIT))
        slideModels.add(SlideModel(R.drawable.shedevrumadd, title="Твори с Шедеврум",  ScaleTypes.FIT))
        slideModels.add(SlideModel(R.drawable.yandexmapkit, title="API Mapkit - должно помочь",  ScaleTypes.FIT))
        binding.imageSlider.setImageList(slideModels, ScaleTypes.FIT)

        binding.searchEditText.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchTheaters(query)  // Запускаем поиск театров в городе
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Обработка изменения текста поиска
                return false
            }
        })

      //  setupRecyclerView()
        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        binding.recyclerView.layoutManager = gridLayoutManager
       dataList.apply{
           add(DataThearteClass("Театр Таро", "улица Гороховая, 51", "Открыто до 01:00", R.drawable.threatrelist))
           add(DataThearteClass("Кинотеатр Лицедей", "улица Гороховая, 51", "Открыто до 23:00", R.drawable.streethaus))
           add(DataThearteClass("Отдыхающая уточка", "улица Гороховая, 51", "Открыто до 22:00", R.drawable.theareslider))
           add(DataThearteClass("Клоун-мим театр \"Фантазия кончилась\"", "улица Гороховая, 51", "Открыто до 02:00", R.drawable.theatreyandex))
           //add(DataThearteClass("Rating Bar", R.string.rating, "Java", R.drawable.date_detail))
       }
        adapter = AdapterDataTheatreClass(requireContext(), dataList, this)
        binding.recyclerView.adapter = adapter



        binding.searchEditText.clearFocus()
        binding.searchEditText.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchList(newText)
                return true
            }
        })

        return root
    }
//    private fun setupRecyclerView() {
//        dataList.apply {
//            add(DataThearteClass("Camera", R.string.camera, "Java", R.drawable.date_detail))
//            add(DataThearteClass("RecyclerView", R.string.recyclerview, "Kotlin", R.drawable.date_detail))
//            add(DataThearteClass("Date Picker", R.string.date, "Java", R.drawable.date_detail))
//            add(DataThearteClass("EditText", R.string.edit, "Kotlin", R.drawable.date_detail))
//            add(DataThearteClass("Rating Bar", R.string.rating, "Java", R.drawable.date_detail))
//        }
//
//        adapter = AdapterDataTheatreClass(requireContext(), dataList, this)
//        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
//        binding.recyclerView.adapter = adapter
//    }


//
    override fun onItemClick(item: DataThearteClass) {

        val bundle = Bundle().apply {
            putInt("Image", item.getDataImage())
            putString("Title", item.getDataTitle().toString())
            putString("Desc", item.getDataDesc().toString())
        }
        findNavController().navigate(R.id.action_yourCurrentFragment_to_fragmentDetail, bundle)
    }

    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Алё, не молчите, говорите, вас не слышно")
        }

        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "Извините, голосовой ввод не поддерживается на вашем устройстве.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (result != null && result.isNotEmpty()) {
                val recognizedText = result[0]

                // Устанавливаем распознанный текст в поле ввода
                binding.searchEditText.setQuery(recognizedText, false)
            }
        }
    }


    private fun searchList(text: String) {
        val dataSearchList = dataList.filter {
            searchTheaters(text)
            it.getDataTitle().contains(text, ignoreCase = true)
        }
        if (dataSearchList.isEmpty()) {
            Toast.makeText(requireContext(), "Не найдено среди сохранённых", Toast.LENGTH_SHORT).show()
        } else {
            adapter?.setSearchList(dataSearchList)
        }
    }


    // Метод для поиска театров в городе:
    private fun searchTheaters(city: String) {
        // Используем MapKit API для запроса по названию города
        val visibleRegion = binding.mapView.map.visibleRegion  // Получаем видимую область карты
        val searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        val searchOptions = SearchOptions().apply { searchTypes = SearchType.BIZ.value }
        val searchArea = Geometry.fromBoundingBox(BoundingBox(visibleRegion.bottomLeft, visibleRegion.topRight))

        searchManager.submit(
            "$city theaters", // Запрос поиска
            searchArea,
            searchOptions,   // Опции поиска
            object : Session.SearchListener {
                override fun onSearchResponse(response: Response) {
                    val theaters = response.collection.children.mapNotNull { item ->
                        item.obj?.let {
                            DataThearteClass(
                                it.name.toString(),
                               // it.descriptionText.toString(),
                                //it.metadataContainer.toString(),
                                it.descriptionText.toString(),
                                it.descriptionText.toString(),
                                //it.geometry.first().point.toString(),
                                R.drawable.spbadd  // Статичное изображение
                            )
                        }
                    }
                    updateRecyclerView(theaters)
                }

                override fun onSearchError(error: Error) {
                    val errorMessage = when (error) {
                        is NetworkError -> "Ошибка сети. Проверьте подключение к интернету."
                        is RemoteError -> "Ошибка сервера."
                        else -> "Неизвестная ошибка."
                    }
                    Toast.makeText(context, "Ошибка поиска: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun updateRecyclerView(theaters: List<DataThearteClass>) {
        dataList.clear()
        dataList.addAll(theaters)
        adapter?.notifyDataSetChanged()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
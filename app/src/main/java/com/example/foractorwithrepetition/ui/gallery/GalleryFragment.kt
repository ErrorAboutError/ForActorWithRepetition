package com.example.foractorwithrepetition.ui.gallery


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foractorwithrepetition.R
import com.example.foractorwithrepetition.Rehearsal
import com.example.foractorwithrepetition.RehearsalAdapter
import com.example.foractorwithrepetition.RehearsalViewModel
import com.example.foractorwithrepetition.databinding.FragmentGalleryBinding
import com.example.foractorwithrepetition.ui.home.HomeFragment

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private lateinit var rehearsalViewModel: RehearsalViewModel


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)
        rehearsalViewModel = ViewModelProvider(this).get(RehearsalViewModel::class.java)
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        _binding!!.addPehearsalButton.setOnClickListener {
            addRehearsalButtonClick()
        }
        navControler = findNavController()
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun addRehearsalButtonClick(){
        navControler.navigate(R.id.nav_home)
    }

    override fun onResume() {
        super.onResume()
        // Загрузка Rehearsal
        rehearsalViewModel.getAllRehearsals().observe(viewLifecycleOwner) { rehearsals ->
            // Создание адаптера
            val rehearsalAdapter = RehearsalAdapter(rehearsals.toMutableList())
            rehearsalAdapter.updateRehearsals(rehearsals)
            _binding!!.rehearsalList.adapter = rehearsalAdapter
            // Передача доступа к БД
            rehearsalAdapter.rehearsalViewModel = this.rehearsalViewModel
            // Настройка внешнего вида списка
            _binding!!.rehearsalList.layoutManager = LinearLayoutManager(this.context)
        }
    }

    companion object{
        lateinit var navControler: NavController
        fun goToChangeRehearsal(rehearsal: Rehearsal){
            navControler.navigate(R.id.nav_home)
            HomeFragment.changingRehearsal = rehearsal
        }
    }
}
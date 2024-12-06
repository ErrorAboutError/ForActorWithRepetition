package com.example.foractorwithrepetition.ui.detail

import android.content.Intent.getIntent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.foractorwithrepetition.R
import com.example.foractorwithrepetition.databinding.FragmentFragmentDetailBinding


class FragmentDetail : Fragment() {
    private var _binding: FragmentFragmentDetailBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = FragmentDetail()
    }

    private lateinit var viewModel: FragmentDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFragmentDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val args = arguments
        if (args != null) {
            binding.detailDesc.text = args.getString("Desc")?: "Описание отсутствует"
            binding.detailImage.setImageResource(args.getInt("Image"))
            binding.detailTitle.text = args.getString("Title")?: "Отсутствует"
        }

        return root
        //return inflater.inflate(R.layout.fragment_fragment_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FragmentDetailViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Чтобы избежать утечек памяти
    }

}
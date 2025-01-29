package com.example.foractorwithrepetition

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.foractorwithrepetition.databinding.FragmentAddressBinding
import com.example.foractorwithrepetition.databinding.FragmentLinkBinding

class AddressFragment : Fragment() {
    private var _binding: FragmentAddressBinding? = null
    private val binding get() = _binding!!

    private var data: String? = null

    fun setData(data: String) {
        this.data = data
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddressBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.detailDesc.text = data
        binding.addressCard.alpha=0f
        binding.addressCard.animate().alpha(1f).translationYBy((-30).toFloat()).setStartDelay(300).duration=1500
        return root
    }
}
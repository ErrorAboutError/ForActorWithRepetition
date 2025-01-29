package com.example.foractorwithrepetition.ui.qrcode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.foractorwithrepetition.R
import com.example.foractorwithrepetition.databinding.FragmentQrBinding

class qrCodeFragment: Fragment() {
    private var _binding: FragmentQrBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val qrcodeViewModel =
//            ViewModelProvider(this).get(qrCodeViewModel::class.java)

        _binding = FragmentQrBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // Переход к полю создания QR кода
        binding.createQR.setOnClickListener() {
            findNavController().navigate(R.id.action_qrCodeFragment_to_createQRFragment)
        }
        // Переход к полю сканирования QR кода
        binding.scanQR.setOnClickListener() {
            findNavController().navigate(R.id.action_qrCodeFragment_to_ScanQRFragment)
        }

        binding.createQR.alpha =0f
        binding.scanQR.alpha=0f

        binding.createQR.animate().alpha(1f).translationYBy((30).toFloat()).setStartDelay(300).duration=1500
        binding.scanQR.animate().alpha(1f).translationYBy((-30).toFloat()).setStartDelay(300).duration=1500



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.foractorwithrepetition

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class ScanQRFragment : Fragment() {

    companion object {
        fun newInstance() = ScanQRFragment()
    }

    private lateinit var viewModel: ScanQRViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scan_q_r, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ScanQRViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
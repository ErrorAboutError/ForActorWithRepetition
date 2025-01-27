package com.example.foractorwithrepetition.ui.scanQR

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.foractorwithrepetition.R
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView



class ScanQRFragment : Fragment() {

    companion object {
        fun newInstance() = ScanQRFragment()
    }

    private lateinit var viewModel: ScanQRViewModel
    private lateinit var barcodeView: DecoratedBarcodeView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Используем разметку с `DecoratedBarcodeView`
        val rootView = inflater.inflate(R.layout.fragment_scan_q_r, container, false)
        barcodeView = rootView.findViewById(R.id.barcode_scanner)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ScanQRViewModel::class.java)
        // TODO: Use the ViewModel
    }
    override fun onResume() {
        super.onResume()
        barcodeView.decodeContinuous(callback)
        barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    private val callback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult?) {
            result?.let {
                val scannedText = it.text
                Log.d("ScanQRFragment", "Result: ${it.text}")
                // Обработайте результат сканирования
                Log.d("ScanQRFragment", "Result: $scannedText")

                // Показываем результат в UI (можно заменить на TextView)
                Toast.makeText(requireContext(), "Scanned: $scannedText", Toast.LENGTH_SHORT).show()

                // Если это ссылка, можно открыть её
                if (scannedText.startsWith("http://") || scannedText.startsWith("https://")) {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(scannedText))
                    startActivity(browserIntent)
                }
            }
        }

        override fun possibleResultPoints(resultPoints: List<com.google.zxing.ResultPoint>) {
            // Можно обработать потенциальные точки результата (опционально)
        }
    }

}
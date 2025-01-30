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
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.foractorwithrepetition.R
import com.example.foractorwithrepetition.RehearsalViewModel
import com.example.foractorwithrepetition.ui.home.HomeFragment
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import kotlinx.coroutines.delay


class ScanQRFragment : Fragment() {

    companion object {
        fun newInstance() = ScanQRFragment()
        lateinit var navControler: NavController
    }

    private lateinit var viewModel: ScanQRViewModel
    private lateinit var barcodeView: DecoratedBarcodeView
    private lateinit var rehearsalViewModel: RehearsalViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Используем разметку с `DecoratedBarcodeView`
        val rootView = inflater.inflate(R.layout.fragment_scan_q_r, container, false)
        barcodeView = rootView.findViewById(R.id.barcode_scanner)
        rehearsalViewModel = ViewModelProvider(this).get(RehearsalViewModel::class.java)
        navControler = findNavController()
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
            // Приостановка сканирования
            barcodeView.pause()
            result?.let {
                val scannedText = it.text
                Log.d("ScanQRFragment", "Result: ${it.text}")
                // Обработайте результат сканирования
                Log.d("ScanQRFragment", "Result: $scannedText")

                // Показываем результат в UI (можно заменить на TextView)
                // TODO Меняй, но Toast точно не надо оставить - слишком часто вызывается метод
                //Toast.makeText(requireContext(), "Scanned: $scannedText", Toast.LENGTH_SHORT).show()
                // Если это ссылка, можно открыть её
                if (scannedText.startsWith("http://") || scannedText.startsWith("https://")) {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(scannedText))
                    startActivity(browserIntent)
                } else
                    rehearsalViewModel.checkShareID(it.text)

                // Возобновление сканирования
                barcodeView.resume()
            }
        }

        override fun possibleResultPoints(resultPoints: List<com.google.zxing.ResultPoint>) {
            // Можно обработать потенциальные точки результата (опционально)
        }
    }

}
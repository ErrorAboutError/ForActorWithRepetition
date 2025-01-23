package com.example.foractorwithrepetition.ui.createQR

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import com.example.foractorwithrepetition.R
import com.example.foractorwithrepetition.databinding.FragmentCreateQRBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.io.File
import java.io.FileOutputStream
import java.util.EnumMap

class CreateQRFragment : Fragment() {
    private var _binding: FragmentCreateQRBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = CreateQRFragment()
    }

    private lateinit var viewModel: CreateQRViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateQRBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.buttonGenerate.setOnClickListener() {

            if(binding.editText.text.toString().isNotEmpty()){
                createQRCode(binding.editText.text.toString().trim())
            }
        }
        return root
       // return inflater.inflate(R.layout.fragment_create_q_r, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CreateQRViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun createQRCode(text:String){
        val qrCodeWriter = QRCodeWriter()
        val hints: MutableMap<EncodeHintType, Any> = EnumMap(EncodeHintType::class.java)
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        hints[EncodeHintType.MARGIN]  = 1
        try{
            val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 300, 300, hints)
            val width = bitMatrix.width
            val height = bitMatrix.height
              val bitmap = Bitmap.createBitmap(width , height, Bitmap.Config.RGB_565)
            for(x in 0  until width){
                for(y in 0 until height){
                    bitmap.setPixel(x, y, if(bitMatrix.get(x, y)) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
                }
            }
            val logoBitmap = BitmapFactory.decodeResource(resources, R.drawable.logo3)
            val logoWidth = bitmap.width/4
            val logoHeight = bitmap.height/4
            val logoX = (bitMatrix.width - logoWidth) /2
            val logoY = (bitMatrix.height -logoHeight) /2
            val combinedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
            val canvas = Canvas(combinedBitmap)
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            canvas.drawBitmap(logoBitmap, Rect(0,0,logoBitmap.width, logoBitmap.height), Rect(logoX, logoY, logoX+logoWidth, logoY+logoHeight), null)
            binding.imageView2.setImageBitmap(combinedBitmap)
            binding.imageView2.visibility = View.VISIBLE

            // Добавление кнопки для отправки QR-кода
            binding.buttonShare.visibility = View.VISIBLE
            binding.buttonShare.setOnClickListener {
                shareQRCode(combinedBitmap)
            }
        }catch(e: Exception) {
            e.printStackTrace()
        }
    }
    private fun shareQRCode(bitmap: Bitmap) {
        try {
            // Сохранение Bitmap в файл
            val file = File(
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "shared_qr_code.png"
            )
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            // Получение URI через FileProvider
            val uri: Uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                file
            )

            // Создание Intent для обмена
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // Запуск Intent
            startActivity(Intent.createChooser(shareIntent, "Поделиться QR-кодом"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
package com.example.foractorwithrepetition.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
            val uri = args?.getString("Uri")?.split(",") ?:emptyList()
            // Логируем полученный URI
            if (uri.isNotEmpty()){
                binding.linkText.text=uri.joinToString("\n")
                binding.linkText.movementMethod = LinkMovementMethod.getInstance()
                binding.linkText.autoLinkMask= Linkify.WEB_URLS
            }else{
                binding.linkText.text="Данных нет!"
            }
            Log.d("FragmentDetail", "Received URI: $uri")
        }

//        binding.button.setOnClickListener {
//            if (!uri.isNullOrEmpty()) {
//                try {
//                    val validUri = if (!uri.startsWith("http://") && !uri.startsWith("https://")) {
//                        "https://$uri" // Добавляем протокол, если он отсутствует
//                    } else {
//                        uri
//                    }
//                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(validUri))
//                    startActivity(intent)
//                } catch (e: Exception) {
//                    Toast.makeText(context, "Не удалось открыть ссылку: ${e.message}", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                Toast.makeText(context, "Ссылка недоступна или некорректна", Toast.LENGTH_SHORT).show()
//            }
//        }
        return root
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
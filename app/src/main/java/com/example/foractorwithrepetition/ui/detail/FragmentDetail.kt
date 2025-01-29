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
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.foractorwithrepetition.AddressFragment
import com.example.foractorwithrepetition.LinkFragment
import com.example.foractorwithrepetition.TitleFragment
import com.example.foractorwithrepetition.databinding.FragmentFragmentDetailBinding
import com.google.android.material.tabs.TabLayoutMediator


class FragmentDetail : Fragment() {
    private var _binding: FragmentFragmentDetailBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = FragmentDetail()
    }

    private lateinit var viewModel: FragmentDetailViewModel
    class TabPagerAdapter(fragmentActivity: FragmentActivity,
                          private val fragments: List<Fragment>,
                          private val data: String,
                          private val dataAdress: String,
                          private val dataTitle: String) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment {
            val fragment = fragments[position]

            // Если это экземпляр AddressFragment, передайте данные
            if (fragment is AddressFragment) {
                fragment.setData(dataAdress)
            }else if (fragment is LinkFragment){
                fragment.setData(data)
            }else if (fragment is TitleFragment){
                fragment.setData(dataTitle)
            }

            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFragmentDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val args = arguments
        if (args != null) {
            //binding.detailDesc.text = args.getString("Desc")?: "Описание отсутствует"
            val description = args.getString("Desc")?: "Описание отсутствует"
           // binding.detailImage.setImageResource(args.getInt("Image"))
            //binding.detailTitle.text = args.getString("Title")?: "Отсутствует"
            val title = args.getString("Title")?: "Отсутствует"
           // val description = args.getString("Title")?: "Отсутствует"
            val uri = args?.getString("Uri")?.split(",") ?:emptyList()
            // Логируем полученный URI
//            if (uri.isNotEmpty()){
//                binding.linkText.text=uri.joinToString("\n")
//                binding.linkText.movementMethod = LinkMovementMethod.getInstance()
//                binding.linkText.autoLinkMask= Linkify.WEB_URLS
//            }else{
//                binding.linkText.text="Данных нет!"
//            }
//            Log.d("FragmentDetail", "Received URI: $uri")

            // Создаем список фрагментов
            val fragments = listOf(
                TitleFragment(),
                AddressFragment(),
                LinkFragment()
            )

            // Устанавливаем адаптер
            binding.viewPager.adapter = TabPagerAdapter(requireActivity(), fragments, uri.joinToString("\n"), description, title)

            // Связываем TabLayout с ViewPager2
            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> "Название"
                    1 -> "Адрес"
                    2 -> "Ссылки"
                    else -> ""
                }
            }.attach()
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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Чтобы избежать утечек памяти
    }

}
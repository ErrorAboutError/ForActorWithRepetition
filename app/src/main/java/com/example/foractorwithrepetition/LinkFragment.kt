package com.example.foractorwithrepetition

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.foractorwithrepetition.databinding.FragmentFragmentDetailBinding
import com.example.foractorwithrepetition.databinding.FragmentLinkBinding

class LinkFragment : Fragment() {
    private var _binding: FragmentLinkBinding? = null
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
        _binding = FragmentLinkBinding.inflate(inflater, container, false)
        binding.linkText.text=data
        binding.linkText.movementMethod = LinkMovementMethod.getInstance()
        binding.linkText.autoLinkMask= Linkify.WEB_URLS
        binding.linkCard.alpha=0f
        binding.linkCard.animate().alpha(1f).translationYBy((-30).toFloat()).setStartDelay(300).duration=1500
        val root: View = binding.root
        return root
    }
}
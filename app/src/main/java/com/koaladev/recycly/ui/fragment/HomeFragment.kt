package com.koaladev.recycly.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.koaladev.recycly.databinding.FragmentHomeBinding
import com.koaladev.recycly.ui.viewmodel.RecyclyViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecyclyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.points.observe(viewLifecycleOwner) { points ->
            binding.tvPointsValue.text = points.toString()
        }

//        binding.btnScanTrash.setOnClickListener() {
//            viewModel.addPoints(5)
//        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
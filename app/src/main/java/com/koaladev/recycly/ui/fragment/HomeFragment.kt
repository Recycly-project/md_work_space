package com.koaladev.recycly.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.koaladev.recycly.R
import com.koaladev.recycly.databinding.FragmentHomeBinding
import com.koaladev.recycly.ui.viewmodel.RecyclyViewModel
import com.koaladev.recycly.ui.viewmodel.ViewModelFactory

class HomeFragment : Fragment() {

    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding

    private val viewModel: RecyclyViewModel by viewModels{
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.points.observe(viewLifecycleOwner) { points ->
            binding.tvPointsValue.text = points.toString()
            Log.d("HomeFragment", "Points updated: $points")
        }

        binding.btnScanTrash.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_scanActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshPoints()
    }
}
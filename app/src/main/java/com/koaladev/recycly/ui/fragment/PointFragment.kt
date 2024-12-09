package com.koaladev.recycly.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.koaladev.recycly.databinding.FragmentPointBinding
import com.koaladev.recycly.ui.viewmodel.RecyclyViewModel
import com.koaladev.recycly.ui.viewmodel.ViewModelFactory

class PointFragment : Fragment() {

    private lateinit var _binding: FragmentPointBinding
    private val binding get() = _binding
    private val viewModel: RecyclyViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPointBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.points.observe(viewLifecycleOwner) { points ->
            binding.currentPoint.text = "Current point: $points"
            Log.d("PointFragment", "Points updated: $points")
        }

        setupExchange()
    }

    private fun setupExchange() {
        binding.btn1.setOnClickListener {
            val requiredPoints = 100
            val currentPoints = viewModel.points.value ?: 0

            if (currentPoints >= requiredPoints) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Exchange Success!")
                    .setMessage("You have successfully exchanged $requiredPoints points.")
                    .setPositiveButton("OK") { _, _ ->
                        viewModel.minsPoints(requiredPoints)
                    }
                    .create()
                    .show()
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle("Exchange Failed!")
                    .setMessage("Not enough points! You need $requiredPoints points, but you only have $currentPoints points.")
                    .setPositiveButton("OK", null)
                    .create()
                    .show()
            }
        }

        binding.btn2.setOnClickListener {
            val requiredPoints = 1000
            val currentPoints = viewModel.points.value ?: 0

            if (currentPoints >= requiredPoints) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Exchange Success!")
                    .setMessage("You have successfully exchanged $requiredPoints points.")
                    .setPositiveButton("OK") { _, _ ->
                        viewModel.minsPoints(requiredPoints)
                    }
                    .create()
                    .show()
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle("Exchange Failed!")
                    .setMessage("Not enough points! You need $requiredPoints points, but you only have $currentPoints points.")
                    .setPositiveButton("OK", null)
                    .create()
                    .show()
            }
        }

        binding.btn3.setOnClickListener {
            val requiredPoints = 2000
            val currentPoints = viewModel.points.value ?: 0

            if (currentPoints >= requiredPoints) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Exchange Success!")
                    .setMessage("You have successfully exchanged $requiredPoints points.")
                    .setPositiveButton("OK") { _, _ ->
                        viewModel.minsPoints(requiredPoints)
                    }
                    .create()
                    .show()
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle("Exchange Failed!")
                    .setMessage("Not enough points! You need $requiredPoints points, but you only have $currentPoints points.")
                    .setPositiveButton("OK", null)
                    .create()
                    .show()
            }
        }
    }
}
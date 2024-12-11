package com.koaladev.recycly.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        binding.btnScanTrash.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_scanActivity)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.isLogin) {
                viewModel.getUserById(user.id, user.token)
            } else {
                Toast.makeText(requireContext(), "Session expired", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.userData.observe(viewLifecycleOwner) { result ->
            val user = result.data.user
            binding.tvPointsValue.text = user.totalPoints.toString()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshPoints()
    }
}
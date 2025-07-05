package com.koaladev.recycly.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.koaladev.recycly.adapter.RewardAdapter
import com.koaladev.recycly.data.response.RewardItem
import com.koaladev.recycly.databinding.FragmentPointBinding
import com.koaladev.recycly.ui.viewmodel.PointViewModel
import com.koaladev.recycly.ui.viewmodel.RecyclyViewModel
import com.koaladev.recycly.ui.viewmodel.ViewModelFactory
import java.text.NumberFormat

class PointFragment : Fragment() {

    private var _binding: FragmentPointBinding? = null
    private val binding get() = _binding!!

    // ViewModel utama untuk data sesi dan poin
    private val mainViewModel: RecyclyViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    // ViewModel khusus untuk halaman ini
    private val pointViewModel: PointViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var rewardAdapter: RewardAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPointBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModels()
    }

    private fun setupRecyclerView() {
        rewardAdapter = RewardAdapter { reward ->
            // Aksi saat tombol "Tukar" di klik
            showRedeemConfirmationDialog(reward)
        }
        binding.rvRewards.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rewardAdapter
        }
    }

    private fun observeViewModels() {
        // Mengamati data sesi untuk mendapatkan token dan userId
        mainViewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.isLogin) {
                // Muat daftar reward saat pengguna login
                pointViewModel.getRewards(user.token)
            }
        }

        // Mengamati total poin dari ViewModel utama
        mainViewModel.points.observe(viewLifecycleOwner) { points ->
            val formattedPoints = NumberFormat.getIntegerInstance().format(points)
            binding.currentPoint.text = "$formattedPoints Poin"
        }

        // Mengamati daftar reward
        pointViewModel.rewards.observe(viewLifecycleOwner) { rewards ->
            rewardAdapter.submitList(rewards)
        }

        // Mengamati status loading
        pointViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Mengamati hasil penukaran
        pointViewModel.redeemResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { response ->
                Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                // Refresh data pengguna untuk memperbarui total poin
                mainViewModel.getSession().value?.let {
                    mainViewModel.getUserById(it.id, it.token)
                }
            }.onFailure {
                Toast.makeText(context, "Penukaran gagal: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // Mengamati error
        pointViewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
        }
    }

    private fun showRedeemConfirmationDialog(reward: RewardItem) {
        val currentPoints = mainViewModel.points.value ?: 0
        if (currentPoints < reward.redeemPoint) {
            Toast.makeText(context, "Poin Anda tidak cukup!", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Penukaran")
            .setMessage("Anda akan menukar ${reward.redeemPoint} poin dengan ${reward.title}. Lanjutkan?")
            .setPositiveButton("Tukar") { _, _ ->
                mainViewModel.getSession().value?.let { user ->
                    pointViewModel.redeemReward(user.id, reward.id, user.token)
                }
            }
            .setNegativeButton("Batal", null)
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
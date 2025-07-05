// app/src/main/java/com/koaladev/recycly/ui/fragment/HistoryFragment.kt

package com.koaladev.recycly.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.koaladev.recycly.adapter.QrHistoryAdapter // Impor adapter baru
import com.koaladev.recycly.databinding.FragmentHistoryBinding
import com.koaladev.recycly.ui.viewmodel.HistoryViewModel
import com.koaladev.recycly.ui.viewmodel.RecyclyViewModel
import com.koaladev.recycly.ui.viewmodel.ViewModelFactory

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecyclyViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private val historyViewModel: HistoryViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var adapter: QrHistoryAdapter // Ganti adapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvHistoryTitle.text = "Riwayat Scan QR" // Ganti judul
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = QrHistoryAdapter() // Gunakan adapter baru
        binding.rvHistoryCollection.layoutManager = LinearLayoutManager(context)
        binding.rvHistoryCollection.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.isLogin) {
                historyViewModel.getQrHistory(user.id, user.token)

                historyViewModel.qrHistory.observe(viewLifecycleOwner) { history ->
                    adapter.submitList(history)
                }

                historyViewModel.error.observe(viewLifecycleOwner) { error ->
                    // Tangani error, misal: tampilkan Toast
                }

                historyViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
                    binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
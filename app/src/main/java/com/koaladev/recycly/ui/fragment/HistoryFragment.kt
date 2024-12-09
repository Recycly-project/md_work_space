package com.koaladev.recycly.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.koaladev.recycly.data.repository.RecyclyRepository
import com.koaladev.recycly.data.repository.SessionPreferences
import com.koaladev.recycly.data.retrofit.ApiConfigAuth
import com.koaladev.recycly.databinding.FragmentHistoryBinding
import com.koaladev.recycly.ui.history.HistoryAdapter
import com.koaladev.recycly.ui.history.HistoryViewModel
import com.koaladev.recycly.ui.history.HistoryViewModelFactory
import kotlinx.coroutines.flow.first

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HistoryViewModel

//    private val viewModel: HistoryViewModel by viewModels {
//        val apiService = ApiConfigAuth.getApiService()
//        HistoryViewModelFactory(
//            RecyclyRepository.getInstance(apiService),
//            SessionPreferences.getInstance(requireContext())
//        )
//    }

    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = ApiConfigAuth.getApiService()
        val repository = RecyclyRepository.getInstance(apiService)
        val sessionPreferences = SessionPreferences.getInstance(requireContext())
        val factory = HistoryViewModelFactory(repository, sessionPreferences)
        viewModel = ViewModelProvider(this, factory)[HistoryViewModel::class.java]

        setupRecyclerView()
        observeViewModel()

        viewModel.getWasteCollections()
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter()
        binding.rvHistoryCollection.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@HistoryFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.wasteCollections.observe(viewLifecycleOwner) { collections ->
            adapter.submitList(collections)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            // Handle error, e.g., show a Toast or Snackbar
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
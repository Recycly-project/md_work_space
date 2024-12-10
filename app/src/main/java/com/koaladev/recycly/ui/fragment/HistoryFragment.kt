package com.koaladev.recycly.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.koaladev.recycly.adapter.HistoryAdapter
import com.koaladev.recycly.databinding.FragmentHistoryBinding
import com.koaladev.recycly.ui.viewmodel.HistoryViewModel
import com.koaladev.recycly.ui.viewmodel.RecyclyViewModel
import com.koaladev.recycly.ui.viewmodel.ViewModelFactory

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecyclyViewModel by viewModels{
        ViewModelFactory.getInstance(requireContext())
    }
    private val historyViewModel: HistoryViewModel by viewModels{
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter()
        binding.rvHistoryCollection.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@HistoryFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.isLogin) {
                historyViewModel.getWasteCollections(user.id, user.token)

                historyViewModel.wasteCollections.observe(viewLifecycleOwner) { collections ->
                    adapter.submitList(collections)
                }

                historyViewModel.error.observe(viewLifecycleOwner) { error ->
                    // Handle error, e.g., show a Toast or Snackbar
                }

                historyViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
                    binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                }

            } else {
                // Fetch waste collections for the user
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
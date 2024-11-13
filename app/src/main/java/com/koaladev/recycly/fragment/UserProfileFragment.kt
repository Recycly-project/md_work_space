package com.koaladev.recycly.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.koaladev.recycly.R
import com.koaladev.recycly.databinding.FragmentUserProfileBinding

class UserProfileFragment : Fragment() {

    private lateinit var _binding: FragmentUserProfileBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
}
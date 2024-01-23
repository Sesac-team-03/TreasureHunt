package com.treasurehunt.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.treasurehunt.databinding.FragmentProfileBinding

class ProfileFragment: Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEditButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setEditButton() {
        binding.ibEdit.setOnClickListener {
            binding.tvCancel.visibility = View.VISIBLE
            binding.tvCompleted.visibility = View.VISIBLE
            binding.ibEdit.visibility = View.GONE
        }
        binding.tvCancel.setOnClickListener {
            binding.tvCancel.visibility = View.GONE
            binding.tvCompleted.visibility = View.GONE
            binding.ibEdit.visibility = View.VISIBLE
        }
        binding.tvCompleted.setOnClickListener {
            binding.tvCancel.visibility = View.GONE
            binding.tvCompleted.visibility = View.GONE
            binding.ibEdit.visibility = View.VISIBLE
        }
    }
}
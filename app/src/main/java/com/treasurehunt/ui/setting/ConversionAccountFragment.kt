package com.treasurehunt.ui.setting

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.treasurehunt.databinding.FragmentConversionAccountBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConversionAccountFragment : DialogFragment() {
    private var _binding: FragmentConversionAccountBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentConversionAccountBinding.inflate(LayoutInflater.from(context))
        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .create()
            .apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window?.requestFeature(Window.FEATURE_NO_TITLE)
                setCanceledOnTouchOutside(false)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCloseButton()
        setCancelButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setCloseButton() {
        binding.ibClose.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setCancelButton() {
        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}
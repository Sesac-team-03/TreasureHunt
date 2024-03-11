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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.treasurehunt.R
import com.treasurehunt.databinding.FragmentDeleteUserBinding
import com.treasurehunt.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeleteUserFragment : DialogFragment() {
    private var _binding: FragmentDeleteUserBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingViewModel by viewModels()

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentDeleteUserBinding.inflate(LayoutInflater.from(context))
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
        setBackButton()
        setDeleteButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setBackButton() {
        setCloseButton()
        setCancelButton()
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

    private fun setDeleteButton() {
        binding.btnDelete.setOnClickListener {
            val user = Firebase.auth.currentUser ?: return@setOnClickListener kotlin.run {
                it.showSnackbar(R.string.delete_user_sb_delete_user_error)
            }

            viewLifecycleOwner.lifecycleScope.launch {
                async { viewModel.deleteAllData(user.uid) }.await()
                user.delete()
                findNavController().navigate(R.id.action_deleteUserFragment_to_logInFragment)
            }
        }
    }
}
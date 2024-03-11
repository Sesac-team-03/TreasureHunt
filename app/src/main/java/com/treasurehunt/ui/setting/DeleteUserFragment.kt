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
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_delete_user, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackButton()
        setDeleteUserInfo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setBackButton() {
        binding.ibClose.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setDeleteUserInfo() {
        val userId = Firebase.auth.currentUser?.uid
        val user = Firebase.auth.currentUser
        binding.btnDelete.setOnClickListener {
            if (userId != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    async { viewModel.deleteAllData(userId) }.await()
                    user?.delete()
                    findNavController().navigate(R.id.action_deleteUserFragment_to_logInFragment)
                }
            }
        }
    }
}
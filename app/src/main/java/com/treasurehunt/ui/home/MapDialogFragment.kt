package com.treasurehunt.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.treasurehunt.R
import com.treasurehunt.data.local.model.PlaceEntity
import com.treasurehunt.data.remote.model.PlaceDTO
import com.treasurehunt.databinding.FragmentMapDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentMapDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private val args: MapDialogFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setPrompt()
        setBtnVisit()
        setBtnPlan()
        setBtnCancel()
    }

    private fun setPrompt() {
        binding.tvTitle.text = getString(R.string.map_prompt, args.mapSymbol.caption)
    }

    private fun setBtnVisit() {
        binding.btnVisit.setOnClickListener {
            if (viewModel.uiState.value.uid.isNullOrEmpty()) {
                findNavController().navigateUp()
            }

            val action =
                MapDialogFragmentDirections.actionMapDialogFragmentToSaveLogFragment(
                    args.mapSymbol
                )
            findNavController().navigate(action)
        }
    }

    private fun setBtnPlan() {
        binding.btnPlan.setOnClickListener {
            if (viewModel.uiState.value.uid.isNullOrEmpty()) {
                findNavController().navigateUp()
            }

            val uid = viewModel.uiState.value.uid ?: return@setOnClickListener
            val (lat, lng, isPlan, caption) = args.mapSymbol
            val planEntity = PlaceEntity(
                lat,
                lng,
                true,
                caption
            )
            val planDTO = PlaceDTO(
                lat,
                lng,
                true,
                caption
            )

            viewLifecycleOwner.lifecycleScope.launch {
                val user = viewModel.getRemoteUser(uid)
                val localPlanId = viewModel.addPlace(planEntity)
                val remotePlanId = viewModel.addPlace(planDTO.copy(localId = localPlanId))
                viewModel.updatePlace(
                    planEntity.copy(localId = localPlanId, remoteId = remotePlanId)
                )
                viewModel.updatePlace(
                    remotePlanId,
                    planDTO.copy(localId = localPlanId)
                )
                viewModel.updateUser(uid, user.copy(remotePlanIds = user.remotePlanIds + (remotePlanId to true)))

                findNavController().navigateUp()
            }
        }
    }

    private fun setBtnCancel() {
        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
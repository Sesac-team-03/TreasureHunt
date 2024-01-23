package com.treasurehunt.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.treasurehunt.data.model.PlaceEntity
import com.treasurehunt.databinding.FragmentMapDialogBinding

class MapDialogFragment : DialogFragment() {

    private var _binding: FragmentMapDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels { HomeViewModel.Factory } // share?
    private val args: MapDialogFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setLayout(400, MATCH_PARENT)

        binding.btnPlace.setOnClickListener {
            // findNavController() -> save log fragment
        }

        binding.btnPlan.setOnClickListener {
            val (lat, lng, caption) = args.MapSymbol
            val plan = PlaceEntity(
                lat,
                lng,
                true,
                caption
            )
            viewModel.addPlan(plan)
            findNavController().navigateUp()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
package com.treasurehunt.ui.detail

import android.content.Intent
import android.content.res.Resources
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.treasurehunt.R
import com.treasurehunt.databinding.FragmentDetailBinding
import com.treasurehunt.ui.home.HomeViewModel
import com.treasurehunt.ui.model.LogModel
import kotlinx.coroutines.launch

class DetailFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageSliderAdapter: ImageSliderAdapter
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUIComponents()

        val logId = arguments?.getString("logId") ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.fetchLogData(logId)
        }

        viewModel.logData.observe(viewLifecycleOwner) { logModel ->
            logModel?.let {
                updateUI(it)
            } ?: run {
                Log.e("DetailFragment", "logModel is null")
            }
        }
    }


    private fun setupUIComponents() {
        setDotsIndicator()
        setShareButton()
        setCloseButton()
        setEditButton()
        setBottomSheet()
    }

    private fun updateUI(logModel: LogModel) {
        binding.textView.text = logModel.text
        setImageSlider(logModel.images)
    }

    private fun setImageSlider(imageUrls: List<String>) {
        imageSliderAdapter = ImageSliderAdapter(imageUrls)
        binding.viewPager.adapter = imageSliderAdapter
        imageSliderAdapter.submitList(imageUrls)
    }

    private fun setDotsIndicator() {
        binding.dotsIndicator.setViewPager2(binding.viewPager)
    }

    private fun setShareButton() {
        binding.btnShareContent.setOnClickListener {
            val contentToShare = collectDataForSharing()
            shareContent(contentToShare)
        }
    }

    private fun setCloseButton() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun setEditButton() {
        binding.btnEdit.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_saveLogFragment)
        }
    }

    private fun setBottomSheet() {
        dialog?.setOnShowListener {
            val bottomSheet =
                (it as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                val dpValue = 580
                val pixels = (dpValue * Resources.getSystem().displayMetrics.density).toInt()
                it.layoutParams.height = pixels
                behavior.peekHeight = pixels
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private fun collectDataForSharing(): String {
        val textViewContent = binding.textView.text.toString()
        val currentImageUrl = imageSliderAdapter.getImageItems()[imageSliderAdapter.currentPage]
        return "TextView 내용: $textViewContent\n이미지 URL: $currentImageUrl"
    }

    private fun shareContent(content: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, content)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(intent, "내용 공유하기"))
    }

    companion object {
        fun newInstance(userId: String): DetailFragment {
            return DetailFragment().apply {
                arguments = Bundle().apply {
                    putString("userId", userId)
                }
            }
        }
    }
}

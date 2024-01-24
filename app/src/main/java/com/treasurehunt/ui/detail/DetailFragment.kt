package com.treasurehunt.ui.detail

import android.content.Intent
import android.content.res.Resources
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.treasurehunt.R
import com.treasurehunt.databinding.FragmentDetailBinding

class DetailFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private var contentId: String? = null
    private lateinit var imageSliderAdapter: ImageSliderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        contentId = arguments?.getString("content_id")

        setImageSlider()

        return binding.root
    }

    private fun setImageSlider() {
        val imageUrls = listOf(
            "https://firebasestorage.googleapis.com/v0/b/treasurehunt-32565.appspot.com/o/uid%2Fimage%2F52.png?alt=media&token=4323ef4c-8380-46e5-91af-911e51011c41",
            "https://firebasestorage.googleapis.com/v0/b/treasurehunt-32565.appspot.com/o/uid%2Fimage%2F1000003321.png?alt=media&token=34cae484-ef2e-48a7-bd2f-54daa74a00c8",
            R.drawable.gajwa, R.drawable.gajwa, R.drawable.gajwa
        ) as List<Any>
        imageSliderAdapter = ImageSliderAdapter(imageUrls)
        imageSliderAdapter.submitList(imageUrls)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setImageSlider()
        setDotsIndicator()
        setShareButton()
        setCloseButton()
        setBottomSheet()
        setEditButton()
    }

    private fun setDotsIndicator() {
        binding.viewPager.adapter = imageSliderAdapter

        binding.dotsIndicator.setViewPager2(binding.viewPager)
    }

    private fun setShareButton() {
        binding.btnShareContent.setOnClickListener {
            val contentToShare = collectDataForSharing()
            shareContent(contentToShare)
        }
    }

    //테스트용 데이터
    private fun setBottomSheet() {
        dialog?.setOnShowListener {
            val bottomSheet =
                (it as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
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

    private fun setEditButton() {
        binding.btnEdit.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_saveLogFragment)
        }
    }

    private fun setCloseButton() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(contentId: String): DetailFragment {
            return DetailFragment().apply {
                arguments = Bundle().apply {
                    putString("content_id", contentId)
                }
            }
        }
    }
}

package com.treasurehunt.ui.detail

import android.content.Intent
import android.content.res.Resources
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.treasurehunt.databinding.FragmentBottomsheetBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.treasurehunt.R

class BottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomsheetBinding? = null
    private val binding get() = _binding!!

    private var contentId: String? = null
    private lateinit var imageSliderAdapter: ImageSliderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomsheetBinding.inflate(inflater, container, false)
        contentId = arguments?.getString("content_id")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomSheet()
        setEditButton()
    }

    private fun setupBottomSheet() {
        val viewPager = binding.viewPager
        val imageUrls = listOf(
            "https://firebasestorage.googleapis.com/v0/b/treasurehunt-32565.appspot.com/o/uid%2Fimage%2F52.png?alt=media&token=4323ef4c-8380-46e5-91af-911e51011c41",
            "https://firebasestorage.googleapis.com/v0/b/treasurehunt-32565.appspot.com/o/uid%2Fimage%2F1000003321.png?alt=media&token=34cae484-ef2e-48a7-bd2f-54daa74a00c8",
            R.drawable.gajwa, R.drawable.gajwa, R.drawable.gajwa
        ) as List<Any>

        imageSliderAdapter = ImageSliderAdapter(imageUrls)
        viewPager.adapter = imageSliderAdapter

        val dotsIndicator = binding.dotsIndicator
        dotsIndicator.setViewPager2(viewPager)

        val btnShareContent = binding.btnShareContent
        btnShareContent.setOnClickListener {
            val contentToShare = collectDataForSharing()
            shareContent(contentToShare)
        }

        setupCloseButton()

        dialog?.setOnShowListener {
            val bottomSheet =
                (it as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            val dpValue = 580
            val pixels = (dpValue * Resources.getSystem().displayMetrics.density).toInt()
            val layoutParams = bottomSheet.layoutParams
            layoutParams.height = pixels
            bottomSheet.layoutParams = layoutParams
            behavior.peekHeight = layoutParams.height
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun collectDataForSharing(): String {
        val textViewContent = binding.textView.text.toString()
        val currentImageUrl = imageSliderAdapter.imageItems[imageSliderAdapter.currentPage]
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

    private fun setupCloseButton() {
        val btnClose = binding.btnClose
        btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun setEditButton() {
        binding.btnEdit.setOnClickListener {
            findNavController().navigate(R.id.action_bottomSheetFragment_to_saveLogFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(contentId: String): BottomSheetFragment {
            val fragment = BottomSheetFragment()
            val args = Bundle()
            args.putString("content_id", contentId)
            fragment.arguments = args
            return fragment
        }
    }
}

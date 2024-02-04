package com.treasurehunt.ui.detail

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
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

        contentId = arguments?.getString("content_id")

        setImageSlider()
        setDotsIndicator()
        setCloseButton()
        setBottomSheet()
        setEditButton()
    }

    private fun setDotsIndicator() {
        binding.viewPager.adapter = imageSliderAdapter

        binding.dotsIndicator.setViewPager2(binding.viewPager)
    }

    //테스트용 데이터
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

        binding.btnShareContent.setOnClickListener {
            createDynamicLinkAndShare()
        }
    }

    private fun createDynamicLinkAndShare() {
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("https://treasurehuntsesac.page.link/KPo2"))
            .setDomainUriPrefix("https://treasurehuntsesac.page.link")
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder()
                    .setMinimumVersion(1)
                    .build()
            )
            .buildDynamicLink()

        val dynamicLinkUri = dynamicLink.uri
        shareContent(dynamicLinkUri.toString())
    }

    private fun shareContent(link: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Check out Treasure Hunt: $link")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(intent, "내용 공유하기"))
    }

    private fun setEditButton() {
        binding.btnEdit.setOnClickListener {
            findNavController().navigate(R.id.action_detailFragment_to_saveLogFragment)
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
}

package com.treasurehunt.ui.detail

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.ktx.Firebase
import com.treasurehunt.R
import com.treasurehunt.databinding.FragmentLogdetailBinding
import com.treasurehunt.ui.model.LogModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LogDetailFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentLogdetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageSliderAdapter: ImageSliderAdapter
    private val viewModel: LogDetailViewModel by viewModels()
    private val args: LogDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogdetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupUI() {
        imageSliderAdapter = ImageSliderAdapter()
        binding.vpPhoto.adapter = imageSliderAdapter
        binding.diText.setViewPager2(binding.vpPhoto)

        loadData()
        setBottomSheet()
        setDotsIndicator()
        setCloseButton()
        //setEditButton()
        setDeleteButton()
        binding.btnEdit.setOnClickListener {
            //setPopupMenu(it)
        }
    }

    private fun setDotsIndicator() {
        binding.vpPhoto.adapter = imageSliderAdapter
        binding.diText.setViewPager2(binding.vpPhoto)
    }

    private fun setBottomSheet() {
        dialog?.setOnShowListener { dialogSheet ->
            val bottomSheet =
                (dialogSheet as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { view ->
                val behavior = BottomSheetBehavior.from(view)
                val dpValue = 580
                val pixels = (dpValue * Resources.getSystem().displayMetrics.density).toInt()
                view.layoutParams.height = pixels
                behavior.peekHeight = pixels
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        binding.btnShare.setOnClickListener {
            shareContent(getDynamicLink())
        }
    }

    private fun getDynamicLink(): String {
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse(LINK_URL))
            .setDomainUriPrefix(DOMAIN_URI_PREFIX)
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder()
                    .setMinimumVersion(1)
                    .build()
            )
            .buildDynamicLink()

        return dynamicLink.uri.toString()
    }

    private fun shareContent(link: String) {
        val shareText = getString(R.string.Logdetail_content, link)
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(intent, getString(R.string.Logdetail_chooser_title)))
    }

    private fun updateUIWithLogModel(logModel: LogModel) {
        val imageItems = logModel.imageUrls.map { ImageItem.Url(it) }
        imageSliderAdapter.submitList(imageItems)
        binding.vpPhoto.adapter = imageSliderAdapter
        binding.diText.setViewPager2(binding.vpPhoto)
        binding.tvText.text = logModel.text
    }

    private fun loadData() {
        val placeId = args.placeId
        val logModel = args.logModel
        if (placeId.isNotEmpty()) {
            viewLifecycleOwner.lifecycleScope.launch {
                val logModel = viewModel.getPlace(placeId)
                logModel?.let {
                    updateUIWithLogModel(it)
                }
            }
        } else {
            logModel?.let {
                updateUIWithLogModel(it)
            }
        }
    }

    //    private fun setDeleteButton() {
//        binding.btnDelete.setOnClickListener {
//            val placeId = args.placeId
//            val userId = Firebase.auth.currentUser?.uid
//
//            if (placeId.isNotEmpty() && userId != null) {
//                viewModel.deletePost(placeId, userId)
//                Toast.makeText(requireContext(), "삭제", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(requireContext(), "삭제 실패", Toast.LENGTH_SHORT).show()
//            }
//        }
    private fun setDeleteButton() {
        binding.btnDelete.setOnClickListener {
            val placeId = args.placeId
            val userId = Firebase.auth.currentUser?.uid

            if (placeId.isNotEmpty() && userId != null) {
                viewModel.viewModelScope.launch {
                    val placeDTO = viewModel.getRemotePlace(placeId)
                    placeDTO.log?.let { logId ->
                        viewModel.deletePost(placeId, userId, logId)
                        Toast.makeText(requireContext(), "삭제", Toast.LENGTH_SHORT).show()
                        dismiss()
                    } ?: run {
                        Toast.makeText(requireContext(), "삭제 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "삭제 처리 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

//    private fun setPopupMenu(view: View) {
//        val popup = PopupMenu(requireContext(), view)
//        popup.menuInflater.inflate(R.menu.edit_menu, popup.menu)
//
//        popup.setOnMenuItemClickListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.action_edit -> {
//                    true
//                }
//                R.id.action_delete -> {
//                    val placeId = args.placeId
//                    val userId = Firebase.auth.currentUser?.uid
//
//                    if (placeId.isNotEmpty() && userId != null) {
//                        viewModel.viewModelScope.launch {
//                            val placeDTO = viewModel.getRemotePlace(placeId)
//                            placeDTO.log?.let { logId ->
//                                viewModel.deletePost(placeId, userId, logId)
//                                Toast.makeText(requireContext(), "삭제", Toast.LENGTH_SHORT).show()
//                                dismiss()
//                            } ?: run {
//                                Toast.makeText(requireContext(), "삭제 실패", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    } else {
//                        Toast.makeText(requireContext(), "삭제 처리 실패", Toast.LENGTH_SHORT).show()
//                    }
//                    true
//                }
//                else -> false
//            }
//        }
//    }


    private fun setCloseButton() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val LINK_URL = "https://treasurehuntsesac.page.link/KPo2"
        const val DOMAIN_URI_PREFIX = "https://treasurehuntsesac.page.link"
    }
}
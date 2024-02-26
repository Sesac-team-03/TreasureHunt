package com.treasurehunt.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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

const val LINK_URL = "https://treasurehuntsesac.page.link/KPo2"
const val DOMAIN_URI_PREFIX = "https://treasurehuntsesac.page.link"

@AndroidEntryPoint
class LogDetailFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentLogdetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageSliderAdapter: ImageSliderAdapter
    private val viewModel: LogDetailViewModel by viewModels()
    private val args: LogDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogdetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDotsIndicator()
        loadLog()
        setPopupMenu(binding.btnEdit)
        setCloseButton()
        setShareButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setDotsIndicator() {
        imageSliderAdapter = ImageSliderAdapter()
        binding.vpPhoto.adapter = imageSliderAdapter
        binding.diText.setViewPager2(binding.vpPhoto)
    }

    private fun loadLog() {
        val placeId = args.remotePlaceId
        if (placeId.isNotEmpty()) {
            viewLifecycleOwner.lifecycleScope.launch {
                val log = viewModel.getLogByRemotePlaceId(placeId) ?: return@launch
                setTextAndImages(log)
                updateDotsIndicator()
            }
        } else {
            args.log?.let { log ->
                setTextAndImages(log)
                updateDotsIndicator()
            }
        }
    }

    private fun setTextAndImages(log: LogModel) {
        binding.tvText.text = log.text
        val imageItems = log.imageUrls.map { ImageItem.Url(it) }
        imageSliderAdapter.submitList(imageItems)
    }

    private fun updateDotsIndicator() {
        binding.vpPhoto.adapter = imageSliderAdapter
        binding.diText.setViewPager2(binding.vpPhoto)
    }

    private fun setCloseButton() {
        binding.btnClose.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setShareButton() {
        binding.btnShare.setOnClickListener {
            shareContent(getDynamicLink())
        }
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

    private fun getDynamicLink(): String {
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse(LINK_URL))
            .setDomainUriPrefix(DOMAIN_URI_PREFIX)
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder().setMinimumVersion(1).build()
            )
            .buildDynamicLink()

        return dynamicLink.uri.toString()
    }

    private fun setPopupMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.edit_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    binding.btnEdit.setOnClickListener {
                        findNavController().navigate(R.id.action_logDetailFragment_to_saveLogFragment)
                    }
                    true
                }

                R.id.action_delete -> {
                    val placeId = args.remotePlaceId
                    val userId = Firebase.auth.currentUser?.uid

                    if (placeId.isNotEmpty() && userId != null) {
                        viewLifecycleOwner.lifecycleScope.launch {
                            val placeDTO = viewModel.getRemotePlace(placeId)
                            placeDTO.remoteLogId?.let { logId ->
                                viewModel.deleteLogAndAssociatedData(logId, placeId, userId)
                                Toast.makeText(requireContext(), "삭제", Toast.LENGTH_SHORT)
                                    .show()
                                dismiss()
                            } ?: run {
                                Toast.makeText(requireContext(), "삭제 실패", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "삭제 처리 실패", Toast.LENGTH_SHORT)
                            .show()
                    }
                    true
//                        val action =
//                            LogDetailFragmentDirections.actionLogDetailFragmentToHomeFragment(args.remotePlaceId)
//                        findNavController().navigate(action)

                }

                else -> false
            }
            false
        }
        view.setOnClickListener { popup.show() }
    }
}

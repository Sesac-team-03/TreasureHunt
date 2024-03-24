package com.treasurehunt.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.treasurehunt.R
import com.treasurehunt.databinding.FragmentLogDetailBinding
import com.treasurehunt.ui.detail.adapter.LogDetailViewPagerAdapter
import com.treasurehunt.ui.model.LogModel
import com.treasurehunt.ui.model.TextTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

const val LINK_URL = "https://treasurehuntsesac.page.link/KPo2"
const val DOMAIN_URI_PREFIX = "https://treasurehuntsesac.page.link"
const val MIME_TYPE_TEXT_PLAIN = "text/plain"

@AndroidEntryPoint
class LogDetailFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentLogDetailBinding? = null
    private val binding get() = _binding!!
    private val logDetailViewPagerAdapter = LogDetailViewPagerAdapter()
    private val viewModel: LogDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setDotsIndicator()
        loadLog()
        setPopupButton(getPopupMenu())
        setCloseButton()
        setShareButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setAdapter() {
        binding.vpImage.adapter = logDetailViewPagerAdapter
    }

    private fun setDotsIndicator() {
        binding.diImage.setViewPager2(binding.vpImage)
    }

    private fun loadLog() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.logResult.collect { logResult ->
                when (logResult) {
                    is LogResult.LogLoaded -> {
                        hideLoadingBar()
                        if (logResult.value.imageUrls.isEmpty()) {
                            setThemedText(logResult.value)
                        } else {
                            setTextAndImages(logResult.value)
                        }
                        setDotsIndicator()
                    }

                    is LogResult.LogLoading -> {
                        showLoadingBar()
                    }

                    is LogResult.LogNotLoaded -> {
                        hideLoadingBar()
                        showLoadFailMessage()
                    }
                }
            }
        }
    }

    private fun showLoadingBar() {
        binding.cpiLoading.isVisible = true
    }

    private fun hideLoadingBar() {
        binding.cpiLoading.isVisible = false
    }

    private fun showLoadFailMessage() {
        binding.tvLoadFail.isVisible = true
    }

    private fun setTextAndImages(log: LogModel) {
        val imageItem = LogDetailItem.TextItem(
            value = log.text,
            theme = TextTheme.entries[log.theme]
        )
        logDetailViewPagerAdapter.submitList(listOf(imageItem))
    }

    private fun setThemedText(log: LogModel) {
        val item = LogDetailItem.TextItem(
            value = log.text,
            theme = TextTheme.entries[log.theme]
        )
        logDetailViewPagerAdapter.submitList(listOf(item))
    }

    private fun setImagesAndText(log: LogModel) {
        val items = log.imageUrls.map {
            LogDetailItem.ImageItem(storageReference = Firebase.storage.getReferenceFromUrl(it))
        }
        logDetailViewPagerAdapter.submitList(items)
        binding.tvText.text = log.text
    }

    private fun setPopupButton(popup: PopupMenu) {
        binding.ibPopup.setOnClickListener {
            popup.show()
        }
    }

    private fun getPopupMenu(): PopupMenu {
        return PopupMenu(requireContext(), binding.ibPopup).apply {
            menuInflater.inflate(R.menu.edit_menu, menu)

            setOnMenuItemClickListener { menuItem ->
                val menu = LogDetailMenuAction.from(menuItem.itemId)
                    ?: return@setOnMenuItemClickListener false
                when (menu) {
                    LogDetailMenuAction.EDIT -> {
                        setEditLog()
                        true
                    }

                    LogDetailMenuAction.DELETE -> {
                        setDeleteLog()
                        true
                    }
                }
            }
        }
    }

    private fun setEditLog() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.logResult.collect { logResult ->
                if (logResult !is LogResult.LogLoaded) {
                    showToast(R.string.logdetail_log_still_loading_message)
                    return@collect
                }

                val mapSymbol = viewModel.run {
                    getMapSymbol(args.remotePlaceId, args.log)
                }
                val action =
                    LogDetailFragmentDirections.actionLogDetailFragmentToSaveLogFragment(
                        mapSymbol, logResult.value
                    )
                findNavController().navigate(action)
            }
        }
    }

    private fun setDeleteLog() {
        viewLifecycleOwner.lifecycleScope.launch {
            val userId = Firebase.auth.currentUser?.uid ?: return@launch showErrorMessage()
            val placeId = viewModel.args.remotePlaceId
            val log = viewModel.args.log

            if (placeId != null) {
                handleDeleteFromHome(placeId, userId)
            } else if (log != null) {
                handleDeleteFromFeed(log, userId)
            } else {
                showErrorMessage()
            }
        }
    }

    private suspend fun handleDeleteFromHome(placeId: String, userId: String) {
        val placeDTO = viewModel.getRemotePlace(placeId)

        placeDTO.remoteLogId?.let { logId ->
            deleteLog(logId, placeId, userId)
        } ?: return showErrorMessage()

        val action = LogDetailFragmentDirections.actionLogDetailFragmentToHomeFragment(
            placeId
        )
        findNavController().navigate(action)
    }

    private suspend fun handleDeleteFromFeed(log: LogModel, userId: String) {
        log.remoteId?.let { logId ->
            deleteLog(logId, log.remotePlaceId, userId)
        } ?: return showErrorMessage()

        findNavController().navigate(LogDetailFragmentDirections.actionLogDetailFragmentToFeedFragment())
    }

    private fun showErrorMessage() {
        kotlin.run {
            showToast(R.string.logdetail_error_message)
        }
    }

    private suspend fun deleteLog(
        logId: String,
        placeId: String,
        userId: String
    ) {
        viewModel.deleteLogAndAssociatedData(logId, placeId, userId)
        showToast(R.string.logdetail_log_deleted_message)
        dismiss()
    }

    private fun setCloseButton() {
        binding.ibClose.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setShareButton() {
        binding.ibShare.setOnClickListener {
            shareContent(getDynamicLink())
        }
    }

    private fun shareContent(link: String) {
        val shareText = getString(R.string.logdetail_content, link)
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = MIME_TYPE_TEXT_PLAIN
        }
        startActivity(Intent.createChooser(intent, getString(R.string.logdetail_chooser_title)))
    }

    private fun getDynamicLink(): String {
        val dynamicLink =
            FirebaseDynamicLinks.getInstance().createDynamicLink().setLink(Uri.parse(LINK_URL))
                .setDomainUriPrefix(DOMAIN_URI_PREFIX).setAndroidParameters(
                    DynamicLink.AndroidParameters.Builder().setMinimumVersion(1).build()
                ).buildDynamicLink()

        return dynamicLink.uri.toString()
    }

    private fun showToast(stringResId: Int, durationId: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(requireContext(), stringResId, durationId)
            .show()
    }

    enum class LogDetailMenuAction(val itemId: Int) {
        EDIT(R.id.action_edit), DELETE(R.id.action_delete);

        companion object {
            fun from(itemId: Int) = entries.find { it.itemId == itemId }
        }
    }
}
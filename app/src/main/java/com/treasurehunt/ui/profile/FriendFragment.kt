package com.treasurehunt.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.treasurehunt.R
import com.treasurehunt.data.remote.model.UserDTO
import com.treasurehunt.data.remote.model.toUserModel
import com.treasurehunt.databinding.FragmentFriendBinding
import com.treasurehunt.ui.model.UserModel
import com.treasurehunt.util.hide
import com.treasurehunt.util.show
import com.treasurehunt.util.showSnackbar
import kotlinx.coroutines.launch

class FriendFragment : Fragment() {

    private var _binding: FragmentFriendBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FriendViewModel by viewModels { FriendViewModel.Factory }
    private lateinit var searchFriendAdapter: SearchFriendAdapter
    private lateinit var friendAdapter: FriendAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSearchFriendAdapter()
        setSearchFriend(searchFriendAdapter)
        setBtnHideSearchResult()

        initFriendAdapter()
        showFriendList()
    }

    private fun initSearchFriendAdapter() {
        searchFriendAdapter = SearchFriendAdapter(getAddFriendClickListener()).apply {
            binding.rvSearchResult.adapter = this
        }
    }

    private fun setSearchFriend(adapter: SearchFriendAdapter) {
        binding.tietSearchFriend.setOnEditorActionListener { _, _, _ ->
            viewLifecycleOwner.lifecycleScope.launch {
                val uid = validateRegisteredUser() ?: return@launch

                showSearchResult(uid, adapter)
            }
            true
        }
    }

    private fun validateRegisteredUser(): String? {
        val isSignedInAsUser = viewModel.uiState.value.signedInAsRegisteredUser
        val uid = viewModel.uiState.value.uid

        return if (isSignedInAsUser) uid else null
    }

    private suspend fun showSearchResult(uid: String, adapter: SearchFriendAdapter) {
        val searchKeyword = binding.tietSearchFriend.text.toString()
        val searchResult = search(uid, searchKeyword)

        binding.tietSearchFriend.clearFocus()
        adapter.submitList(searchResult)
        hideKeyboard()

        binding.btnHideSearchResult.show()
        binding.rvSearchResult.show()
        handleNoSearchResult(searchResult)
        binding.rvFriendList.hide()
    }

    private suspend fun search(uid: String, startAt: String): List<UserModel> {
        if (startAt == "") return emptyList()

        val friendIds = viewModel.uiState.value.friends.map { it.remoteId!! }
        return viewModel.search(startAt)
            .filterNotFriend(friendIds, uid)
            .sortedByEmail()
    }

    private fun List<UserDTO>.filterNotFriend(friends: List<String>, uid: String): List<UserDTO> {
        return filterNot {
            friends.contains(it.remoteId) || it.remoteId == uid
        }
    }

    private fun List<UserDTO>.sortedByEmail(): List<UserModel> {
        return map {
            it.toUserModel(it.remoteId!!)
        }.sortedBy {
            it.email
        }
    }

    private fun hideKeyboard() {
        val keyboard =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        keyboard.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun setBtnHideSearchResult() {
        binding.btnHideSearchResult.setOnClickListener {
            binding.btnHideSearchResult.hide()
            binding.rvSearchResult.hide()
            binding.groupNoSearchResult.hide()
            binding.rvFriendList.show()
        }
    }

    private fun handleNoSearchResult(searchResult: List<UserModel>) {
        with(binding.groupNoSearchResult) {
            if (searchResult.isEmpty()) {
                show()
            } else {
                hide()
            }
        }
    }

    private fun getAddFriendClickListener() = FriendClickListener { friend ->
        viewLifecycleOwner.lifecycleScope.launch {
            val uid = validateRegisteredUser() ?: return@launch
            viewModel.addFriend(uid, friend.remoteId!!)
            (binding.rvSearchResult.adapter as SearchFriendAdapter).submitList(emptyList())

            val message = getString(R.string.profile_friend_added, friend.nickName)
            binding.root.showSnackbar(message)
        }
    }

    private fun initFriendAdapter() {
        friendAdapter = FriendAdapter(getRemoveFriendClickListener()).apply {
            binding.rvFriendList.adapter = this
        }
    }

    private fun showFriendList() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    val friends = uiState.friends.map { it.toUserModel(it.remoteId ?: "") } // check
                    friendAdapter.submitList(friends)
                }
            }
        }
    }

    private fun getRemoveFriendClickListener() = FriendClickListener { friend ->
        viewLifecycleOwner.lifecycleScope.launch {
            val uid = Firebase.auth.currentUser?.uid ?: return@launch
            viewModel.removeFriend(uid, friend.remoteId!!)

            val message = getString(R.string.profile_friend_removed, friend.nickName)
            binding.root.showSnackbar(message)
        }
    }
}
package com.treasurehunt.ui.profile

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.treasurehunt.R
import com.treasurehunt.data.remote.model.UserDTO
import com.treasurehunt.data.remote.model.toUserModel
import com.treasurehunt.databinding.FragmentFriendBinding
import com.treasurehunt.util.showSnackbar
import kotlinx.coroutines.launch

class FriendFragment : Fragment() {
    // 데이터를 반응형으로. 친구 삭제하고 검색해도, 삭제하기 전처럼 검색에 안나옴. 그리고 친구추가해도 실시간 반영 안됨

    private var _binding: FragmentFriendBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FriendViewModel by viewModels { FriendViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addFriendClickListener = FriendClickListener { friend ->
            viewLifecycleOwner.lifecycleScope.launch {
                binding.tietSearchFriend.text = Editable.Factory.getInstance().newEditable("")
                val uid = Firebase.auth.currentUser?.uid ?: return@launch
                viewModel.addFriend(uid, friend.remoteId!!)
                (binding.rvSearchResult.adapter as SearchFriendAdapter).submitList(emptyList())

                val message = getString(R.string.profile_friend_added, friend.nickName)
                binding.root.showSnackbar(message)
            }
        }

        val searchFriendAdapter = SearchFriendAdapter(addFriendClickListener)
        binding.rvSearchResult.adapter = searchFriendAdapter

        setSearchFriend(searchFriendAdapter)

        val removeFriendClickListener = FriendClickListener { friend ->
            viewLifecycleOwner.lifecycleScope.launch {
                val uid = Firebase.auth.currentUser?.uid ?: return@launch
                viewModel.removeFriend(uid, friend.remoteId!!)
                (binding.rvFriendList.adapter as FriendAdapter).submitList(emptyList())

                val message = getString(R.string.profile_friend_removed, friend.nickName)
                binding.root.showSnackbar(message)
            }
        }

        val friendAdapter = FriendAdapter(removeFriendClickListener)
        binding.rvFriendList.adapter = friendAdapter

        initFriendList(friendAdapter)
    }

    private fun setSearchFriend(adapter: SearchFriendAdapter) {
        binding.tietSearchFriend.setOnEditorActionListener { _, _, _ ->
            viewLifecycleOwner.lifecycleScope.launch {
                val uid = Firebase.auth.currentUser?.uid ?: return@launch
                val isSignedInAsUser = !Firebase.auth.currentUser!!.isAnonymous
                if (!isSignedInAsUser) return@launch

                val searchKeyword = binding.tietSearchFriend.text.toString()
                val searchResult = search(uid, searchKeyword)

                binding.tietSearchFriend.clearFocus()

                val users = searchResult.map {
                    it.value.toUserModel(it.key)
                }.sortedBy {
                    it.email
                }
                adapter.submitList(users)

                val keyboard =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                keyboard.hideSoftInputFromWindow(binding.root.windowToken, 0)
            }
            true
        }
    }

    private suspend fun search(uid: String, startAt: String): Map<String, UserDTO> {
        if (startAt == "") return emptyMap()

        val friends = viewModel.getRemoteUser(uid).friends
        return viewModel.search(startAt).filterNot {
            friends[it.key] == true
        }
    }

    private fun initFriendList(adapter: FriendAdapter) {
        viewLifecycleOwner.lifecycleScope.launch {
            val uid = Firebase.auth.currentUser?.uid ?: return@launch
            val friendIds = viewModel.getRemoteUser(uid).friends.filter { it.value }
            val friends = friendIds.map {
                viewModel.getRemoteUser(it.key).toUserModel(it.key)
            }
            adapter.submitList(friends)
        }
    }
}
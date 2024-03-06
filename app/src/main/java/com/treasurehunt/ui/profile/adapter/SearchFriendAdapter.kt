package com.treasurehunt.ui.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.treasurehunt.R
import com.treasurehunt.databinding.ItemSearchFriendBinding
import com.treasurehunt.ui.model.UserModel
import com.treasurehunt.ui.profile.FriendClickListener

class SearchFriendAdapter(private val friendClickListener: FriendClickListener, private val isClickable: Boolean) :
    ListAdapter<UserModel, SearchFriendAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position], friendClickListener, isClickable)
    }

    class ViewHolder(
        private val binding: ItemSearchFriendBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private var _isClickable = false

        fun bind(friend: UserModel, friendClickListener: FriendClickListener, isClickable: Boolean) {
            bindImage(binding.ivProfileImage, friend.profileImage)
            binding.tvNickname.text = friend.nickName
            binding.tvEmail.text = friend.email.toString().substringBefore('@')

            _isClickable = isClickable
            binding.ibAdd.setOnClickListener {
                if (_isClickable) {
                    _isClickable = false
                    friendClickListener.onClick(friend)
                }
            }
        }

        private fun bindImage(view: ImageView, url: String?) {
            view.scaleType = if (url.isNullOrEmpty()) {
                ImageView.ScaleType.CENTER_INSIDE
            } else {
                ImageView.ScaleType.CENTER_CROP
            }

            Glide.with(view.context)
                .load(url)
                .error(R.drawable.ic_no_profile_image)
                .into(view)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    ItemSearchFriendBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<UserModel>() {

            override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
                return oldItem.email == newItem.email
            }

            override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
package com.treasurehunt.ui.profile

import android.util.Log
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

class SearchFriendAdapter : ListAdapter<UserModel, SearchFriendAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
        Log.d("test$", currentList[position].nickName)
    }

    class ViewHolder(private val binding: ItemSearchFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: UserModel) {
            bindImageUrl(binding.ivProfileImage, user.profileImage)
            binding.tvNickname.text = user.nickName
            binding.tvEmail.text = user.email
        }

        private fun bindImageUrl(view: ImageView, url: String?) {
            when (url) {
                null -> view.run {
                    setImageResource(R.drawable.ic_no_profile_image)
                    scaleType = ImageView.ScaleType.CENTER_INSIDE
                }

                else -> view.run {
                    Glide.with(context).load(url)
                        .into(this)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
            }
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
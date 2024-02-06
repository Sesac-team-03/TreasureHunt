package com.treasurehunt.ui.profile

import com.treasurehunt.ui.model.UserModel

fun interface FriendClickListener {
    fun onClick(friend: UserModel)
}
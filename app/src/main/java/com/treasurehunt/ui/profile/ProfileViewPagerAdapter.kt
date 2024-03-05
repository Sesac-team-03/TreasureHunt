package com.treasurehunt.ui.profile

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.treasurehunt.R

class ProfileViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when(Tab.entries[position]) {
            Tab.FRIEND -> {
                FriendFragment()
            }
            Tab.COMMENT -> {
                // 임시
                FriendFragment()
            }
            Tab.TAG -> {
                // 임시
                FriendFragment()
            }
        }
    }

    enum class Tab(val restId: Int) {
        FRIEND(R.string.profile_tl_friend),
        COMMENT(R.string.profile_tl_comment),
        TAG(R.string.profile_tl_tag)
    }
}
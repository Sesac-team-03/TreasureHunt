package com.treasurehunt.ui.model

import com.treasurehunt.R

enum class TextTheme(val backgroundResId: Int?, val textColorResId: Int) {
    DEFAULT(null, R.color.black),
    LIME_BLUE(R.drawable.text_theme_lime_blue, R.color.white),
    PURPLE(R.drawable.text_theme_purple, R.color.white),
    MINT(R.drawable.text_theme_mint, R.color.black),
    ORANGE_BLACK(R.drawable.text_theme_orange_black, R.color.white),
    PEACH(R.drawable.text_theme_peach, R.color.white)
}
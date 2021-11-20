package com.example.liveplayerstats

import android.view.MenuItem

interface OnActionItemClickListener {
    fun onActionItemClick(item: MenuItem)
    fun onCanceled()
}
package com.example.liveplayerstats.playercomponents

import android.view.MenuItem

interface OnActionItemClickListener {
    fun onActionItemClick(item: MenuItem)
    fun onCanceled()
}
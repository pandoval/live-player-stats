package com.example.liveplayerstats

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes

class MainActionModeCallback : ActionMode.Callback {

    var onActionItemClickListener: OnActionItemClickListener? = null

    private var mode: ActionMode? = null
    @MenuRes
    private var menuResId: Int = 0
    private var title: String? = null
    private var subtitle: String? = null

    override fun onCreateActionMode(p0: ActionMode?, p1: Menu?): Boolean {
        if (p0 != null) {
            this.mode = p0
            p0.menuInflater.inflate(menuResId, p1)
            p0.title = title
            p0.subtitle = subtitle
            return true
        }
        return false
    }

    override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(p0: ActionMode?, p1: MenuItem?): Boolean {
        if (p0 != null && p1 != null) {
            onActionItemClickListener?.onActionItemClick(p1)
            p0.finish()
            return true
        }
        return false
    }

    override fun onDestroyActionMode(p0: ActionMode?) {
        this.mode = null
    }

    fun startActionMode(view: View,
                        @MenuRes menuResId: Int,
                        title: String? = null,
                        subtitle: String? = null) {
        this.menuResId = menuResId
        this.title = title
        this.subtitle = subtitle
        view.startActionMode(this)
    }

    fun finishActionMode() {
        mode?.finish()
    }

}
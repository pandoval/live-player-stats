package com.example.liveplayerstats.gameinfoactivity

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.liveplayerstats.R
import com.example.liveplayerstats.enums.TeamImgResources
import com.example.liveplayerstats.pbp.Play

class PlaysAdapter: ListAdapter<Play, PlaysAdapter.PlaysViewHolder>(PlaysComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaysViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.plays_recyclerview_item, parent, false)
        return PlaysViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaysViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    inner class PlaysViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val playsClock: TextView = itemView.findViewById(R.id.playsClock)
        private val playsIcon: ImageView = itemView.findViewById(R.id.playsIcon)
        private val playsDesc: TextView = itemView.findViewById(R.id.playsDesc)

        private val teamArray = itemView.resources.getStringArray(R.array.nba_team_id)
        private val teamImgResources = TeamImgResources.values()
        private val resourcesMap: Map<String, TeamImgResources> = teamArray.zip(teamImgResources).toMap()

        fun bind(play: Play) {
            val periodChange = play.description == "Start Period" || play.description == "End Period"
            if (play.isScoreChange || periodChange) {
                playsClock.typeface = Typeface.DEFAULT_BOLD
                playsDesc.typeface = Typeface.DEFAULT_BOLD
            } else {
                playsClock.typeface = Typeface.DEFAULT
                playsDesc.typeface = Typeface.DEFAULT
            }
            playsClock.text = play.clock
            Glide.with(itemView).load(resourcesMap[play.teamId]?.id).into(playsIcon)

            if (periodChange) {
                playsDesc.text = play.formatted.description
                itemView.setBackgroundResource(R.color.dark_gray)
            } else {
                playsDesc.text = play.description
                itemView.setBackgroundResource(R.color.darker_gray)
            }
        }
    }

    class PlaysComparator : DiffUtil.ItemCallback<Play>() {
        override fun areItemsTheSame(oldItem: Play, newItem: Play): Boolean {
            return oldItem.description == newItem.description
        }

        override fun areContentsTheSame(oldItem: Play, newItem: Play): Boolean {
            return oldItem == newItem
        }

    }
}
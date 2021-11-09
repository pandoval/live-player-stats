package com.example.liveplayerstats.playercomponents

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.liveplayerstats.R
import com.example.liveplayerstats.boxscore.Boxscore
import com.google.android.material.imageview.ShapeableImageView

class PlayerListAdapter : ListAdapter<Pair<Player, Boxscore>, PlayerListAdapter.PlayerViewHolder>(PlayersComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.player_recyclerview_item, parent, false)

        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val pName: TextView = itemView.findViewById(R.id.pName)
        private val pPic: ShapeableImageView = itemView.findViewById(R.id.pPic)
        private val pTeam: TextView = itemView.findViewById(R.id.pTeam)

        fun bind(pair: Pair<Player, Boxscore>?) {
            if (pair != null) {
                val p = pair.first
                pName.text = p.name
                val idString = p.id
                Glide.with(itemView)
                    .load("https://cdn.nba.com/headshots/nba/latest/260x190/${idString}.png").centerCrop().circleCrop()
                    .into(pPic)
                pTeam.text =  p.teamName
            } else {
                Log.d("hi", "null")
            }
        }
    }

    class PlayersComparator : DiffUtil.ItemCallback<Pair<Player, Boxscore>>() {
        override fun areItemsTheSame(oldItem: Pair<Player, Boxscore>, newItem: Pair<Player, Boxscore>): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Pair<Player, Boxscore>, newItem: Pair<Player, Boxscore>): Boolean {
            return oldItem.first.id == newItem.first.id
        }
    }
}
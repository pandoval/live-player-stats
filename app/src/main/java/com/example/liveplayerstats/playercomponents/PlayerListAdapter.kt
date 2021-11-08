package com.example.liveplayerstats.playercomponents

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.liveplayerstats.R
import com.example.liveplayerstats.boxscore.Boxscore

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

        private val playerName: TextView = itemView.findViewById(R.id.playerName)

        fun bind(pair: Pair<Player, Boxscore>?) {
            if (pair != null) {
                Log.d("hi", pair.first.name)
                Log.d("hi", pair.second.stats.activePlayers.toString())
            } else {
                Log.d("hi", "null")
            }

            //this just test
            if (pair != null) {
                val text = "${pair.first.name} ${pair.second.basicGameData.hTeam} vs ${pair.second.basicGameData.vTeam}"
                playerName.text = text
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
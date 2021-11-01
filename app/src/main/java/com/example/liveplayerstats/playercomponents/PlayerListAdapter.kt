package com.example.liveplayerstats.playercomponents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.liveplayerstats.R

class PlayerListAdapter : ListAdapter<Player, PlayerListAdapter.PlayerViewHolder>(PlayersComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        return PlayerViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.name)
    }

    class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val playerItemView: TextView = itemView.findViewById(R.id.playerName)

        fun bind(text: String?) {
            playerItemView.text = text
        }

        companion object {
            fun create(parent: ViewGroup): PlayerViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.player_recyclerview_item, parent, false)
                return PlayerViewHolder(view)
            }
        }
    }

    class PlayersComparator : DiffUtil.ItemCallback<Player>() {
        override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem.name == newItem.name
        }
    }
}
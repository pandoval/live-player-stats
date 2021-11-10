package com.example.liveplayerstats.playercomponents

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.liveplayerstats.Months
import com.example.liveplayerstats.R
import com.example.liveplayerstats.TeamImgResources
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

        private val pHTeam: TextView = itemView.findViewById(R.id.pHTeam)
        private val pHTeamRecord: TextView = itemView.findViewById(R.id.pHTeamRecord)
        private val pHTeamPoints: TextView = itemView.findViewById(R.id.pHTeamPoints)
        private val pHTeamLogo: ImageView = itemView.findViewById(R.id.pHTeamLogo)
        private val pVTeam: TextView = itemView.findViewById(R.id.pVTeam)
        private val pVTeamRecord: TextView = itemView.findViewById(R.id.pVTeamRecord)
        private val pVTeamPoints: TextView = itemView.findViewById(R.id.pVTeamPoints)
        private val pVTeamLogo: ImageView = itemView.findViewById(R.id.pVTeamLogo)
        private val pQuarter: TextView = itemView.findViewById(R.id.pQuarter)
        private val pClock: TextView = itemView.findViewById(R.id.pClock)
        private val pFinal: TextView = itemView.findViewById(R.id.pFinal)

        val teamArray = itemView.resources.getStringArray(R.array.nba_team_id)
        val teamImgResources = TeamImgResources.values()
        val resourcesMap: Map<String,TeamImgResources> = teamArray.zip(teamImgResources).toMap()

        fun bind(pair: Pair<Player, Boxscore>?) {
            if (pair != null) {
                val p = pair.first
                val b = pair.second
                Glide.with(itemView)
                    .load("https://cdn.nba.com/headshots/nba/latest/260x190/${p.id}.png").centerCrop().circleCrop()
                    .into(pPic)
                pName.text = p.name
                pTeam.text = p.teamName

                val hTeam = b.basicGameData.hTeam
                pHTeam.text = hTeam.triCode
                val hRecord = "${hTeam.win}-${hTeam.loss}"
                pHTeamRecord.text = hRecord
                Glide.with(itemView)
                    .load(resourcesMap[hTeam.teamId]?.id).into(pHTeamLogo)
                pHTeamPoints.text = hTeam.score

                val vTeam = b.basicGameData.vTeam
                pVTeam.text = vTeam.triCode
                val vRecord = "${vTeam.win}-${vTeam.loss}"
                pVTeamRecord.text = vRecord
                Glide.with(itemView)
                    .load(resourcesMap[vTeam.teamId]?.id).into(pVTeamLogo)
                pVTeamPoints.text = vTeam.score

                val quarterText = "Q${b.basicGameData.period.current.toString()}"
                when (b.basicGameData.statusNum) {
                    1 -> {
                        pQuarter.text = getDateString(b)
                        pClock.text = b.basicGameData.startTimeEastern
                    }
                    2 -> {
                        if (b.basicGameData.period.isHalftime) {
                            pFinal.text = "Halftime"
                        } else if (b.basicGameData.period.isEndOfPeriod) {
                            val endOfQ = "End of $quarterText"
                            pFinal.text = endOfQ
                        } else {
                            pQuarter.text = quarterText
                            pClock.text = b.basicGameData.clock
                        }
                    }
                    3 -> {
                        pFinal.visibility = View.VISIBLE
                    }
                }

            } else {
                pFinal.text = "Error"
                pFinal.visibility = View.VISIBLE
            }
        }
    }

    private fun getDateString(b: Boxscore): String {
        val startDateEastern = b.basicGameData.startDateEastern
        val month = startDateEastern.substring(4,6)
        val day = startDateEastern.substring(6)
        val monthString = Months.values()[Integer.parseInt(month)-1].string
        val dayNum = Integer.parseInt(day)
        return "$monthString $dayNum"
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
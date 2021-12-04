package com.example.liveplayerstats.playercomponents

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.liveplayerstats.MainActivity
import com.example.liveplayerstats.enums.Months
import com.example.liveplayerstats.R
import com.example.liveplayerstats.enums.TeamImgResources
import com.example.liveplayerstats.boxscore.ActivePlayer
import com.example.liveplayerstats.boxscore.BoxScore
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView

class PlayerListAdapter(private val listener: OnItemClickListener,
private val longClickListener: OnItemLongClickListener, private val context: Context
) :
    ListAdapter<Pair<Player, BoxScore>, PlayerListAdapter.PlayerViewHolder>(PlayersComparator()) {

    var selectionMode = false
    var newSelectionMode = false
    var firstId: String = ""
    val selectedIds = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.player_recyclerview_item, parent, false)

        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val current = getItem(position)
        Log.d("onBind", selectedIds.toString())
        holder.bind(current)
    }

    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, View.OnLongClickListener {

        private val pName: TextView = itemView.findViewById(R.id.pName)
        private val pPic: ShapeableImageView = itemView.findViewById(R.id.pPic)
        private val pTeam: TextView = itemView.findViewById(R.id.pTeam)
        private val pIndicator: TextView = itemView.findViewById(R.id.pIndicator)
        private val pStatus: TextView = itemView.findViewById(R.id.pStatus)

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

        private val boxscoreSeparator: View = itemView.findViewById(R.id.boxscoreSeparator)
        private val horizScrollView: HorizontalScrollView = itemView.findViewById(R.id.pHorizScroll)
        private val mainCardView = itemView.findViewById<MaterialCardView>(R.id.mainCardView)

        private val mainMin: TextView = itemView.findViewById(R.id.mainMin)
        private val mainPts: TextView = itemView.findViewById(R.id.mainPts)
        private val mainReb: TextView = itemView.findViewById(R.id.mainReb)
        private val mainAst: TextView = itemView.findViewById(R.id.mainAst)
        private val mainStl: TextView = itemView.findViewById(R.id.mainStl)
        private val mainBlk: TextView = itemView.findViewById(R.id.mainBlk)
        private val mainFg: TextView = itemView.findViewById(R.id.mainFg)
        private val main3p: TextView = itemView.findViewById(R.id.main3p)
        private val mainFt: TextView = itemView.findViewById(R.id.mainFt)
        private val mainOreb: TextView = itemView.findViewById(R.id.mainOreb)
        private val mainDreb: TextView = itemView.findViewById(R.id.mainDreb)
        private val mainTov: TextView = itemView.findViewById(R.id.mainTov)
        private val mainPf: TextView = itemView.findViewById(R.id.mainPf)
        private val mainPm: TextView = itemView.findViewById(R.id.mainPm)

        private val teamArray = itemView.resources.getStringArray(R.array.nba_team_id)
        private val teamImgResources = TeamImgResources.values()
        private val resourcesMap: Map<String, TeamImgResources> = teamArray.zip(teamImgResources).toMap()

        private val defaultColor = pStatus.textColors.defaultColor

        private lateinit var currentPlayer: Player

        private lateinit var ap: ActivePlayer
        private fun setBoxscore(p: Player, b: BoxScore, gameFinished: Boolean = false) {
            var playerActive = false
            for (player in b.stats.activePlayers) {
                if (p.id == player.personId) {
                    ap = player
                    playerActive = true
                    break
                }
            }
            if (playerActive) {
                boxscoreSeparator.visibility = View.VISIBLE
                horizScrollView.visibility = View.VISIBLE

                mainMin.text = ap.min
                mainPts.text = ap.points
                mainReb.text = ap.totReb
                mainAst.text = ap.assists
                mainStl.text = ap.steals
                mainBlk.text = ap.blocks
                val fgText = "${ap.fgm}-${ap.fga}"
                mainFg.text = fgText
                val threeText = "${ap.tpm}-${ap.tpa}"
                main3p.text = threeText
                val ftText = "${ap.ftm}-${ap.fta}"
                mainFt.text = ftText
                mainOreb.text = ap.offReb
                mainDreb.text = ap.defReb
                mainTov.text = ap.turnovers
                mainPf.text = ap.pFouls
                mainPm.text = ap.plusMinus

                if (ap.isOnCourt && !gameFinished) {
                    setIndicator(ContextCompat.getColor(context, R.color.green_indicator), "On court")
                } else if(!gameFinished) {
                    setIndicator(defaultColor, "On bench")
                }
            } else {
                setIndicator(ContextCompat.getColor(context, R.color.red_indicator), "Inactive")
            }
        }

        fun bind(pair: Pair<Player, BoxScore>?) {
            currentPlayer = pair!!.first

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
                pQuarter.text = ""
                pClock.text = ""
                pFinal.visibility = View.INVISIBLE
                horizScrollView.visibility = View.GONE
                boxscoreSeparator.visibility = View.GONE
                pIndicator.visibility = View.GONE
                pStatus.visibility = View.GONE
                when (b.basicGameData.statusNum) {
                    1 -> {
                        pQuarter.text = getDateString(b)
                        pClock.text = b.basicGameData.startTimeEastern
                        pHTeamPoints.text = ""
                        pVTeamPoints.text = ""
                    }
                    2 -> {
                        if (b.basicGameData.period.isHalftime) {
                            pFinal.visibility = View.VISIBLE
                            pFinal.text = "Halftime"
                        } else if (b.basicGameData.period.isEndOfPeriod) {
                            pFinal.visibility = View.VISIBLE
                            val endOfQ = "End of $quarterText"
                            pFinal.text = endOfQ
                        } else {
                            pQuarter.text = quarterText
                            pClock.text = b.basicGameData.clock
                        }
                        setBoxscore(p, b)
                    }
                    3 -> {
                        pFinal.visibility = View.VISIBLE
                        pFinal.text = "Final"
                        setBoxscore(p, b, true)
                    }
                }

            } else {
                pFinal.visibility = View.VISIBLE
                pFinal.text = "Error"
            }
            if (selectionMode) {
                itemView.setOnClickListener {
                    itemSelected(currentPlayer.id)
                }
                itemView.setOnLongClickListener {
                    itemSelected(currentPlayer.id)
                    true
                }
            } else {
                selectedIds.clear()
                mainCardView.strokeWidth = 0
                itemView.setOnClickListener(this)
                itemView.setOnLongClickListener(this)
            }

            if (newSelectionMode && currentPlayer.id == firstId) {
                itemSelected(firstId)
                newSelectionMode = false
            }
        }

        private fun setIndicator(color: Int, text: String) {
            pIndicator.visibility = View.VISIBLE
            pStatus.visibility = View.VISIBLE
            pIndicator.setTextColor(color)
            pStatus.setTextColor(color)
            pStatus.text = text
        }

        private fun itemSelected(id: String) {
            val main = context as MainActivity
            if (selectedIds.contains(id)) {
                selectedIds.remove(id)
                mainCardView.strokeWidth = 0
                if (selectedIds.size == 0) {
                    selectionMode = false
                    main.mainActionModeCallback.finishActionMode()
                    notifyDataSetChanged()
                }
            } else {
                selectedIds.add(id)
                mainCardView.strokeWidth = 10
            }
        }

        override fun onClick(p0: View?) {
            listener.onItemClick(currentPlayer.teamId)
        }

        override fun onLongClick(p0: View?): Boolean {
            longClickListener.onItemLongClick(currentPlayer.id)
            return true
        }
    }

    interface OnItemClickListener {
        fun onItemClick(teamId: String)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(id: String)
    }

    companion object {
        fun getDateString(b: BoxScore): String {
            val startDateEastern = b.basicGameData.startDateEastern
            val month = startDateEastern.substring(4,6)
            val day = startDateEastern.substring(6)
            val monthString = Months.values()[Integer.parseInt(month)-1].string
            val dayNum = Integer.parseInt(day)

            val currentDate = PlayerStatsRepository.getDate()
            if (startDateEastern == currentDate) {
                return "Today"
            }
            return "$monthString $dayNum"
        }
    }

    class PlayersComparator : DiffUtil.ItemCallback<Pair<Player, BoxScore>>() {
        override fun areItemsTheSame(oldItem: Pair<Player, BoxScore>, newItem: Pair<Player, BoxScore>): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Pair<Player, BoxScore>, newItem: Pair<Player, BoxScore>): Boolean {
            return oldItem.first.id == newItem.first.id
        }
    }

    override fun getItemId(position: Int): Long {
        return Integer.parseInt(getItem(position).first.id).toLong()
    }
}
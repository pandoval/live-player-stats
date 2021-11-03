package com.example.liveplayerstats.newplayercomponents

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.liveplayerstats.R
import com.example.liveplayerstats.playerlist.Standard
import com.google.android.material.imageview.ShapeableImageView

class NewPlayerAdapter(private val data: MutableList<Standard>, context: Context,
    private val listener: OnItemClickListener) :
    RecyclerView.Adapter<NewPlayerAdapter.ViewHolder>(), Filterable{

    val dataFull = data.toList()
    val tricodeArray = context.resources.getStringArray(R.array.nba_tricode)
    val teamArray = context.resources.getStringArray(R.array.nba_team_id)
    val teamMap: Map<String,String> = teamArray.zip(tricodeArray).toMap()

    val selectedPlayers = mutableListOf<Standard>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val playerNewName: TextView
        val playerNewTeam: TextView
        val playerNewPic: ShapeableImageView

        init {
            playerNewName = view.findViewById(R.id.playerNewName)
            playerNewTeam = view.findViewById(R.id.playerNewTeam)
            playerNewPic = view.findViewById(R.id.playerNewPic)

            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val player = data[bindingAdapterPosition]
            val ps = playerSelected(player)
            //IF IT IS ALREADY SELECTED
            if (ps.first) {
                selectedPlayers.removeAt(ps.second)
                notifyItemChanged(bindingAdapterPosition)
            } else {
                selectedPlayers.add(player)
                notifyItemChanged(bindingAdapterPosition)
            }
            Log.d("selectedPlayers",selectedPlayers.toString())
            listener.onItemClick(player)
        }
    }

    fun playerSelected(player: Standard): Pair<Boolean, Int> {
        var isSelected = false
        var index = -1
        for (i in selectedPlayers.indices) {
            if (selectedPlayers[i].personId == (player.personId)) {
                isSelected = true
                index = i
            }
        }
        return Pair(isSelected,index)
    }

    interface OnItemClickListener {
        fun onItemClick(player: Standard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.player_new_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val player = data[position]
        holder.playerNewName.text = getFullName(player)
        holder.playerNewTeam.text = teamMap[player.teamId.toString()]
        val idString = player.personId

        if (playerSelected(player).first) {
            holder.itemView.setBackgroundResource(R.color.highlight_blue)
            holder.playerNewPic.setImageResource(R.drawable.ic_baseline_check_64)
        } else {
            holder.itemView.setBackgroundResource(R.color.white)
            Glide.with(holder.itemView)
                .load("https://cdn.nba.com/headshots/nba/latest/260x190/${idString}.png")
                .into(holder.playerNewPic)
        }
    }

    companion object {
        fun getFullName(player: Standard): String {
            return "${player.firstName} ${player.lastName}"
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getFilter(): Filter {
        return filter
    }

    private val filter = object : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val filteredList = mutableListOf<Standard>()
            if (p0 == null || p0.isEmpty()) {
                filteredList.addAll(dataFull)
            } else {
                val filterPattern = p0.toString().lowercase().trim()
                for (player in dataFull) {
                    if (getFullName(player).lowercase().contains(filterPattern)) {
                        filteredList.add(player)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            data.clear()
            data.addAll(p1?.values as MutableList<Standard>)
            notifyDataSetChanged()
        }

    }
}
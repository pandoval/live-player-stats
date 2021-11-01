package com.example.liveplayerstats.playercomponents

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_table")
data class Player(@PrimaryKey val name: String, val id: String) {

}
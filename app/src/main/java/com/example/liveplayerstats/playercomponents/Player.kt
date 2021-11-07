package com.example.liveplayerstats.playercomponents

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_table")
data class Player(val name: String, @PrimaryKey val id: String, val teamName: String) {

}
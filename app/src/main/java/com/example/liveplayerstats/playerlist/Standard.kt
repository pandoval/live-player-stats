package com.example.liveplayerstats.playerlist

data class Standard(
    val collegeName: String,
    val country: String,
    val dateOfBirthUTC: String,
    val draft: Draft,
    val firstName: String,
    val heightFeet: String,
    val heightInches: String,
    val heightMeters: String,
    val isActive: Boolean,
    val isallStar: Boolean,
    val jersey: String,
    val lastAffiliation: String,
    val lastName: String,
    val nbaDebutYear: String,
    val personId: String,
    val pos: String,
    val teamId: String,
    val teamSitesOnly: TeamSitesOnly,
    val teams: List<Team>,
    val temporaryDisplayName: String,
    val weightKilograms: String,
    val weightPounds: String,
    val yearsPro: String
)
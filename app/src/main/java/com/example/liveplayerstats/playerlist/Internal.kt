package com.example.liveplayerstats.playerlist

data class Internal(
    val consolidatedDomKey: String,
    val endToEndTimeMillis: String,
    val igorPath: String,
    val pubDateTime: String,
    val xslt: String,
    val xsltCompileTimeMillis: String,
    val xsltForceRecompile: String,
    val xsltInCache: String,
    val xsltTransformTimeMillis: String
)
package edu.vt.smarttrail.db

//data class SurveyHistoryItem(
//    val userId: String,
//    val timestamp: String,
//    val weekNumber: Int,
//    val badgeAwarded: Boolean)

data class SurveyHistoryItem(
    val userId: String = "",
    val timestamp: String = "",
    val weekNumber: Int = 0,
    val badgeAwarded: Boolean = false
)


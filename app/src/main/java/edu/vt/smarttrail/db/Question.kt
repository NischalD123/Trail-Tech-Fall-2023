package edu.vt.smarttrail.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Question data object definition to represent a survey question that is stored in local database
 * to query for trail surveys
 */
@Entity(tableName = "question_table")
class Question(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "qtype") val qtype: Int,  // 0: MC, 1: Likert, 2: Free Response
    @ColumnInfo(name = "qsection") val qsection: Int,
    @ColumnInfo(name = "freqtype") val ftype: Int, // 0: always, 1: cycle
    @ColumnInfo(name = "qbody") val qbody: String,
    @ColumnInfo(name = "frespa") val frespa: String,
    @ColumnInfo(name = "sel") val selection: Int
)
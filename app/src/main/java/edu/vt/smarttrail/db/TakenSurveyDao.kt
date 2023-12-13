package edu.vt.smarttrail.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for TakenSurvey class
 */
@Dao
interface TakenSurveyDao {
    // Flow is an asyc sequence of values
    @Query("SELECT * FROM taken_survey_table")
    fun getAllTakenSurveys(): Flow<List<TakenSurvey>>
//    @Query("SELECT * FROM taken_survey_table WHERE uid = :userId")
//    fun getAllTakenSurveys(userId: String): Flow<List<TakenSurvey>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(new_survey: TakenSurvey): Long

    @Update
    fun updateTakenSurvey(vararg updatedSurvey: TakenSurvey): Int // returns int value indicating number of rows updated successfully

    @Query("DELETE FROM taken_survey_table")
    suspend fun deleteAll()
}

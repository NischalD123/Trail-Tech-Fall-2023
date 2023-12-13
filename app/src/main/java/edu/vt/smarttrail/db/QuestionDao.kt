package edu.vt.smarttrail.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data access object corresponding to Question class to provide necessary functionality with survey
 * question database
 */
@Dao
interface QuestionDao {
    // Flow is an asyc sequence of values
    @Query("SELECT * FROM question_table ORDER BY id ASC")
    fun getOrderedQuestions(): Flow<List<Question>>
//    @Query("SELECT * FROM question_table ORDER BY id ASC")
//    fun getOrderedQuestions(): List<Question>

    @Query("SELECT * FROM question_table WHERE qsection = :section_id ORDER BY id ASC")
    fun getOrderedQuestionSection(section_id: Int): Flow<List<Question>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(Question: Question)

    @Query("DELETE FROM Question_table")
    suspend fun deleteAll()
}

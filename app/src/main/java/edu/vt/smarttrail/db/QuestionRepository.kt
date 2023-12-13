package edu.vt.smarttrail.db

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

/**
 * Question repository manages queries and allows use of multiple backends
 * Can implement logic for deciding whether to fetch data from network or use local database cache
 * Note: DAO is passed into repository constructor rather than the entire database
 */
// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class QuestionRepository(private val questionDao: QuestionDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.

    val s1Questions: Flow<List<Question>> = questionDao.getOrderedQuestionSection(1)
    val s2Questions: Flow<List<Question>> = questionDao.getOrderedQuestionSection(2)
    val s3Questions: Flow<List<Question>> = questionDao.getOrderedQuestionSection(3)
    val s4Questions: Flow<List<Question>> = questionDao.getOrderedQuestionSection(4)
    val s5Questions: Flow<List<Question>> = questionDao.getOrderedQuestionSection(5)
    val s6Questions: Flow<List<Question>> = questionDao.getOrderedQuestionSection(6)
    val s7Questions: Flow<List<Question>> = questionDao.getOrderedQuestionSection(7)
    val s8Questions: Flow<List<Question>> = questionDao.getOrderedQuestionSection(8)
    val s9Questions: Flow<List<Question>> = questionDao.getOrderedQuestionSection(9)

    /**
     * Inserts a new question into the database
     */
    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(question: Question) {
        questionDao.insert(question)
    }
}

package edu.vt.smarttrail.db

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

/**
 * TakenSurvey repository manages queries to database
 */
class TakenSurveyRepository(private val surveyDao: TakenSurveyDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allSurveys: Flow<List<TakenSurvey>> = surveyDao.getAllTakenSurveys()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(tsurvey: TakenSurvey): Long {
        return surveyDao.insert(tsurvey)
    }

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
//    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun update(tsurvey: TakenSurvey) {
        surveyDao.updateTakenSurvey(tsurvey)
    }
}
package edu.vt.smarttrail

import android.app.Application
import edu.vt.smarttrail.db.QuestionRepository
import edu.vt.smarttrail.db.QuestionRoomDatabase
import edu.vt.smarttrail.db.TakenSurveyRepository
import edu.vt.smarttrail.db.TakenSurveyRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/**
 * Used to only have one instance of the database & repository in app
 */
class QuestionsApplication : Application() {
    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using "by lazy" so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { QuestionRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { QuestionRepository(database.questionDao()) }
    val ts_database by lazy { TakenSurveyRoomDatabase.getDatabase(this, applicationScope) }
    val ts_repository by lazy { TakenSurveyRepository(ts_database.takenSurveyDao()) }

}
package edu.vt.smarttrail.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Room database storing survey responses
 */
// Annotates class to be a Room Database with a table (entity) of the TakenSurvey class
@Database(entities = arrayOf(TakenSurvey::class), version = 1, exportSchema = false)
abstract class TakenSurveyRoomDatabase : RoomDatabase() {

    abstract fun takenSurveyDao(): TakenSurveyDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: TakenSurveyRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): TakenSurveyRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TakenSurveyRoomDatabase::class.java,
                    "taken_survey_database"
                ).addCallback(TakenSurveyDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class TakenSurveyDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateDatabase(database.takenSurveyDao())
                    }
                }
            }

            suspend fun populateDatabase(takenSurveyDao: TakenSurveyDao) {
                // Delete all content here.
                takenSurveyDao.deleteAll()

                // Add takenSurveys
//                Section 1
//                val utilDate: java.util.Date = Date()
//                val sqlCurrDate = java.sql.Date(utilDate.time)
//                var takenSurvey = TakenSurvey(0, sqlCurrDate.toString(), "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
//                takenSurveyDao.insert(takenSurvey)
            }
        }
    }
}

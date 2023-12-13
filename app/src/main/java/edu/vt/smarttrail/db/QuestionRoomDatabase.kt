package edu.vt.smarttrail.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Room database storing survey questions
 */
// Annotates class to be a Room Database with a table (entity) of the Question class
@Database(entities = [Question::class], version = 1, exportSchema = false)
abstract class QuestionRoomDatabase : RoomDatabase() {

    abstract fun questionDao(): QuestionDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: QuestionRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): QuestionRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuestionRoomDatabase::class.java,
                    "question_database"
                ).addCallback(QuestionDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        /**
         * Callback class for creation/population of database
         */
        private class QuestionDatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {

            /**
             * Populates db with questions on creation
             */
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateDatabase(database.questionDao())
                    }
                }
            }
            /// ***** Morva Added the Questions *****
            /**
             * Populates question database with the relevant questions for each section
             */
            suspend fun populateDatabase(questionDao: QuestionDao) {
//                // Delete all content here.
                questionDao.deleteAll()
                //Add Questions:
                //Section 1
                var question = Question(100, 1,1, 0,  "How experienced would you consider yourself as a long distance hiker before your current hike?", "", -1)
                questionDao.insert(question)

                // Section2 (hardcoded questions)
                // Questions 6/8/9/10/11/12/13 should be hardcoded!!!!!!!!!!!!!!!


                /*
                Question type values: (qtype)
                1: Experience level
                2: Likelihood
                3: Desirability
                4: Agree
                5: benefits
                6: Frequency (never to always)
                7: risk
                8: quantity
                 */

                //Section 2 (Agree/Disagree)
                question = Question(201, 4, 2, 0, "I am reserved", "", -1)
                questionDao.insert(question)
                question = Question(202, 4, 2, 0, "I am generally trusting", "", -1)
                questionDao.insert(question)
                question = Question(203, 4, 2, 0, "I tend to be lazy", "", -1)
                questionDao.insert(question)
                question = Question(204, 4, 2, 0, "I am relaxed, handle stress well", "", -1)
                questionDao.insert(question)
                question = Question(205, 4, 2, 0, "I have few artistic interests", "", -1)
                questionDao.insert(question)
                question = Question(206, 4, 2, 0, "I am outgoing, sociable", "", -1)
                questionDao.insert(question)
                question = Question(207, 4, 2, 0, "I tend to find fault with others", "", -1)
                questionDao.insert(question)
                question = Question(208, 4, 2, 0, "I do a thorough job", "", -1)
                questionDao.insert(question)
                question = Question(209, 4, 2, 0, "I get nervous easily", "", -1)
                questionDao.insert(question)
                question = Question(210, 4, 2, 0, "I have an active imagination", "", -1)
                questionDao.insert(question)

                //Section 3 (desirability)
                question = Question(300, 3, 3, 0,"To think about who I am", "", -1)
                questionDao.insert(question)
                question = Question(301, 3, 3, 0,"To learn more about things on the trail", "", -1)
                questionDao.insert(question)
                question = Question(302, 3, 3, 0,"To be close to nature", "", -1)
                questionDao.insert(question)
                question = Question(303, 3, 3, 0,"To be with friends", "", -1)
                questionDao.insert(question)
                question = Question(304, 3, 3, 0,"To understand things on the trail better", "", -1)
                questionDao.insert(question)
                question = Question(305, 3, 3, 0,"To be where it is quiet", "", -1)
                questionDao.insert(question)
                question = Question(306, 3, 3, 0,"To help me better understand what my life is all about", "", -1)
                questionDao.insert(question)
                question = Question(307, 3, 3, 0,"To enjoy the smells and sounds of nature", "", -1)
                questionDao.insert(question)
                question = Question(308, 3, 3, 0,"To reflect on personal religious values", "", -1)
                questionDao.insert(question)
                question = Question(309, 3, 3, 0,"To share what I have learned with other", "", -1)
                questionDao.insert(question)
                question = Question(310, 3, 3, 0,"To help others learn about history here", "", -1)
                questionDao.insert(question)
                question = Question(311, 3, 3, 0,"To teach others about history here", "", -1)
                questionDao.insert(question)
                question = Question(312, 3, 3, 0,"To develop spiritual values", "", -1)
                questionDao.insert(question)
                question = Question(313, 3, 3, 0,"To  experience the sense of discovery involved", "", -1)
                questionDao.insert(question)
                question = Question(314, 3, 3, 0,"To develop knowledge of things on the trail", "", -1)
                questionDao.insert(question)
                question = Question(315, 3, 3, 0,"To be with members of my trail group", "", -1)
                questionDao.insert(question)
                question = Question(316, 3, 3, 0,"To experience solitude", "", -1)
                questionDao.insert(question)

                // Section 4
                question = Question(400, 2, 4, 0, "Consuming 5 or more servings of alcohol in a single evening", "", -1)
                questionDao.insert(question)
                question = Question(401, 2, 4, 0, "Wading across a stream with fast moving water at waist level.", "", -1)
                questionDao.insert(question)
                question = Question(402, 2, 4, 0, "Periodically drinking untreated and unfiltered water from an unprotected water source.", "", -1)
                questionDao.insert(question)
                question = Question(403, 2, 4, 0, "Going off the trail without a navigation app or hardcopy map.", "", -1)
                questionDao.insert(question)
                question = Question(404, 2, 4, 0, "Going down a steep, rocky slope that is beyond your ability.", "", -1)
                questionDao.insert(question)
                question = Question(405, 2, 4, 0, "Hiking for 3 hours or more in the dark.", "", -1)
                questionDao.insert(question)
                question = Question(406, 2, 4, 0, "Taking a 5 mile hike off the trail (bushwhacking) to take a shortcut to a road.", "", -1)
                questionDao.insert(question)
                question = Question(407, 2, 4, 0, "Walking home alone at night in a somewhat unsafe area of town", "", -1)
                questionDao.insert(question)
                question = Question(408, 2, 4, 0, "Following a bear on the trail to take a dramatic photo.", "", -1)
                questionDao.insert(question)
                question = Question(409, 2, 4, 0, "Regularly eating high cholesterol foods", "", -1)
                questionDao.insert(question)
                question = Question(410, 2, 4, 0, "Not wearing a seatbelt when being a passenger in the front seat", "", -1)
                questionDao.insert(question)
                question = Question(411, 2, 4, 0, "Not wearing a helmet when riding a motorcycle", "", -1)
                questionDao.insert(question)
                question = Question(412, 2, 4, 0, "Exposing yourself to the sun without using sunscreen", "", -1)
                questionDao.insert(question)
                question = Question(413, 2, 4, 0, "Engaging in unprotected sex", "", -1)
                questionDao.insert(question)
                question = Question(414, 2, 4, 0, "Going more than a mile off the trail for camping in the woods.", "", -1)
                questionDao.insert(question)
                question = Question(415, 2, 4, 0, "Buying an illegal drug for your own use", "", -1)
                questionDao.insert(question)

// Section 5
                question = Question(500, 5, 5, 0, "Consuming 5 or more servings of alcohol in a single evening", "", -1)
                questionDao.insert(question)
                question = Question(501, 5, 5, 0, "Wading across a stream with fast moving water at waist level.", "", -1)
                questionDao.insert(question)
                question = Question(502, 5, 5, 0, "Periodically drinking untreated and unfiltered water from an unprotected water source.", "", -1)
                questionDao.insert(question)
                question = Question(503, 5, 5, 0, "Going off the trail without a navigation app or hardcopy map.", "", -1)
                questionDao.insert(question)
                question = Question(504, 5, 5, 0, "Going down a steep, rocky slope that is beyond your ability.", "", -1)
                questionDao.insert(question)
                question = Question(505, 5, 5, 0, "Hiking for 3 hours or more in the dark.", "", -1)
                questionDao.insert(question)
                question = Question(506, 5, 5, 0, "Taking a 5 mile hike off the trail (bushwhacking) to take a shortcut to a road.", "", -1)
                questionDao.insert(question)
                question = Question(507, 5, 5, 0, "Walking home alone at night in a somewhat unsafe area of town", "", -1)
                questionDao.insert(question)
                question = Question(508, 5, 5, 0, "Following a bear on the trail to take a dramatic photo.", "", -1)
                questionDao.insert(question)
                question = Question(509, 5, 5, 0, "Regularly eating high cholesterol foods", "", -1)
                questionDao.insert(question)
                question = Question(510, 5, 5, 0, "Not wearing a seatbelt when being a passenger in the front seat", "", -1)
                questionDao.insert(question)
                question = Question(511, 5, 5, 0, "Not wearing a helmet when riding a motorcycle", "", -1)
                questionDao.insert(question)
                question = Question(512, 5, 5, 0, "Exposing yourself to the sun without using sunscreen", "", -1)
                questionDao.insert(question)
                question = Question(513, 5, 5, 0, "Engaging in unprotected sex", "", -1)
                questionDao.insert(question)
                question = Question(514, 5, 5, 0, "Going more than a mile off the trail for camping in the woods.", "", -1)
                questionDao.insert(question)
                question = Question(515, 5, 5, 0, "Buying an illegal drug for your own use", "", -1)
                questionDao.insert(question)

// Section 6
                question = Question(600, 7, 6, 0, "Consuming 5 or more servings of alcohol in a single evening", "", -1)
                questionDao.insert(question)
                question = Question(601, 7, 6, 0, "Wading across a stream with fast moving water at waist level.", "", -1)
                questionDao.insert(question)
                question = Question(602, 7, 6, 0, "Periodically drinking untreated and unfiltered water from an unprotected water source.", "", -1)
                questionDao.insert(question)
                question = Question(603, 7, 6, 0, "Going off the trail without a navigation app or hardcopy map.", "", -1)
                questionDao.insert(question)
                question = Question(604, 7, 6, 0, "Going down a steep, rocky slope that is beyond your ability.", "", -1)
                questionDao.insert(question)
                question = Question(605, 7, 6, 0, "Hiking for 3 hours or more in the dark.", "", -1)
                questionDao.insert(question)
                question = Question(606, 7, 6, 0, "Taking a 5 mile hike off the trail (bushwhacking) to take a shortcut to a road.", "", -1)
                questionDao.insert(question)
                question = Question(607, 7, 6, 0, "Walking home alone at night in a somewhat unsafe area of town", "", -1)
                questionDao.insert(question)
                question = Question(608, 7, 6, 0, "Following a bear on the trail to take a dramatic photo.", "", -1)
                questionDao.insert(question)
                question = Question(609, 7, 6, 0, "Regularly eating high cholesterol foods", "", -1)
                questionDao.insert(question)
                question = Question(610, 7, 6, 0, "Not wearing a seatbelt when being a passenger in the front seat", "", -1)
                questionDao.insert(question)
                question = Question(611, 7, 6, 0, "Not wearing a helmet when riding a motorcycle", "", -1)
                questionDao.insert(question)
                question = Question(612, 7, 6, 0, "Exposing yourself to the sun without using sunscreen", "", -1)
                questionDao.insert(question)
                question = Question(613, 7, 6, 0, "Engaging in unprotected sex", "", -1)
                questionDao.insert(question)
                question = Question(614, 7, 6, 0, "Going more than a mile off the trail for camping in the woods.", "", -1)
                questionDao.insert(question)
                question = Question(615, 7, 6, 0, "Buying an illegal drug for your own use", "", -1)
                questionDao.insert(question)



                //Questions 18 & 19 should be hardcoded!!!!!!!!!!!!!!!!!!!!!

                //Section 7 (Never/Always)
                question = Question(701, 6, 7, 0,"Happy", "", -1)
                questionDao.insert(question)
                question = Question(702, 6, 7, 0,"Anxious", "", -1)
                questionDao.insert(question)
                question = Question(703, 6, 7, 0,"Distressed", "", -1)
                questionDao.insert(question)
                question = Question(704, 6, 7, 0,"Enthusiastic", "", -1)
                questionDao.insert(question)
                question = Question(705, 6, 7, 0,"Nervous", "", -1)
                questionDao.insert(question)
                question = Question(706, 6, 7, 0,"Excited", "", -1)
                questionDao.insert(question)
                question = Question(707, 6, 7, 0,"Upset", "", -1)
                questionDao.insert(question)
                question = Question(708, 6, 7, 0,"Strong", "", -1)
                questionDao.insert(question)
                question = Question(709, 6, 7, 0,"Irritable", "", -1)
                questionDao.insert(question)
                question = Question(710, 6, 7, 0,"Active", "", -1)
                questionDao.insert(question)
                question = Question(711, 6, 7, 0,"Scared", "", -1)
                questionDao.insert(question)

                //Section 8 (Agree/Disagree)
                question = Question(801, 4, 8, 0,"I feel a part of wild nature", "", -1)
                questionDao.insert(question)
                question = Question(802, 4, 8, 0,"I care what time it is", "", -1)
                questionDao.insert(question)
                question = Question(803, 4, 8, 0,"I feel like I’m living like a pioneer", "", -1)
                questionDao.insert(question)
                question = Question(804, 4, 8, 0,"I feel the simplicity of life on this hike", "", -1)
                questionDao.insert(question)
                question = Question(805, 4, 8, 0,"I feel a special closeness with nature", "", -1)
                questionDao.insert(question)
                question = Question(806, 4, 8, 0,"I care what time it is when I eat", "", -1)
                questionDao.insert(question)
                question = Question(807, 4, 8, 0,"I feel the simplicity of life on this hike", "", -1)
                questionDao.insert(question)
                question = Question(808, 4, 8, 0,"The environment seems free of human-made noise", "", -1)
                questionDao.insert(question)
                question = Question(809, 4, 8, 0,"I want to behave properly toward this placeplade", "", -1)
                questionDao.insert(question)
                question = Question(810, 4, 8, 0,"I am in awe of nature’s creation", "", -1)
                questionDao.insert(question)
                question = Question(811, 4, 8, 0,"I feel connected with times long ago", "", -1)
                questionDao.insert(question)
                question = Question(812, 4, 8, 0,"Enthusiastic", "", -1)
                questionDao.insert(question)
                question = Question(813, 4, 8, 0,"I am feeling insignificant in the glory of nature", "", -1)
                questionDao.insert(question)
                question = Question(814, 4, 8, 0,"I feel I want to care for this place", "", -1)
                questionDao.insert(question)
                question = Question(815, 4, 8, 0,"I feel the tranquility and peacefulness of this place", "", -1)
                questionDao.insert(question)
                question = Question(816, 4, 8, 0,"I am feeling the heartbeat of the earth", "", -1)
                questionDao.insert(question)
                question = Question(817, 4, 8, 0,"I feel that life is simple", "", -1)
                questionDao.insert(question)
                question = Question(818, 4, 8, 0,"I feel the silence of the environment", "", -1)
                questionDao.insert(question)
                question = Question(819, 4, 8, 0,"I feel humbled by all of the nature around me", "", -1)
                questionDao.insert(question)

                //Section 9
                question = Question(900, 8,9, 0,  "Please indicate approximately how many different hikers you saw yesterday on the trail (e.g., hikers you hiked with, hikers passed you, hikers with whom you camped/sheltered/hosteled/shuttled, etc.).", "", -1)
                questionDao.insert(question)
                question = Question(901, 8,9, 0,  "Please indicate approximately how many different hikers you talked to yesterday in a face-to-face conversation.(By face-to-face conversation, we mean an in-person conversation lasting 30 seconds or more.)", "", -1)
                questionDao.insert(question)
                question = Question(902, 8,9, 0,  "Please indicate approximately how many different hikers you you communicated with via your phone yesterday (e.g., text messages, phone conversations, etc.).", "", -1)
                questionDao.insert(question)

                // Questions 26,27,28,29,30,31 should be hardcoded!!!
            }
        }

    }
}
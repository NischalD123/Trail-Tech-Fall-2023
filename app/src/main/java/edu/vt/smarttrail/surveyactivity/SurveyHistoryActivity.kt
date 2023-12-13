package edu.vt.smarttrail.surveyactivity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.vt.smarttrail.DBHelper
import edu.vt.smarttrail.HomeActivity
import edu.vt.smarttrail.R
import edu.vt.smarttrail.SurveyHistoryAdapter
import edu.vt.smarttrail.UidSingleton
import edu.vt.smarttrail.db.SurveyHistoryItem

class SurveyHistoryActivity : AppCompatActivity() {

    private lateinit var surveyHistoryAdapter: SurveyHistoryAdapter
    val surveyHistoryList = mutableListOf<SurveyHistoryItem>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_history)

        val rvSurveyHistory: RecyclerView = findViewById(R.id.rvSurveyHistory)
        val firebaseCloudSurveyHistory = FirebaseDatabase.getInstance().getReference("history")

        // Initialize RecyclerView and Adapter
        surveyHistoryAdapter = SurveyHistoryAdapter(this, surveyHistoryList)
        rvSurveyHistory.adapter = surveyHistoryAdapter
        rvSurveyHistory.layoutManager = LinearLayoutManager(this)
        surveyHistoryAdapter.notifyDataSetChanged()

        firebaseCloudSurveyHistory.orderByChild("userId").equalTo(UidSingleton.getUid()).addListenerForSingleValueEvent(object :
            ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val history = snapshot.getValue(SurveyHistoryItem::class.java)
                    history?.let { surveyHistoryList.add(it) }
                }
                if (surveyHistoryList.size < 1){
                    Toast.makeText(this@SurveyHistoryActivity, "Survey History Data is not Found", Toast.LENGTH_SHORT).show()
                }
                surveyHistoryAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
                // You might want to add error handling here based on your application needs
            }
        })

    }
//
//    private fun getSampleSurveyHistory(): List<SurveyHistoryItem> {
//        val historyList = mutableListOf<SurveyHistoryItem>()
//
//        // Add sample data (replace this with your actual survey history data)
//        historyList.add(SurveyHistoryItem("2023-12-01", true)) // Survey completed
//        historyList.add(SurveyHistoryItem("2023-12-05", false)) // Survey not completed
//        historyList.add(SurveyHistoryItem("2023-12-10", true)) // Survey completed
//
//        return historyList
//    }

}
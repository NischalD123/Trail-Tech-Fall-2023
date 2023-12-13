package edu.vt.smarttrail.surveyactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.vt.smarttrail.R

/**
 * Activity showing trail survey to users; utilizing fragments for different pages
 */
class SurveyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)
        // Creates an action bar, where back button is.
        val actionBar = supportActionBar
        // Disables back button.
        actionBar!!.setDisplayHomeAsUpEnabled(false)
    }
    companion object {
        const val TAG: String = "SurveyActivity.kt"
    }
}
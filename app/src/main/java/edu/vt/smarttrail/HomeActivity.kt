package edu.vt.smarttrail

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.vt.smarttrail.db.SurveyHistoryItem
import edu.vt.smarttrail.db.TakenSurvey
import edu.vt.smarttrail.db.TakenSurveyViewModel
import edu.vt.smarttrail.db.TakenSurveyViewModelFactory
import edu.vt.smarttrail.notifications.channelID
import edu.vt.smarttrail.surveyactivity.FinishFragment
import edu.vt.smarttrail.surveyactivity.SurveyActivity
import edu.vt.smarttrail.surveyactivity.SurveyHistoryActivity
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class HomeActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var navView: NavigationView
    // Database Reference for writing.
    private val firebaseCloud = FirebaseDatabase.getInstance().getReference("users")
    private var lastSurveyWeekType: Int = 0
    val surveyHistoryList = mutableListOf<SurveyHistoryItem>()
    val badgeList = mutableListOf<SurveyHistoryItem>()
    lateinit var noBadgeAwarded : TextView
    lateinit var tvCurrentWeek : TextView
    lateinit var rlAwarded : RelativeLayout
    lateinit var ivBadge : ImageView


    private val takenSurveyViewModel: TakenSurveyViewModel by viewModels {
        TakenSurveyViewModelFactory((this.application as QuestionsApplication).ts_repository)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        lifecycleRegistry = LifecycleRegistry(this)
//        lifecycleRegistry.markState(Lifecycle.State.CREATED)
        setContentView(R.layout.activity_home)

        createNotificationChannel()
        val lastSurveyTv = findViewById<TextView>(R.id.last_survey_tv)
        noBadgeAwarded = findViewById(R.id.noBadgeAwarded)
        rlAwarded = findViewById(R.id.rlAwarded)
        tvCurrentWeek = findViewById(R.id.tvCurrentWeek)
        ivBadge = findViewById(R.id.ivBadge)

        //Sets up Hamburger menu
        drawerLayout = findViewById(R.id.drawer_layout)
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        navView = findViewById(R.id.nav_view)

        // Makes menu toggleable
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // Set up Action Bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


//        TODO: modify db files to only query most recent survey
        this.takenSurveyViewModel.allTakenSurveys.observe(this, Observer { surveys ->
            var tknSurveys = surveys as ArrayList<TakenSurvey>
            // TOOO: properly get truly most recent survey
            if (tknSurveys.size > 0) {
                val lastTakenDate = tknSurveys[tknSurveys.size - 1].datetime
                lastSurveyWeekType = tknSurveys[tknSurveys.size - 1].weektype
                lastSurveyTv.text = "LAST SURVEY: ${lastTakenDate}"
            }
        })

        val extras = intent.extras
        var primaryKey:String? = "NotFound"
        if (extras != null) {
            primaryKey = extras.getString("primary_key")
            val login_db = DBHelper(this)
            val user = login_db.getUser(primaryKey)
            val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
            tvWelcome.text = "WELCOME ${user.username}!"

        }
        // Uploads Appstats to cloud
        if (usagePermission()){
            recordAppStats()
        }


        // Set Up Hamburger Menu
        navView.setNavigationItemSelectedListener {
            setUpNavMenu(it)
        }

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        // Load the value of the boolean flag from SharedPreferences
        val isSurveyButtonClicked = sharedPreferences.getBoolean("isSurveyButtonClicked", false)


        // Survey Button Set up
        val survey = findViewById<View>(R.id.button_Survey) as Button

// Set the onClickListener for the survey button
        survey.setOnClickListener {
            if (!isSurveyButtonClicked) {
                val intent = Intent(this, PermissionsActivity::class.java)
                startActivity(intent)
                // Update the boolean flag in SharedPreferences to true
                sharedPreferences.edit().putBoolean("isSurveyButtonClicked", true).apply()
            } else {
                openSurveyActivity()
            }
        }
    }

    private fun createNotificationChannel()
    {
        val name = "Notif Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelID, name, importance)
            channel.description = desc
            val notificationManager = getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        else {
//            TODO("VERSION.SDK_INT < O")
            Log.e(FinishFragment.TAG, "notification channel creation failed")
        }

    }

    // Listener for Hamburger menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    // Set up Hamburger Menu
    private fun setUpNavMenu(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_GPS -> {
                openMapsActivity()
            }
            R.id.nav_Screentime -> {
                openScreentimeActivity()
            }
            R.id.locations_permission -> {
                openLocationPermissionsActivity()
            }
            R.id.screentime_permission -> {
                openScreentimePermissionsActivity()
            }
            R.id.change_password -> {
                openChangePasswordActivity()
            }
            R.id.survey_history -> {
                openHistoryActivity()
            }
            R.id.Logout -> {
                val sharedPre = getSharedPreferences("autoLogin", Context.MODE_PRIVATE)
                sharedPre.edit().putString("key", null).apply()
                openMainActivity()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun openHistoryActivity() {

        val extras = intent.extras
        var key:String? = null
        var userID:String? = "NotFound"
        var password:String? = "NotFound"
        var email:String? = "NotFound"
        if (extras != null) {
            key = extras.getString("primary_key")
            userID = extras.getString("userID")
            password = extras.getString("password")
            email = extras.getString("email")
        }

        val intent = Intent(this, SurveyHistoryActivity::class.java)
        intent.putExtra("primary_key", key)
        intent.putExtra("userID", userID)
        intent.putExtra("password", password)
        intent.putExtra("email", email)

        startActivity(intent)
    }

    // Instructions to send to GPS
    private fun openMapsActivity() {
        val extras = intent.extras
        var userID:String? = "NotFound"
        var primaryKey:String? = "NotFound"
        if (extras != null) {
            userID = extras.getString("userID")
            primaryKey = extras.getString("primary_key")
        }
        val intent = Intent(this, GMapsActivity::class.java)
        intent.putExtra("userID", userID)
        intent.putExtra("primary_key", primaryKey)
        startActivity(intent)
    }

    // Instructions to send to Screen time
    private fun openScreentimeActivity() {
        val extras = intent.extras
        var key:String? = null
        var userID:String? = "NotFound"
        var password:String? = "NotFound"
        var email:String? = "NotFound"
        if (extras != null) {
            key = extras.getString("primary_key")
            userID = extras.getString("userID")
            password = extras.getString("password")
            email = extras.getString("email")
        }
        val intent = Intent(this, ScreentimeActivity::class.java)
        intent.putExtra("primary_key", key)
        intent.putExtra("userID", userID)
        intent.putExtra("password", password)
        intent.putExtra("email", email)
        startActivity(intent)
    }

    // Instructions to send to Survey
    private fun openSurveyActivity() {
        val extras = intent.extras
        var userID:String? = "NotFound"
        var userKey:String? = "NotFound"
        if (extras != null) {
            userID = extras.getString("userID")
            userKey = extras.getString("primary_key")
        }
        val intent = Intent(this, SurveyActivity::class.java)
        intent.putExtra("userID", userID)
        intent.putExtra("primary_key", userKey)
        var cwt = lastSurveyWeekType + 1
        if(cwt >= 15){
            cwt = 1
        }
        Log.d("QuestionFragment.kt", "currweektype: ${cwt}")
        intent.putExtra("currweektype", "${cwt}")
        startActivity(intent)
//        finish()
    }

    // Instructions to send to Permissions
    private fun openLocationPermissionsActivity() {
        val intent = Intent(this, edu.vt.smarttrail.LocationPermissionActivity::class.java)
        startActivity(intent)
    }

    private fun openScreentimePermissionsActivity() {
        val intent = Intent(this, edu.vt.smarttrail.ScreentimePermissionActivity::class.java)
        startActivity(intent)
    }

    private fun openMainActivity() {
        val intent = Intent(this, edu.vt.smarttrail.MainActivity::class.java)
        startActivity(intent)
    }

    private fun openChangePasswordActivity() {

        val extras = intent.extras
        var key:String? = null
        var userID:String? = "NotFound"
        var password:String? = "NotFound"
        var email:String? = "NotFound"
        if (extras != null) {
            key = extras.getString("primary_key")
            userID = extras.getString("userID")
            password = extras.getString("password")
            email = extras.getString("email")
        }

        val intent = Intent(this, ChangePasswordActivity::class.java)
        intent.putExtra("primary_key", key)
        intent.putExtra("userID", userID)
        intent.putExtra("password", password)
        intent.putExtra("email", email)

        startActivity(intent)
    }

    private fun recordAppStats() {
        val extras = intent.extras
        var key:String? = null
        if (extras != null) {
            key = extras.getString("primary_key")
            Log.e("UN", key!!)
        }

        Log.e("CHK", "Running")
        val appstats_db = AppStatsDBHelper(this)
        if (key != null && appstats_db.checkAppStatsExist(key)) {
            val appStatsList = appstats_db.getAppStats(key)
            for (appstats in appStatsList) {
                var usageStatsSurvey = ""
                // Creates a list storing all app usage in the specified interval
                val format = SimpleDateFormat("yyyy-MM-dd")
                val takenDate = format.parse(appstats.date) as Date

                // Creates the format reader for the calender day.
                val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())

                var daysPassed = Date(takenDate.time)
                Log.e("SIN", "daysPassed: $daysPassed")
                var days = 0

                while (daysPassed.time <= System.currentTimeMillis()) {
                    val dayName = dayFormat.format(daysPassed)
                    Log.e("SIN", dayName)

                    val appStatsLastSurvey = createUsageStatsFromLastTaken(daysPassed, days)
                    if (appStatsLastSurvey == null) {
                        usageStatsSurvey = "$usageStatsSurvey${dayName.uppercase()}: NONE "
                    }
                    else {
                        val usageListLastSurvey = ArrayList(appStatsLastSurvey!!.values)
                        val lastSurveyString = recordUsageList(usageListLastSurvey)
                        usageStatsSurvey = "$usageStatsSurvey${dayName.uppercase()}: $lastSurveyString"
                        Log.e("SIN", usageStatsSurvey)
                    }

                    daysPassed = Date(daysPassed.time + 1000 * 3600 * 24)
                    days++

                    if (days == 7) {
                        break
                    }

                }

                //SQLite DB
                val login_db = DBHelper(this)
                val user = login_db.getUser(key)
                login_db.insertUsageStats(key, user.username, user.password, user.email, usageStatsSurvey)
                firebaseCloud.child(key).child("usageStats").child(appstats.pushKey).setValue(usageStatsSurvey)
            }

        }


    }

    // Records a usagestats list.
    private fun recordUsageList(usageList: List<UsageStats>): String {
        var usageStatsString = ""
        for (usageStats in usageList) {
            try {
                val packageName = usageStats.packageName
                val packageNames = packageName.split("\\")
                var appName = packageNames[packageNames.size - 1].trim { it <= ' ' }

                if (isAppInfoAvailable(usageStats)) {
                    val ai = applicationContext.packageManager.getApplicationInfo(packageName, 0)
                    appName = applicationContext.packageManager.getApplicationLabel(ai).toString()
                }
                var usageDuration = getDurationString(usageStats.totalTimeInForeground)
                usageStatsString = "$usageStatsString$appName, $usageDuration; "
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
        return usageStatsString
    }

    // Gets the duration of app foreground time in hh:mm:ss format.
    private fun getDurationString(millis: Long): String {
        var millis = millis
        require(millis >= 0) { "Duration must be greater than zero!" }
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        millis -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        millis -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
        return "$hours h $minutes m $seconds s"
    }

    // Checks if App information is available
    private fun isAppInfoAvailable(usageStats: UsageStats): Boolean {
        return try {
            applicationContext.packageManager.getApplicationInfo(usageStats.packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    // Creates usages stats from last taken survey
    private fun createUsageStatsFromLastTaken(takenDate: Date, dayNeeded: Int): SortedMap<Long, UsageStats>? {
        val dayTimespan = 1000 * 3600 * 24 + 1000

        Log.e("SIN", takenDate.toString())
        Log.e("SIN", Date(takenDate.time + dayTimespan).toString())

        val usm = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val appList = usm.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            // Begin Time: A amount of days (dayNeeded) after the survey.
            // End Time: A Full day after the survey.
            takenDate.time, takenDate.time + dayTimespan)
        Log.e("SIN", appList.size.toString())
        // Sorts the list by app usage
        if (appList != null && appList.size > 0) {
            var mySortedMap: SortedMap<Long, UsageStats> = TreeMap()
            for (usageStats in appList) {
                mySortedMap[usageStats.totalTimeInForeground] = usageStats
            }
            return mySortedMap
        }
        return null
    }

    // Gets usage permission status
    private fun usagePermission(): Boolean {
        var appOps: AppOpsManager? = null
        var mode: Int = 0
        appOps = getSystemService(APP_OPS_SERVICE)!! as AppOpsManager
        mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), applicationContext.packageName)
        return mode == AppOpsManager.MODE_ALLOWED
    }

    public fun exitApp() {
        finishAndRemoveTask()
    }

    override fun onResume() {
        super.onResume()
        getCurrentWeekBudget()
    }

    fun getCurrentWeekBudget(){
        val calendar = Calendar.getInstance()
        val currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val currentWeekOfMonth = (currentDayOfMonth - 1) / 7 + 1
        val firebaseCloudSurveyHistory = FirebaseDatabase.getInstance().getReference("history")
        firebaseCloudSurveyHistory.orderByChild("userId").equalTo(UidSingleton.getUid()).addListenerForSingleValueEvent(object :
            ValueEventListener {
            @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val history = snapshot.getValue(SurveyHistoryItem::class.java)
                    history?.let { surveyHistoryList.add(it) }
                }
                if (surveyHistoryList.size < 1){
                    noBadgeAwarded.visibility = View.VISIBLE
                    rlAwarded.visibility = View.GONE
                }else{
                    noBadgeAwarded.visibility = View.VISIBLE
                    rlAwarded.visibility = View.GONE
                    surveyHistoryList.forEach{
                        if (it.badgeAwarded && currentWeekOfMonth == it.weekNumber){
                            tvCurrentWeek.text = "Week Number: "+it.weekNumber+"  "
                            when(it.weekNumber){
                                1->{
                                    ivBadge.setColorFilter(getColor(R.color.purple))
                                    tvCurrentWeek.setTextColor(getColor(R.color.purple))
                                }
                                2->{
                                    ivBadge.setColorFilter(getColor(R.color.yellow))
                                    tvCurrentWeek.setTextColor(getColor(R.color.yellow))
                                }
                                3->{
                                    ivBadge.setColorFilter(getColor(R.color.btn_green))
                                    tvCurrentWeek.setTextColor(getColor(R.color.btn_green))
                                }
                                else-> {
                                    ivBadge.setColorFilter(getColor(R.color.purple_700))
                                    tvCurrentWeek.setTextColor(getColor(R.color.purple_700))
                                }
                            }
                            noBadgeAwarded.visibility = View.GONE
                            rlAwarded.visibility = View.VISIBLE
                            return@forEach
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
                // You might want to add error handling here based on your application needs
            }
        })
    }

}
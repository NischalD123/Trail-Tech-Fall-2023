package edu.vt.smarttrail


import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Process
import android.widget.ListView

import androidx.appcompat.app.AppCompatActivity
import java.util.*
import java.util.concurrent.TimeUnit

class ScreentimeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screentime)

        // Creates an action bar, where back button is.
        val actionBar = supportActionBar

        // Creates back button.
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        // Creates a usage stat list if given permission.
        if (usagePermission()) {
            val appStats = createUsageStats(UsageStatsManager.INTERVAL_MONTHLY)
            appStats?.let {showAppStats(appStats)}
        }
        else {
            val intent = Intent(this, edu.vt.smarttrail.ScreentimePermissionActivity::class.java)
            startActivity(intent)
        }
    }

    // Get usage stats permission
    private fun usagePermission(): Boolean {
        var appOps: AppOpsManager? = null
        var mode: Int = 0
        appOps = getSystemService(APP_OPS_SERVICE)!! as AppOpsManager
        mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), applicationContext.packageName)
        return mode == AppOpsManager.MODE_ALLOWED
    }

    //Creates usages stats
    private fun createUsageStats(interval: Int): SortedMap<Long, UsageStats>? {
        // Creates a list storing all app usage in the last week
        var usm = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        var appList = usm.queryUsageStats(
            interval,
            System.currentTimeMillis() - 1000 * 3600 * 24, System.currentTimeMillis())

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

    // Shows app stats
    fun showAppStats(appStats: SortedMap<Long, UsageStats>) {

        val extras = intent.extras
        var key:String? = "NotFound"
        var userID:String? = "NotFound"
        var password:String? = "NotFound"
        var email:String? = "NotFound"
        if (extras != null) {
            key = extras.getString("primary_key");
            userID = extras.getString("userID")
            password = extras.getString("password")
            email = extras.getString("email")
        }

        //SQLite DB
        val login_db:DBHelper = DBHelper(this)

        var appList = ArrayList<App?>()
        // Creates a new list of UsageStats objects with the appStats values.
        var usageList: List<UsageStats> = ArrayList(appStats.values)
        var totalTime = usageList.stream().map { obj: UsageStats -> obj.totalTimeInForeground }.mapToLong { obj: Long -> obj }.sum()

        // String that is inserted into SQLite.
        var usageStatsDB = "MONTLY STATS: "

        //fill the appsList
        for (usageStats in usageList) {
            try {
                var packageName = usageStats.packageName
                var icon = getDrawable(R.drawable.no_image)
                var packageNames = packageName.split("\\")
                var appName = packageNames[packageNames.size - 1].trim { it <= ' ' }

                if (isAppInfoAvailable(usageStats)) {
                    val ai = applicationContext.packageManager.getApplicationInfo(packageName, 0)
                    icon = applicationContext.packageManager.getApplicationIcon(ai)
                    appName = applicationContext.packageManager.getApplicationLabel(ai).toString()
                }
                var usageDuration = getDurationString(usageStats.totalTimeInForeground)
                var usagePercentage = (usageStats.totalTimeInForeground * 100 / totalTime).toInt()
                var usageStat = App(icon!!, appName, usagePercentage, usageDuration)

                appList.add(usageStat)

                // Add Stats to String.
                usageStatsDB = "$usageStatsDB$appName, $usageDuration; "
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
        // Reverses so that most used app is first
        appList.reverse()
        // Build the adapter
        var adapter = AppAdapter(this, appList)

        // Attach the adapter to a ListView
        var listView = findViewById<ListView>(R.id.app_list)
        listView.adapter = adapter
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
}
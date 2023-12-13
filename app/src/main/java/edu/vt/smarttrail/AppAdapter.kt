package edu.vt.smarttrail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

// Adapts an array specifically for apps.
class AppAdapter  // Initializes an AppAdapter object.
    (context: Context?, appList: ArrayList<edu.vt.smarttrail.App?>?) : ArrayAdapter<edu.vt.smarttrail.App?>(context!!, 0, appList!!) {
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        // Get the data item for this position
        var view = view
        val usageStats = getItem(position)

        // Check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(context).inflate(edu.vt.smarttrail.R.layout.widget_app, parent, false)
        }

        // Lookup view for data population
        val app_name_tv = view!!.findViewById<TextView>(R.id.app_name_tv)
        val usage_duration = view.findViewById<TextView>(R.id.usage_duration)
        val usage_percent = view.findViewById<TextView>(R.id.usage_percent)
        val icon_img = view.findViewById<ImageView>(R.id.icon)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)


        // Populate the data into the template view using the data object
        app_name_tv.text = usageStats!!.appName
        usage_duration.text = usageStats.usageDuration
        usage_percent.text = usageStats.usagePercentage.toString() + "%"
        icon_img.setImageDrawable(usageStats.appIcon)
        progressBar.progress = usageStats.usagePercentage

        // Return the completed view to render on screen
        return view
    }
}
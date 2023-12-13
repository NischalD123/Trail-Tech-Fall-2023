package edu.vt.smarttrail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.vt.smarttrail.db.SurveyHistoryItem

class SurveyHistoryAdapter(val context: Context, private val surveyHistory: List<SurveyHistoryItem>) :
    RecyclerView.Adapter<SurveyHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_survey_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val historyItem = surveyHistory[position]
        holder.tvDate.text = historyItem.timestamp
        holder.tvBadgeStatus.text =
            if (historyItem.badgeAwarded) "Badge Awarded" else "No Badge Awarded"
        if (historyItem.badgeAwarded) {
            holder.ivAward.visibility = View.VISIBLE
            val colorResId = getWeekColor(historyItem.weekNumber)
            holder.ivAward.setColorFilter(context.getColor(colorResId))
            holder.tvBadgeStatus.setTextColor(context.getColor(colorResId))
        } else {
            holder.ivAward.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return surveyHistory.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvBadgeStatus: TextView = itemView.findViewById(R.id.tvBadgeStatus)
        val ivAward: ImageView = itemView.findViewById(R.id.ivAward)
    }

    private fun getWeekColor(weekNumber: Int): Int {
        // Customize this method based on your color resources for each week
        return when (weekNumber) {
            1 -> R.color.purple
            2 -> R.color.yellow
            3 -> R.color.btn_green
            // Add more cases for each week
            else -> R.color.purple_700
        }
    }
}


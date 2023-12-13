package edu.vt.smarttrail.surveyactivity

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

import edu.vt.smarttrail.notifications.*
import edu.vt.smarttrail.R
import java.util.*

/**
 * A simple [Fragment] subclass to confirm completion of survey.
 * Use the [FinishFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FinishFragment : Fragment() {
    companion object {
        const val TAG = "FinishFragment"
        const val PERMISSIONS_REQUEST_LOCATION = 100
    }
    private lateinit var finish_button: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_finish, container, false)
        finish_button = v.findViewById(R.id.finish_btn)
        finish_button.setOnClickListener {
            // Create notification channel to send alarm/notification signal on
            createNotificationChannel()
            // Create notification and set to launch just under a week in the future
            scheduleNotification()


            // return to main activity homepage; check this line
            activity?.finish()
        }

        return v
    }

    private fun scheduleNotification()
    {
        val intent = Intent(requireActivity().applicationContext, NotificationBroadcastReceiver::class.java)
        val title = "SmarTrail Survey"
        val message = "Time to take a trail survey!"
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            requireActivity().applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime()
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        showAlert(time, title, message)
    }

    private fun getTime(): Long
    {
        val MILLIS_IN_WEEK: Long = 604800000 - 1200000 // 20 minutes earlier than exactly in 7 days
        val calendar = Calendar.getInstance()
//    calendar.set(year, month, day, hour, minute)
        return calendar.timeInMillis + MILLIS_IN_WEEK
    }

    private fun createNotificationChannel()
    {
        val name = "Notif Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelID, name, importance)
            channel.description = desc
            val notificationManager = requireContext().getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        } else {
            TODO("VERSION.SDK_INT < O")
            Log.e(TAG, "notification channel creation failed")
        }

    }

    private fun showAlert(time: Long, title: String, message: String)
    {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(requireActivity().applicationContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(requireActivity().applicationContext)
        Log.d(TAG, "Title: " + title +
                "\nMessage: " + message +
                "\nAt: " + dateFormat.format(date) + " " + timeFormat.format(date))
//        AlertDialog.Builder(requireContext())
//            .setTitle("Notification Scheduled")
//            .setMessage(
//                "Title: " + title +
//                        "\nMessage: " + message +
//                        "\nAt: " + dateFormat.format(date) + " " + timeFormat.format(date))
//            .setPositiveButton("Okay"){_,_ ->}
//            .show()
    }
}

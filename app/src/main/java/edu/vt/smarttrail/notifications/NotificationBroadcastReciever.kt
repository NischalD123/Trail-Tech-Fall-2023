package edu.vt.smarttrail.notifications
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import edu.vt.smarttrail.MainActivity
import edu.vt.smarttrail.R


const val notificationID = 1
const val channelID = "channel1"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"

/**
 * Notification class to send notification upon broadcast signal
 */
class NotificationBroadcastReceiver : BroadcastReceiver()
{
    /**
     * Creates the new notification when receiving a signal on the broadcast channel
     */
    override fun onReceive(context: Context, intent: Intent)
    {
        val notifImage = BitmapFactory.decodeResource(
            context.applicationContext.resources,
            R.drawable.mountainbgimage
        )
        val bigPicStyle = NotificationCompat.BigPictureStyle()
            .bigPicture(notifImage)
            .bigLargeIcon(null)
        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setStyle(bigPicStyle)
            .setLargeIcon(notifImage)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(messageExtra))
            .build()
        val contentIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        notification.contentIntent = contentIntent

        val  manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)
    }

}
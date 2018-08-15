package ir.paad.pushtest


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.net.URLDecoder
import java.net.URLEncoder


/*import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;*/

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val mNotification = NotificationModel()

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.d(TAG, "From: " + remoteMessage!!.from!!)
        if (remoteMessage.data.isNotEmpty()) {
            Log.e(TAG, "Message data payload: " + remoteMessage.data)

            val a = "slkdfjslkfjks"

            val b = String(a.toByteArray(), Charsets.UTF_8)

            mNotification.title = remoteMessage.data["title"]
            mNotification.bigTitle = remoteMessage.data["bigTitle"]
            mNotification.message = remoteMessage.data["message"]
            mNotification.bigMessage = remoteMessage.data["bigMessage"]
            mNotification.summary = remoteMessage.data["summary"]
            mNotification.iconUrl = remoteMessage.data["iconUrl"]
            mNotification.smallIconUrl = remoteMessage.data["smallIconUrl"]
            mNotification.imageUrl = remoteMessage.data["imageUrl"]
            mNotification.notificationId = remoteMessage.data["notificationId"]
            mNotification.sendTime = remoteMessage.data["sendTime"]
            mNotification.bgColor = remoteMessage.data["bgColor"]?.toInt()
            mNotification.buttonActionData = remoteMessage.data["buttonActionData"]
            mNotification.sound = remoteMessage.data["sound"]
            mNotification.vibrate = remoteMessage.data["vibrate"]?.toBoolean()!!
            mNotification.showToUser = remoteMessage.data["showToUser"]?.toBoolean()!!
            mNotification.ledColor = remoteMessage.data["ledColor"]?.toInt()

            if (remoteMessage.data["type"]?.toLowerCase().equals("big_text")) {
                mNotification.type = NotificationModel.Type.BIG_TEXT
            }

            if (mNotification.showToUser) {
                sendNotification(mNotification)
            }

        }
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String?) {
        Log.d(TAG, "Refreshed token: " + token!!)

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        subscribe()
        sendRegistrationToServer(token)
    }


    private fun subscribe() {
        FirebaseMessaging.getInstance().subscribeToTopic("all")
                .addOnCompleteListener { task ->
                    var msg = "success subscribe"
                    if (!task.isSuccessful) {
                        msg = "failed subscribe"
                    }
                    Log.d(TAG, msg)
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                }
    }


    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
    }


    /*val intent1 = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0)
        val action = NotificationCompat.Action.Builder(0, "اکشن 1", intent1)
        notificationBuilder.addAction(action.build())

        val intent2 = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0)
        val action2 = NotificationCompat.Action.Builder(0, "action1", intent2)
        notificationBuilder.addAction(action2.build())
*/


    private fun sendNotification(remoteNotification: NotificationModel) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)

        val bigPictureImage = (ContextCompat.getDrawable(this, R.drawable.notifi_big_pic) as BitmapDrawable).bitmap
        val size = Converter.pxFromDp(this , 64f)
        val bigPictureImageSampleSize = Bitmap.createScaledBitmap(bigPictureImage , size.toInt() , size.toInt() , false)

        val launcherBitmap = (ContextCompat.getDrawable(this, R.mipmap.ic_launcher) as BitmapDrawable).bitmap

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(remoteNotification.title)
                .setContentText(remoteNotification.message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setShowWhen(false)

        if (remoteNotification.type == NotificationModel.Type.BIG_IMAGE) {
            notificationBuilder.setStyle(NotificationCompat.BigPictureStyle()
                    .bigPicture(bigPictureImage)
                    .bigLargeIcon(bigPictureImageSampleSize)
                    .setSummaryText(remoteNotification.summary)
                    .setBigContentTitle(remoteNotification.bigTitle))
        } else {
            notificationBuilder.setStyle(NotificationCompat
                    .BigTextStyle()
                    .bigText(remoteNotification.bigMessage)
                    .setBigContentTitle(remoteNotification.bigTitle)
                    .setSummaryText(remoteNotification.summary))
        }


        notificationBuilder.color = ContextCompat.getColor(this, R.color.colorAccent)

        /*notificationBuilder.setStyle(NotificationCompat
                .BigPictureStyle()
                .bigLargeIcon(bitmap.bitmap)
                .bigPicture(bitmap.bitmap)
                .setBigContentTitle("این بیگ تایتل متن است")
                .setSummaryText("این متن خلاصه است "))*/

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }


        //val bigView = RemoteViews(packageName, R.layout.notification_big_layout)
        val smallView = RemoteViews(packageName, R.layout.notification_small_layout)

        //bigView.setImageViewBitmap(R.id.iv_notification_smallImage, bigPictureImage)
        //bigView.setTextViewText(R.id.tv_notification_text, remoteNotification.bigTitle)

        smallView.setImageViewBitmap(R.id.iv_notification_smallImage, launcherBitmap)
        smallView.setTextViewText(R.id.tv_small_notification_title, remoteNotification.title)
        smallView.setTextViewText(R.id.tv__small_notification_message, remoteNotification.message)
        //smallView.setInt(R.id.rl_small_notification_root, "setBackgroundColor", ContextCompat.getColor(this, R.color.colorAccent))

        //notificationBuilder.setCustomBigContentView(bigView)
        notificationBuilder.setCustomContentView(smallView)

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())

    }


    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //val name = getString(R.string.channel_name)
            //val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            //val channel = NotificationChannel(CHANNEL_ID, name, importance)
            //channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            //val notificationManager = getSystemService(NotificationManager::class.java)
            // notificationManager!!.createNotificationChannel(channel)
        }
    }

    companion object {
        private val TAG = "MyFirebaseMsgService"
    }

    private fun getBitmap(url: String): Bitmap {
        return GlideApp.with(this)
                .asBitmap()
                .load(url)
                .submit()
                .get()
    }


}

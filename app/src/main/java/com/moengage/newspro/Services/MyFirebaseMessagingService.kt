package com.moengage.newspro.Services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.moengage.newspro.Activities.MainActivity
import com.moengage.newspro.R

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    companion object{
        const val TAG = "MyFirebaseMessagingService"
        const val CHANNEL_ID = "StatusNotificationChannel"
    }


    //This method is called when notification is received as intent.
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "onMessageReceived() : called")
        Log.d(TAG, "onMessageReceived() : Message received from : ${message.from}")

        val notification = message.notification
        val data = message.data


        //Extract received notification data.
        if (notification != null && data != null) {
            Log.d(TAG, "onMessageReceived() : message.data : $data")
            val title = notification.title
            val body = notification.body
            Log.d(TAG, "onMessageReceived() : showNotification() is going to call")
            //Through notification with title & data received from cloud.
            showNotification(title = title, body = body)
        }
    }


    private fun showNotification(title: String?, body: String?) {
        createNotificationChannel()
        Log.d(TAG,"onMessageReceived() : createNotificationChannel() is called")
        sendStatusNotification(title,body)
        Log.d(TAG,"onMessageReceived() : sendStatusNotification() is called")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Status Notification Channel"
            val descriptionText = "Channel for sending status notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendStatusNotification(title: String?, body: String?) {
        // Create an explicit intent for the MainActivity
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        Log.d(TAG,"onMessageReceived() : sendStatusNotification() : PendingIntent is created")

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.news_app_icon)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setDefaults(Notification.DEFAULT_SOUND)
            .setAutoCancel(true)

        Log.d(TAG,"onMessageReceived() : sendStatusNotification() : Notification builder is created")

        with(NotificationManagerCompat.from(this@MyFirebaseMessagingService)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            Log.d(TAG,"onMessageReceived() : sendStatusNotification() : notify() is called")
            notify(MainActivity.NOTIFICATION_ID, builder.build())
        }
    }


}
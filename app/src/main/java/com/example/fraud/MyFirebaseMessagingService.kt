package com.example.fraud

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.fraud.activity.mainactivity.MainActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private lateinit var fraudDetectionClassifier: FraudDetectionClassifier

    private fun sendRegistrationToServer(debitCardNumber:String,token: String) {
        Firebase.database.getReference("Transactions/debitCardTransaction/$debitCardNumber").child("token").setValue(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${remoteMessage.from}")
        fraudDetectionClassifier = FraudDetectionClassifier()

//        fraudDetectionClassifier.initialise("Fraud")

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
        val data = JSONObject(remoteMessage.data["transactionData"])
        Log.d(TAG, "Message Notification $data")
        val input = ByteBuffer.allocateDirect(12 * 4).order(ByteOrder.nativeOrder())
        input.putFloat(data.getInt("gender").toFloat())
        input.putFloat(data.getInt("city").toFloat())
        input.putFloat(data.getInt("state").toFloat())
        input.putFloat(data.getInt("zip").toFloat())
        input.putFloat(data.getInt("lat").toFloat())
        input.putFloat(data.getInt("long").toFloat())
        input.putFloat(data.getInt("city_pop").toFloat())
        input.putFloat(data.getInt("unix_time").toFloat())
        input.putFloat(data.getInt("category").toFloat())
        input.putFloat(data.getInt("amt").toFloat())
        input.putFloat(data.getInt("merch_lat").toFloat())
        input.putFloat(data.getInt("merch_long").toFloat())
        var output = 12f
        Log.d("ML", "Output: $output")
        fraudDetectionClassifier.initialise("Fraud") {
            output = fraudDetectionClassifier.predict(input)
            Log.d("ML", "Output Real: $output")
//            if (output >= 0.5) {
                sendNotification()
//            }
        }
    }

    private fun sendNotification() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val requestCode = 0
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = "Default"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Fraud Alert")
            .setContentText("Fraudulent Transaction Detected.Please review and Contact Customer Care")
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

}

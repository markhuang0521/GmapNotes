package com.udacity.project4.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthenticationActivity
import com.udacity.project4.locationreminders.ReminderDescriptionActivity
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

private const val NOTIFICATION_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".channel"

@SuppressLint("MissingPermission")
fun addGeofence(
    reminder: ReminderDataItem,
    context: Context
) {
    val geofencingClient = LocationServices.getGeofencingClient(context)
    val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        intent.action = GeofencingConstants.ACTION_GEOFENCE_EVENT
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    // Build the Geofence Object
    val geofence = Geofence.Builder()
        // Set the request ID, string to identify the geofence.
        .setRequestId(reminder.id)
        // Set the circular region of this geofence.
        .setCircularRegion(
            reminder.latitude!!,
            reminder.longitude!!,
            GeofencingConstants.GEOFENCE_RADIUS_IN_METERS
        )
        // Set the expiration duration of the geofence. This geofence gets
        // automatically removed after this period of time.
        .setExpirationDuration(GeofencingConstants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
        // Set the transition types of interest. Alerts are only generated for these
        // transition. We track entry and exit transitions in this sample.
        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
        .build()

    // Build the geofence request
    val geofencingRequest = GeofencingRequest.Builder()
        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)

        // Add the geofences to be monitored by geofencing service.
        .addGeofence(geofence)
        .build()

    // First, remove any existing geofences that use our pending intent
    geofencingClient.removeGeofences(geofencePendingIntent)?.run {
        // Regardless of success/failure of the removal, add the new geofence
        addOnCompleteListener {
            // Add the new geofence request with the new geofence
            geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)?.run {
                addOnSuccessListener {
                    // Geofences added.

                    Log.i("Add Geofence", geofence.requestId)
                    // Tell the viewmodel that we've reached the end of the game and
                    // activated the last "geofence" --- by removing the Geofence.
                }
                addOnFailureListener {
                    // Failed to add geofences.
                    if ((it.message != null)) {
                        Log.i(AuthenticationActivity.TAG, it.message.toString())
                    }
                }
            }
        }
    }
}

fun sendNotification(context: Context, reminderDataItem: ReminderDataItem) {
    val notificationManager = context
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // We need to create a NotificationChannel associated with our CHANNEL_ID before sending a notification.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        && notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null
    ) {
        val name = context.getString(R.string.app_name)
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            name,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

    val intent = ReminderDescriptionActivity.newIntent(context.applicationContext, reminderDataItem)

    //create a pending intent that opens ReminderDescriptionActivity when the user clicks on the notification
    val stackBuilder = TaskStackBuilder.create(context)
        .addParentStack(ReminderDescriptionActivity::class.java)
        .addNextIntent(intent)
    val notificationPendingIntent = stackBuilder
        .getPendingIntent(getUniqueId(), PendingIntent.FLAG_UPDATE_CURRENT)

//    build the notification object with the data to be shown
    val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_save)
        .setLargeIcon(
            BitmapFactory.decodeResource(
                context.resources,
                R.drawable.map
            )
        )
        .setContentTitle(reminderDataItem.title)
        .setContentText(reminderDataItem.location)
        .setContentIntent(notificationPendingIntent)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(getUniqueId(), notification)
}

private fun getUniqueId() = ((System.currentTimeMillis() % 10000).toInt())
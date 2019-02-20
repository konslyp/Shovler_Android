package com.app.shovelerapp.utils;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.app.shovelerapp.R;
import com.app.shovelerapp.activity.AvailableJobActivity;
import com.app.shovelerapp.activity.FirstStepActivity;
import com.app.shovelerapp.activity.HomeActivity;
import com.app.shovelerapp.activity.JobStatusActivity;
import com.app.shovelerapp.activity.WelcomeShovelerActivity;
import com.app.shovelerapp.callback.LogoutCallback;
import com.app.shovelerapp.netutils.NetUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Belal on 5/27/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService implements LogoutCallback {

    private static final String TAG = "MyFirebaseMsgService";
    SharedPrefClass prefClass;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        String messageBody = remoteMessage.getNotification().getBody();
        Log.d(TAG, "Notification Message Body: " + messageBody);
        prefClass = new SharedPrefClass(this);

        if (messageBody.equals("Sorry- you have received a 1 star rating so your account has been deactivated. Please contact admin@shovler.com")) {
            NetUtils.CallLogOut(prefClass.getSavedStringPreference(SharedPrefClass.USER_ID), this, this);
            prefClass.clearKeyVal();
            prefClass.setUserLogin(false);
            startActivity(new Intent(this, WelcomeShovelerActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
        //Calling method to generate notification
        sendNotification(messageBody, remoteMessage.getNotification().getTitle());
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody, String title) {

        Intent intent;
        if (prefClass.isUserLogin()) {
            if (prefClass.getSavedStringPreference(SharedPrefClass.UTYPE).equals("Shovler")) {
                intent = new Intent(this, JobStatusActivity.class);
            } else if (prefClass.getSavedStringPreference(SharedPrefClass.UTYPE).equals("Requester")) {
                intent = new Intent(this, JobStatusActivity.class);
            } else {
                intent = new Intent(this, WelcomeShovelerActivity.class);
            }
        } else {
            intent = new Intent(this, WelcomeShovelerActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.app_logo)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    public void LogoutCallbackSuccess(String success) {

    }

    @Override
    public void LogoutCallbackError(String error) {

    }
}
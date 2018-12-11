package com.georide.georide;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String INTENT_FILTER = "SERVICE_BROADCAST";
    @Override
    public void onNewToken(String token) {
        // Get updated InstanceID token.
        Log.d("Token", "Refreshed token: " + token);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("Message", "From: " + remoteMessage.getFrom());
        Log.d("sdf",remoteMessage.getData().toString());
        Intent broadcastIntent = new Intent(INTENT_FILTER);
        broadcastIntent.putExtra("firebaseUpdate", remoteMessage.getData().toString());
        sendBroadcast(broadcastIntent);
    }
}

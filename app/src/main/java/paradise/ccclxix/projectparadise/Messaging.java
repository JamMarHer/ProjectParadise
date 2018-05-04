package paradise.ccclxix.projectparadise;


import android.app.Notification;

import paradise.ccclxix.projectparadise.Notifications.NotificationHelper;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class Messaging extends FirebaseMessagingService {
    private static final int REQUEST_CODE = 1;
    private static final int NOTIFICATION_ID = 369;

    public Messaging() {
        super();
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...
        super.onMessageReceived(remoteMessage);

        final String message = remoteMessage.getNotification().getBody();
        final String title = remoteMessage.getNotification().getTitle();

        showNotifications(title, message);
    }

    private void showNotifications(String title, String msg) {
        NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
        Notification.Builder nb = notificationHelper.getMessageNotification(title, msg);
        notificationHelper.notify(NOTIFICATION_ID, nb);

    }
}

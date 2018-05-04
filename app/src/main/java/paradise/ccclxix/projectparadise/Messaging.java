package paradise.ccclxix.projectparadise;


import android.app.ActivityManager;
import android.app.Notification;
import android.content.ComponentName;
import android.util.Log;
import android.view.View;

import paradise.ccclxix.projectparadise.Notifications.NotificationHelper;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Arrays;
import java.util.List;

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
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        String[] currentActivityArr = taskInfo.get(0).topActivity.getClassName().split("\\.");
        String currentActivity = currentActivityArr[currentActivityArr.length-1];
        if (!currentActivity.equals("ChatActivity")){
            NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
            Notification.Builder nb = notificationHelper.getMessageNotification(title, msg);
            notificationHelper.notify(NOTIFICATION_ID, nb);
        }
    }
}

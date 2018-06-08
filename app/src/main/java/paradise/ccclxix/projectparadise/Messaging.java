package paradise.ccclxix.projectparadise;


import android.app.ActivityManager;
import android.app.Notification;
import android.content.ComponentName;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.Interfaces.Setting;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.SettingsManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.SettingsRelated.BooleanSetting;
import paradise.ccclxix.projectparadise.Notifications.NotificationHelper;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Messaging extends FirebaseMessagingService {
    private static final int REQUEST_CODE = 1;
    private static final int NOTIFICATION_ID = 369;

    public Messaging() {
        super();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...
        super.onMessageReceived(remoteMessage);

        final String message = remoteMessage.getNotification().getBody();
        final String title = remoteMessage.getNotification().getTitle();

        showNotifications(title, message);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotifications(String title, String msg) {
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        String[] currentActivityArr = taskInfo.get(0).topActivity.getClassName().split("\\.");
        String currentActivity = currentActivityArr[currentActivityArr.length-1];
        if (!currentActivity.equals("ChatActivity")){
            NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
            Notification.Builder nb = notificationHelper.getMessageNotification(title, msg);
            SettingsManager settingsManager = new SettingsManager();
            settingsManager.initialize(getApplicationContext());
            Map<String, Map<String, Setting>> settings = settingsManager.getSettings();
            BooleanSetting booleanSetting = (BooleanSetting) settings.get("Notifications").get("Notifications_All");
            if (booleanSetting.getValue())
                notificationHelper.notify(NOTIFICATION_ID, nb);
        }
    }
}

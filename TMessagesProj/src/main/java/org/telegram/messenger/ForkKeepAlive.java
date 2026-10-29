package org.telegram.messenger;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import org.telegram.ui.LaunchActivity;

/**
 * This build cannot register for Google push (Firebase refuses its package), so notifications
 * depend on keeping our own connection to Telegram alive in the background.
 */
public class ForkKeepAlive {

    private static final String CHANNEL_ID = "fork_keep_alive";
    private static final int NOTIFICATION_ID = 9999;

    public static boolean enabledByDefault() {
        return true;
    }

    public static boolean serverMayDisableKeepAlive() {
        return false;
    }

    public static void startForeground(Service service) {
        try {
            Notification notification = createNotification(service);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                service.startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
            } else {
                service.startForeground(NOTIFICATION_ID, notification);
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    private static Notification createNotification(Service service) {
        createQuietChannel(service);
        PendingIntent openApp = PendingIntent.getActivity(service, 0, new Intent(service, LaunchActivity.class), PendingIntent.FLAG_IMMUTABLE);
        return new NotificationCompat.Builder(service, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("Telegram")
                .setContentText("Connected to receive messages")
                .setContentIntent(openApp)
                .setOngoing(true)
                .setShowWhen(false)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .build();
    }

    private static void createQuietChannel(Service service) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Background connection", NotificationManager.IMPORTANCE_MIN);
            service.getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }
}

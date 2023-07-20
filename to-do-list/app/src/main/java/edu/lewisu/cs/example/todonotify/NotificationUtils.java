package edu.lewisu.cs.example.todonotify;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class NotificationUtils {
    private static final int TODO_REMINDER_NOTIFICATION_ID = 4422;
    private static final String REMINDER_NOTIFICATION_CHANNEL = "reminder_notification_channel";
    public static final int TODO_REMINDER_PENDING_INTENT_ID = 2244;
    public static final int ACTION_IGNORE_PENDING_INTENT_ID = 42;

    public static void remindUser (Context context){
        Intent startActivityIntent = new Intent(context,MainActivity.class);
        PendingIntent startActivityPendingIntent = PendingIntent.getActivity(
                context,
                TODO_REMINDER_NOTIFICATION_ID,
                startActivityIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        Intent ignoreReminderIntent = new Intent(context,NotificationAlertReceiver.class);
        ignoreReminderIntent.setAction(NotificationAlertReceiver.ACTION_DISMISS_NOTIFICATION);
        PendingIntent ignoreReminderPendingIntent = PendingIntent.getBroadcast(
                context,
                ACTION_IGNORE_PENDING_INTENT_ID,
                ignoreReminderIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationManager notificationManager =  (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(REMINDER_NOTIFICATION_CHANNEL,
                    context.getString(R.string.channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,REMINDER_NOTIFICATION_CHANNEL)
                .setColor(ContextCompat.getColor(context,R.color.purple_500))
                .setSmallIcon(R.drawable.ic_assignment_black_24dp)
                .setContentTitle(context.getString(R.string.reminder_title))
                .setContentText(context.getString(R.string.reminder_text))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.reminder_text)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setContentIntent(startActivityPendingIntent)
                .addAction(R.drawable.ic_cancel_black_24dp, "Not now",ignoreReminderPendingIntent);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        notificationManager.notify(TODO_REMINDER_NOTIFICATION_ID, builder.build());
    }
    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(TODO_REMINDER_NOTIFICATION_ID);
    }
}

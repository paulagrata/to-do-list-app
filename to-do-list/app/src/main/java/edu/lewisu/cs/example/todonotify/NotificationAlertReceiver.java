package edu.lewisu.cs.example.todonotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationAlertReceiver extends BroadcastReceiver {

    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";
    public static final String ACTION_REVIEW_REMINDERS = "review-reminders";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(NotificationAlertReceiver.class.getSimpleName(),"action = " + action);

        if (action.equals(ACTION_REVIEW_REMINDERS)){
            NotificationUtils.remindUser(context);
        } else if (action.equals(ACTION_DISMISS_NOTIFICATION)){
            NotificationUtils.clearAllNotifications(context);
        }

    }
}

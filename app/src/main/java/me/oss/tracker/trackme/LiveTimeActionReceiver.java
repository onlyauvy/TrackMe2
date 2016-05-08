package me.oss.tracker.trackme;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * Created by Abdullah on 9/9/2015.
 */
public class LiveTimeActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent alarm=new Intent(context, AlarmReceiver.class);
        boolean alarmRunning=(PendingIntent.getBroadcast(context, 0, alarm, PendingIntent.FLAG_NO_CREATE)!=null);
        if(alarmRunning==false) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarm, 0);
            AlarmManager alarmManager= (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),LocalLog.syn_time,pendingIntent);
        }
    }
}

package me.oss.tracker.trackme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by Abdullah on 9/7/2015.
 */
public class AlarmReceiver extends BroadcastReceiver{
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        Log.e("mamun...","Its ia a testing cycle"+ DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()));

        Intent background = new Intent(context, BackgroundService.class);
        context.startService(background);
    }
//    public boolean office_time(){
//
//        Calendar calendar = new GregorianCalendar();
//        String am_pm;
//        int hour = calendar.get( Calendar.HOUR );
//        int minute = calendar.get( Calendar.MINUTE );
//        // int second = calendar.get(Calendar.SECOND);
//
//        if( calendar.get( Calendar.AM_PM ) == 0 ){
//            am_pm = "AM";
//            if(hour >=9){
//                Log.e("mamun","time:"+hour+":"+minute);
//                return true;
//            }
//
//        }
//        else{
//            am_pm = "PM";
//            if(hour<7){
//                Log.e("mamun","time:"+hour+":"+minute);
//                return true;
//            }
//        }
//        return false;
//    }
}

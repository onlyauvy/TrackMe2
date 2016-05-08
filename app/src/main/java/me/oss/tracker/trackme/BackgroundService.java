package me.oss.tracker.trackme;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Abdullah on 9/7/2015.
 */
public class BackgroundService extends Service{
    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;
    String provider;
    LocationManager locationManager;
//    Location location;
//    boolean gps_enabled;
//    boolean network_enabled;
//    Criteria criteria;
    String bssid, ssid;
    private GPSTracker gps;
    double latitude, longitude;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        this.context=this;

        gps = new GPSTracker(getApplicationContext());

        if(gps.canGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            try {
                provider = gps.getProvider();
            }catch (Exception e){
                provider = "Network";
            }
            //provider = gps.getProvider();

            Log.e("Mamun_location",latitude+"-"+longitude+"-"+provider);
        }else{
            //gps.showSettingsAlert();
        }

//        if(Connectivity_testing.getInstance(getApplicationContext()).isOnline()) {
//            LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
//
//            try {
//                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//            } catch(Exception ex) {}
//
//            try {
//                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//            } catch(Exception ex) {}
//            Log.e("mamun",gps_enabled+" "+network_enabled);
//            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//            if(gps_enabled){
//                // Creating an empty criteria object
//                criteria = new Criteria();
//                provider = locationManager.getBestProvider(criteria, false);
//                location = locationManager.getLastKnownLocation(provider);
//                Log.e("mamun-loc",location+"");
//                if (location != null) {
//                    onLocationChanged(location);
//                }
//                Log.e("point","1");
//
//                if(locationManager.getLastKnownLocation(provider)==null){
//                    criteria = new Criteria();
//                    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//                    criteria.setAltitudeRequired(false);
//                    criteria.setBearingRequired(false);
//                    criteria.setCostAllowed(true);
//                    criteria.setPowerRequirement(Criteria.POWER_LOW);
//                    provider = locationManager.getBestProvider(criteria, false);
//                    location = locationManager.getLastKnownLocation(provider);
//                    Log.e("mamun-loc",location+"");
//                    if (location != null) {
//                        onLocationChanged(location);
//                    }
//                    Log.e("point","2");
//                }
//            }
//            else {
//                criteria = new Criteria();
//                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//                criteria.setAltitudeRequired(false);
//                criteria.setBearingRequired(false);
//                criteria.setCostAllowed(true);
//                criteria.setPowerRequirement(Criteria.POWER_LOW);
//                provider = locationManager.getBestProvider(criteria, true);
//                location = locationManager.getLastKnownLocation(provider);
//                Log.e("mamun-loc",location+"");
//                if (location != null) {
//                    onLocationChanged(location);
//                }
//                Log.e("point","3");
//            }
//        }

        this.isRunning=false;
        this.backgroundThread=new Thread(myTask);
    }




    private Runnable myTask= new Runnable() {
        @Override
        public void run() {
            Log.e("mamun","Background is running");

            TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            String device_id = tm.getDeviceId();

            if(Connectivity_testing.getInstance(getApplicationContext()).isOnline()){

                ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (mWifi.isConnected()) {
                    WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    Log.e("mamun_wifiInfo", wifiInfo.toString());
                    bssid = wifiInfo.getBSSID();
                    ssid = wifiInfo.getSSID().replace("\"","")+"-"+wifiInfo.getMacAddress();
                }else{
                    bssid = "";
                }

                Live_Data_Upload client=new Live_Data_Upload();

                client.execute(LocalLog.Emp_pos_link+"eid="+provider+"&lat="+latitude+"&lon="+longitude+"" +"&dvc="+device_id+"&site="+bssid+"&sitename="+ssid);
                Log.e("Mamun_", LocalLog.Emp_pos_link + "eid=" + provider + "&lat=" + latitude + "&lon=" + longitude + "" + "&dvc=" + device_id + "&site=" + bssid + "&sitename=" + ssid);

            }else{
                Log.e("mamun","offline");
            }
            stopSelf();
        }
    };

    @Override
    public void onDestroy() {
        this.isRunning=false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(!this.isRunning){
            this.isRunning=true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }

    public class Live_Data_Upload extends AsyncTask<String, String, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            URL url;
            HttpURLConnection urlConnection = null;
            JSONArray response = new JSONArray();
            JSONObject jsonObject=new JSONObject();

            try {
                url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                int responseCode = urlConnection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK){
                    String responseString = readStream(urlConnection.getInputStream());
                    Log.v("CatalogClient", responseString);
                    response = new JSONArray(responseString);

                    jsonObject=new JSONObject(response.getString(0));
                    String final_response=jsonObject.getString("Response");

                    jsonObject=new JSONObject(response.getString(0));
                    if(LocalLog.syn_time==Integer.valueOf(jsonObject.getString("UniqueCode"))){
                        Log.e("time","Time not change");
                    }else{
                        LocalLog.syn_time=Integer.valueOf(jsonObject.getString("UniqueCode"));

                        //Here the alarm stopped
                        /*
                            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                            PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                            alarmManager.cancel(sender);
                        */


                        Intent alarm=new Intent(getApplicationContext(), AlarmReceiver.class);
                        boolean alarmRunning=(PendingIntent.getBroadcast(getApplicationContext(), 0, alarm, PendingIntent.FLAG_NO_CREATE)!=null);

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarm, 0);
                        AlarmManager alarmManager= (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),LocalLog.syn_time,pendingIntent);

                    }

                    Log.v("CatalogClient-", final_response);
                }
                else if (responseCode == HttpURLConnection.HTTP_BAD_GATEWAY) {
                    Log.v("CatalogClient", "Bad getway connection :"+ responseCode);
                }else{
                    Log.v("CatalogClient", "Response code:"+ responseCode);
                }

            } catch (Exception e) {
                Log.e("CatalogClient-", e.toString());
            } finally {
                if(urlConnection != null)
                    urlConnection.disconnect();
            }

            return response;
        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                Log.e("CatalogClient-", e.toString());
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }
    }

}

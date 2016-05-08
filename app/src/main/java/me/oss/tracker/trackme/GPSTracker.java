package me.oss.tracker.trackme;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by abdullah on 12/5/2015.
 */
public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location;
    Location locationNetwork, locationGps;// location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        String address = "";
        try {
            locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isNetworkEnabled) {
                // no network provider is enabled
                //showSettingsAlert("Network");
            } else {

                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network"+locationManager);
                    if (locationManager != null) {
                        locationNetwork = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (locationNetwork != null) {
                            latitude = locationNetwork.getLatitude();
                            longitude = locationNetwork.getLongitude();
                        }

                        Log.e("", " network latitude and longitude"+latitude+"----"+longitude);
                    }
                }else{
                    Log.e("Network", "sorry we can't find from network");
                }
// if GPS Enabled get lat/long using GPS Services
//System.out.println("isGPSEnabled"+isGPSEnabled);
                if (isGPSEnabled) {
                    Log.d("in GPS", "GPS IS ENABLE");
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled"+locationManager);
                        if (locationManager != null) {
                            locationGps = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if (locationGps != null) {
                                latitude = locationGps.getLatitude();
                                longitude = locationGps.getLongitude();
                            }else{

                                Log.e("", " stop searching GPS latitude and longitude"+latitude+"----"+longitude);
                            }
                            Log.e("", " GPS latitude and longitude"+latitude+"----"+longitude);
                        }
                    }
                }
// Latest code by amit start
                if(locationNetwork!=null && locationGps!=null){
                    if(locationNetwork.getTime()>locationGps.getTime())
                    {
                        latitude = locationNetwork.getLatitude();
                        longitude = locationNetwork.getLongitude();

                    }
                    else
                    {
                        latitude = locationGps.getLatitude();
                        longitude = locationGps.getLongitude();
                    }


                }
                if (latitude!=0.0 && longitude!=0.0) {
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
//                    address = getAddress(mContext, latitude, longitude);
//                    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//                    if (!address.equalsIgnoreCase("") && address!=null) {
////btnSaveMyLoc.setEnabled(false);
//                        RecentMyLocData homeDTO = new RecentMyLocData (CustomUtil.getInstance(mContext).getUserName(), address, date, latitude, longitude);
//                        dbh.addMyLatestLocation(homeDTO);
//
//                        url = address + "\n       " latitude "," longitude " - Google Maps";
//                    } else {
//                        url = "There is a problem in finding the Address. But you can view the address on Google Map by click on link " +
//                                "           \n"+ "" latitude "," longitude " - Google Maps";
//                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    /*
    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("Network", "Network");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }*/

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    /*
    public void stopUsingGPS(){
        if(locationManager != null){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationManager.removeUpdates(GPSTracker.this);
            }
        }
    }
*/
    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    public String getProvider(){
        try {
            return location.getProvider();
        }catch (Exception e){
            return "GPS";
        }
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
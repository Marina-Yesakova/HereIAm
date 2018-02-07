package lwtech.itad230.hereiam;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class HereIAmService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{
    private final static String LOGTAG = "HereIAmService";

	/**
     * Helper variables to query current location 
     */
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

	/**
     * Data passed from MainActivity  
     */
    private Location mDestinationLocation;
    private double targetDistance;
    private String message;
    private String phone;

	/**
     * Default implementation
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOGTAG, "...onBind...");
        return null;
    }

	/**
     * Populate local state from data passed from MainActivity.
	 * Connect Google API Client to query current location
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOGTAG, "...onStartCommand...");
        if(mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();

        String coordinate = intent.getStringExtra(MainActivity.EXTRA_COORDINATE);
        String[] latlong =  coordinate.split(",");
        double latitude = Double.parseDouble(latlong[0]);
        double longitude = Double.parseDouble(latlong[1]);
        mDestinationLocation= new Location("");
        mDestinationLocation.setLatitude(latitude);
        mDestinationLocation.setLongitude(longitude);

        targetDistance = intent.getDoubleExtra(MainActivity.EXTRA_DISTANCE, 100);
        message  = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        phone  = intent.getStringExtra(MainActivity.EXTRA_CONTACT);

        return super.onStartCommand(intent,flags,startId);
    }

	/**
     * Once we are connected to Google API client we ask it location updates every 2 - 4 seconds. 
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(LOGTAG, "...onConnected...");
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_DENIED) {
            Log.d(LOGTAG, "...onConnected... PermissionCheck failed");
            return;
        }

        if(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient) == null) {
            Log.d(LOGTAG, "...onConnected... getLastLocation failed");
            return;
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(4000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        Log.d(LOGTAG, "...onConnected... Success");
    }

	/**
     * Default implementation
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOGTAG, "...onConnectionSuspended...");
    }

	/**
     * Once we received location change notification from Google API Client 
	 * we check if new location is within target distance from destination.
	 * If so:
	 * 1) we send sms to target contact 
	 * 2) notify MainActivity of that event and stop HereIAmService
	 * 3) notify StartProgramActivity that it should finish itself
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOGTAG, "...onLocationChanged...");

        float distance = location.distanceTo(mDestinationLocation);
        if(distance < targetDistance){
            SmsManager bat = SmsManager.getDefault();
            bat.sendTextMessage(phone, null, message, null, null);

            Log.d(LOGTAG, "...onLocationChanged... message sent" + " " + phone + " " + message);

            Intent cancelStartProgramActivityIntent = new Intent(StartProgramActivity.CANCEL_START_PROGRAM_ACTIVITY);
            sendBroadcast(cancelStartProgramActivityIntent);

            Intent smsSentIntent = new Intent(MainActivity.SMS_SENT);
            sendBroadcast(smsSentIntent);

            this.stopSelf();
        } else {
            Log.d(LOGTAG, "...onLocationChanged... Not a time to send a message yet" + " " + distance + " " + targetDistance);
        }
    }

	/**
     * Default implementation
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(LOGTAG, "...onConnectionFailed...");
    }

	/**
     * We disconnect Google API client when service stops 
     */
    @Override
    public void onDestroy() {
        Log.d(LOGTAG, "...onDestroy...");
        mGoogleApiClient.disconnect();
    }
}
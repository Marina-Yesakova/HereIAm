package lwtech.itad230.hereiam;
import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {
    private final static String LOGTAG = "MainActivity";
    /**
     * Request code passed to the intent to identify its result when it returns.
     */
    private static final int REQUEST_PLACE_PICKER = 1;
    static final int REQUEST_SELECT_CONTACT = 2;
    private static final int REQUEST_LOCATION = 3;
    private static final int REQUEST_SMS = 4;

    private TextView contactName;
    private EditText messageView;
    private String contactPhone;
    private TextView distanceView;
    private String distance;
    private TextView destinationView;
    private String destination;
    Button start;

    boolean contactPicked = false;
    boolean messagePicked = false;
    boolean distancePicked = false;
    boolean destinationPicked = false;

    public static final String EXTRA_CONTACT = "Contact result";
    public static final String EXTRA_MESSAGE = "Message View";
    public static final String EXTRA_DISTANCE = "Distanse Message";
    public static final String EXTRA_DESTINATION = "Target Destination";
    public static final String EXTRA_COORDINATE = "Coordinate";

    public static final String SMS_SENT = "Sms_Sent";

    private BroadcastReceiver mSmsSentMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(MainActivity.this, "Message was sent!", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOGTAG, "...onCreate...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactName = (TextView) findViewById(R.id.contactResult);
        contactPhone = "";

        start = (Button) findViewById(R.id.start);

        messageView = (EditText) findViewById(R.id.editText);

        distanceView = (TextView) findViewById(R.id.distanseMessage);
        distance = "";

        destinationView = (TextView) findViewById(R.id.targetDestination);
        destination = "";

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBarDistance);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                distanceView.setText("Distance: " + progress + " meters");
                distance = "" + progress;
                distancePicked = true;
                checkIfStartEnabled();
            }
        });

        checkIfStartEnabled();

        registerReceiver(mSmsSentMessageReceiver, new IntentFilter(SMS_SENT));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d(LOGTAG, "...onSaveInstanceState...");
    }

    public void onOpenMapButtonClick(View v) {
        Log.d(LOGTAG, "...onOpenMapButtonClick... ACTION_PICK_PLACE");
        // Use the PlacePicker Builder to construct an Intent.
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            // Start the Intent by requesting a result, identified by a request code.
            startActivityForResult(intent, REQUEST_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException e) {
            Log.d(LOGTAG, "...onOpenMapButtonClick... GooglePlayServicesRepairableException");
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), this, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.d(LOGTAG, "...onOpenMapButtonClick... GooglePlayServicesNotAvailableException");
            Toast.makeText(this, "Google Play Services is not available.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Extracts data from PlacePicker result.
     * This method is called when an Intent has been started by calling startActivityForResult.
     * The Intent for the PlacePicker is started with REQUEST_PLACE_PICKER or REQUEST_SELECT_CONTACT request code.
     * When a result with this request code is received in this method, its data is extracted.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOGTAG, "onActivityResult start");
        if (requestCode == REQUEST_PLACE_PICKER) {
            Log.d(LOGTAG, "onActivityResult REQUEST_PLACE_PICKER");
            // This result is from the PlacePicker dialog.
            if (resultCode == Activity.RESULT_OK) {
                Log.d(LOGTAG, "onActivityResult REQUEST_PLACE_PICKER RESULT_OK");
                // User has picked a place, extract data.
                final Place place = PlacePicker.getPlace(data, this);
                // A Place object contains details about that place, such as its name, address and phone number.
                // Extract the name, address.
                String name = place.getName().toString();
                boolean nameContainsCoordinates = name.contains("\"N") && name.contains("\"W");
                final String address = place.getAddress().toString();
                boolean addressIsEmpty = (address == null || address.isEmpty());
                if (nameContainsCoordinates && !addressIsEmpty) {
                    name = "";
                }
                destinationView.setText(name + " " + address);

                destination = place.getLatLng().latitude + "," + place.getLatLng().longitude;
                destinationPicked = true;
                checkIfStartEnabled();

                final String placeId = place.getId();
                Log.d(LOGTAG, "Place selected: " + placeId + " (" + name.toString() + ")");
            } else {
                Log.d(LOGTAG, "onActivityResult REQUEST_PLACE_PICKER NOT RESULT_OK " + resultCode);
            }
        } else if (requestCode == REQUEST_SELECT_CONTACT && resultCode == RESULT_OK) {
            Log.d(LOGTAG, "onActivityResult REQUEST_SELECT_CONTACT RESULT_OK");
            Uri contactUri = data.getData();
            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                String nameAndPhone = cursor.getString(columnIndex);
                columnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                contactPhone = cursor.getString(columnIndex);
                nameAndPhone += " " + contactPhone ;
                contactName.setText(nameAndPhone);
                contactPicked = true;
                checkIfStartEnabled();
            } else {
                Log.d(LOGTAG, "onActivityResult NO data REQUEST_SELECT_CONTACT");
            }
        } else {
            Log.d(LOGTAG, "onActivityResult NOT REQUEST_PLACE_PICKER and NOT REQUEST_SELECT_CONTACT");
            super.onActivityResult(requestCode, resultCode, data);
        }
        Log.d(LOGTAG, "onActivityResult end");
    }

    protected void onSearchContactClick (View view) {
        Log.d(LOGTAG, "...onSearchContactClick...");
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_SELECT_CONTACT);
        }
    }

    protected void onStartButtonClick(View v) {
        String messageViewText = messageView.getText().toString();
        Log.d(LOGTAG, "...onStartButtonClick..." + " " + contactName.getText() + " " + messageViewText + " " + distance + " " + destination + " " + contactPhone);

        Intent intentService = new Intent(this, HereIAmService.class)
                .putExtra(EXTRA_CONTACT, contactPhone)
                .putExtra(EXTRA_MESSAGE, messageViewText)
                .putExtra(EXTRA_COORDINATE, destination)
                .putExtra(EXTRA_DISTANCE, Double.parseDouble(distance));
        startService(intentService);

        Intent intentActivity = new Intent(this, StartProgramActivity.class)
                .putExtra(EXTRA_CONTACT, contactName.getText())
                .putExtra(EXTRA_MESSAGE, messageViewText)
                .putExtra(EXTRA_DISTANCE,distance)
                .putExtra(EXTRA_DESTINATION, destinationView.getText().toString());
        startActivity(intentActivity);
    }

    public void checkIfStartEnabled() {
        Log.d(LOGTAG, "checkIfStartEnabled" + " " + contactPicked + " " + messagePicked + " " + distancePicked + " " + destinationPicked);
        start.setEnabled(contactPicked && distancePicked && destinationPicked);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOGTAG, "...onStart...");

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(LOGTAG, "...onStart... Check LOCATION Permissions Now");
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            Log.d(LOGTAG, "...onStart... we have LOCATION permissions");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(LOGTAG, "...onStart... Check SMS Permissions Now");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    REQUEST_SMS);
        } else {
            Log.d(LOGTAG, "...onStart... we have SMS permissions");
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if(grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(LOGTAG, "...onRequestPermissionsResult... We can now safely use the LOCATION API we requested access to");
            } else {
                Log.d(LOGTAG, "...onRequestPermissionsResult... We still don't have permissions to do LOCATION");
            }
        }
        if (requestCode == REQUEST_SMS) {
            if(grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(LOGTAG, "...onRequestPermissionsResult...We can now safely use the SMS API we requested access to");
            } else {
                Log.d(LOGTAG, "...onRequestPermissionsResult...We still don't have permissions to do SMS");
            }
        }
    }
}

package lwtech.itad230.hereiam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class StartProgramActivity extends AppCompatActivity {
    private final static String LOGTAG = "StartProgramActivity";

    public static final String CANCEL_START_PROGRAM_ACTIVITY = "Cancel_StartProgramActivity";

    private TextView contactView;
    private TextView messageView;
    private TextView distanceView;
    private TextView destinationView;

    private BroadcastReceiver mCancelMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //shows the activity_receive_message layout
        setContentView(R.layout.activity_start_program);
        //getting intent form MainActivity
        Intent intent = getIntent();
        //extract the text that was placed as an Extra in the Intent
        String contactViewString = intent.getStringExtra(MainActivity.EXTRA_CONTACT);
        String messageTextString = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String distanceViewString = intent.getStringExtra(MainActivity.EXTRA_DISTANCE);
        String destinationViewString = intent.getStringExtra(MainActivity.EXTRA_DESTINATION);

        Log.d(LOGTAG, "...onCreate..." + " " + contactViewString + " " + messageTextString + " " + distanceViewString + " " + destinationViewString);
        contactView = (TextView) findViewById(R.id.contactResult);
        contactView.setText(contactViewString);

        messageView = (TextView) findViewById(R.id.messageView);
        messageView.setText(messageTextString);

        distanceView = (TextView) findViewById(R.id.distanseMessage);
        distanceView.setText(distanceViewString);

        destinationView = (TextView) findViewById(R.id.targetDestination);
        destinationView.setText(destinationViewString);

        registerReceiver(mCancelMessageReceiver, new IntentFilter(CANCEL_START_PROGRAM_ACTIVITY));
    }

    protected void onCancelButtonClick(View v) {
        Log.d(LOGTAG, "...onCancelButtonClick...");
        stopService(new Intent(StartProgramActivity.this, HereIAmService.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOGTAG, "...onDestroy...");
        unregisterReceiver(mCancelMessageReceiver);
    }

}



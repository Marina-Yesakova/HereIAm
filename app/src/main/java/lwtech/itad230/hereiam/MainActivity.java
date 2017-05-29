package lwtech.itad230.hereiam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private final static String LOGTAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOGTAG, "...onCreate...");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d(LOGTAG, "...onSaveInstanceState...");
    }

    /*protected void onQuickContactBadgeClick(View v) {
        Log.d(LOGTAG, "...onWidgetsButtonClick...");
        // When you want an activity to start a second activity, use an intent.
        // An intent isan "intent to do something". It's a message that you
        // send to Android, stating that you want another activity started.
        Intent intent = new Intent(this, WidgetsActivity.class);
        startActivity(intent);
    }
*/
    protected void onExitButtonClick(View v) {
        Log.d(LOGTAG, "...onExitButtonClick...");
        finish();
        System.exit(0);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOGTAG, "...onStart...");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOGTAG, "...onResume...");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOGTAG, "...onPause...");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOGTAG, "...onStop...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOGTAG, "...onDestroy...");
    }
}

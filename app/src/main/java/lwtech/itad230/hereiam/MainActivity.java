package lwtech.itad230.hereiam;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.app.Activity;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;


public class MainActivity extends Activity {
    private final static String LOGTAG = "MainActivity";

    /**
     * Action to launch the PlacePicker from a card. Identifies the card action.
     */
    private static final int ACTION_PICK_PLACE = 1;

    /**
     * Request code passed to the PlacePicker intent to identify its result when it returns.
     */
    private static final int REQUEST_PLACE_PICKER = 1;

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

    public void onOpenMapButtonClick(View v) {

            Log.d(LOGTAG, "onCardClick ACTION_PICK_PLACE");
            // BEGIN_INCLUDE(intent)
            /* Use the PlacePicker Builder to construct an Intent.
            Note: This sample demonstrates a basic use case.
            The PlacePicker Builder supports additional properties such as search bounds.
             */
            try {
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                Intent intent = intentBuilder.build(this);
                // Start the Intent by requesting a result, identified by a request code.
                startActivityForResult(intent, REQUEST_PLACE_PICKER);

                // Hide the pick option in the UI to prevent users from starting the picker
                // multiple times.
                showPickAction(false);

            } catch (GooglePlayServicesRepairableException e) {
                Log.d(LOGTAG, "onCardClick GooglePlayServicesRepairableException");
                GooglePlayServicesUtil
                        .getErrorDialog(e.getConnectionStatusCode(), this, 0);
            } catch (GooglePlayServicesNotAvailableException e) {
                Log.d(LOGTAG, "onCardClick GooglePlayServicesNotAvailableException");
                Toast.makeText(this, "Google Play Services is not available.",
                        Toast.LENGTH_LONG)
                        .show();
            }

            // END_INCLUDE(intent)
        }


    /**
     * Extracts data from PlacePicker result.
     * This method is called when an Intent has been started by calling
     * {@link #startActivityForResult(android.content.Intent, int)}. The Intent for the
     * {@link com.google.android.gms.location.places.ui.PlacePicker} is started with
     * {@link #REQUEST_PLACE_PICKER} request code. When a result with this request code is received
     * in this method, its data is extracted by converting the Intent data to a {@link Place}
     * through the
     * {@link com.google.android.gms.location.places.ui.PlacePicker#getPlace(android.content.Intent,
     * android.content.Context)} call.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOGTAG, "onActivityResult start");
        // BEGIN_INCLUDE(activity_result)
        if (requestCode == REQUEST_PLACE_PICKER) {
            Log.d(LOGTAG, "onActivityResult REQUEST_PLACE_PICKER");
            // This result is from the PlacePicker dialog.

            // Enable the picker option
            showPickAction(true);

            if (resultCode == Activity.RESULT_OK) {
                Log.d(LOGTAG, "onActivityResult REQUEST_PLACE_PICKER RESULT_OK");
                /* User has picked a place, extract data.
                   Data is extracted from the returned intent by retrieving a Place object from
                   the PlacePicker.
                 */
                final Place place = PlacePicker.getPlace(data, this);

                /* A Place object contains details about that place, such as its name, address
                and phone number. Extract the name, address, phone number, place ID and place types.
                 */
                final CharSequence name = place.getName();
                final CharSequence address = place.getAddress();
                final CharSequence phone = place.getPhoneNumber();
                final String placeId = place.getId();
                String attribution = PlacePicker.getAttributions(data);
                if(attribution == null){
                    attribution = "";
                }

                // Update data on card.
                /*getCardStream().getCard(CARD_DETAIL)
                        .setTitle(name.toString())
                        .setDescription(getString(R.string.detail_text, placeId, address, phone,
                                attribution));
*/
                // Print data to debug log
                Log.d(LOGTAG, "Place selected: " + placeId + " (" + name.toString() + ")");

                // Show the card.
                //getCardStream().showCard(CARD_DETAIL);

            } else {
                Log.d(LOGTAG, "onActivityResult REQUEST_PLACE_PICKER NOT RESULT_OK " + resultCode);
                // User has not selected a place, hide the card.
                //getCardStream().hideCard(CARD_DETAIL);
            }

        } else {
            Log.d(LOGTAG, "onActivityResult NOT REQUEST_PLACE_PICKER");
            super.onActivityResult(requestCode, resultCode, data);
        }
        Log.d(LOGTAG, "onActivityResult end");
        // END_INCLUDE(activity_result)
    }

    /**
     * Sets the visibility of the 'Pick Action' option on the 'Pick a place' card.
     * The action should be hidden when the PlacePicker Intent has been fired to prevent it from
     * being launched multiple times simultaneously.
     * @param show
     */
    private void showPickAction(boolean show){
        Log.d(LOGTAG, "showPickAction " + show);

    }
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

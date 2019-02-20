package com.app.shovelerapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.app.shovelerapp.activity.FirstStepActivity;
import com.app.shovelerapp.utils.Constants;

import java.util.List;
import java.util.Locale;


/**
 * Asynchronously handles an intent using a worker thread. Receives a ResultReceiver object and a
 * location through an intent. Tries to fetch the address for the location using a Geocoder, and
 * sends the result to the ResultReceiver.
 */
public class FetchAddressIntentService extends IntentService {
    private static final String TAG = "FetchAddressIS";
    String strAdd;

    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(" ");
                }

                if (returnedAddress.getPostalCode() != null)
                    Constants.Postal_code = addresses.get(0).getPostalCode();


                Log.v("ser zipcode", "" + Constants.Postal_code);

                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", "" + strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }

        Intent intent1 = new Intent("pranoti");
        // You can also include some extra data.
        intent1.putExtra("address", "" + strAdd);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);

       /* Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(FirstStepActivity.AddressResultReceiver.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("address", "" + strAdd);
        sendBroadcast(broadcastIntent);*/
    }

    /**
     * Sends a resultCode and message to the receiver.
     */


}

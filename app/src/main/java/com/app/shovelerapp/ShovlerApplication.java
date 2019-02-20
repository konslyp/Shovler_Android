package com.app.shovelerapp;

import android.app.Application;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.app.shovelerapp.service.GPSTracker;
import com.app.shovelerapp.service.ServiceManager;
import com.app.shovelerapp.utils.SharedPrefClass;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;


/**
 * Created by supriya.n on 05-07-2016.
 */
@ReportsCrashes(
        mailTo = "pgyhw718@hotmail.com", // my email here
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash)
public class ShovlerApplication extends Application {
    public static ShovlerApplication mInstance;
    String TAG="ShovlerApplication";
    private RequestQueue mRequestQueue;
    private GPSTracker tracker;
    public boolean isTrack = true;
    private SharedPrefClass prefClass;

    @Override
    public void onCreate() {
        super.onCreate();
//        TestFairy.begin(this, "829620a94bdad5afd0c63b1951d519f134f57cdf"); // e.g "0000111122223333444455566667777788889999";

        ACRA.init(this);
        mInstance = this;

        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);

        tracker = new GPSTracker(this);

        startTrackLocation();
    }

    public void startTrackLocation()
    {
        new Thread()
        {
            public void run()
            {
                while(isTrack)
                {
                    try {
                        sendLocationToServer();
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
    public void sendLocationToServer()
    {
        prefClass = new SharedPrefClass(this);

        // navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (!prefClass.isUserLogin()) {
            return;
        }
        String mUserId = prefClass.getSavedStringPreference(SharedPrefClass.USER_ID);
        tracker.getLocation();
        ServiceManager.onUpdateLocation(mUserId,tracker.getLatitude(),tracker.getLongitude());

    }
    public static synchronized ShovlerApplication getInstance() {
        return mInstance;
    }

    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }

    /***
     * Tracking screen view
     *
     * @param screenName screen name to be displayed on GA dashboard
     */
    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    /***
     * Tracking exception
     *
     * @param e exception to be tracked
     */
    public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(
                            new StandardExceptionParser(this, null)
                                    .getDescription(Thread.currentThread().getName(), e))
                    .setFatal(false)
                    .build()
            );
        }
    }

    /***
     * Tracking event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */
    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }
}

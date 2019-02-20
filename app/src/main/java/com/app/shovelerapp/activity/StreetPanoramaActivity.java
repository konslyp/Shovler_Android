package com.app.shovelerapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.app.shovelerapp.R;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Administrator on 5/10/2017.
 */
public class StreetPanoramaActivity extends AppCompatActivity {

    // Cole St, San Fran
    private static final LatLng SAN_FRAN = new LatLng(37.765927, -122.449972);

    private StreetViewPanorama mStreetViewPanorama;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_panorama);
        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment)
                        getSupportFragmentManager().findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                new OnStreetViewPanoramaReadyCallback() {
                    @Override
                    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                        mStreetViewPanorama = panorama;
                        mStreetViewPanorama.setStreetNamesEnabled(false);
                        mStreetViewPanorama
                                .setUserNavigationEnabled(false);
                        mStreetViewPanorama.setZoomGesturesEnabled(true);
                        mStreetViewPanorama.setPanningGesturesEnabled(true);

                        // Only set the panorama to SAN_FRAN on startup (when no panoramas have been
                        // loaded which is when the savedInstanceState is null).
                        if (savedInstanceState == null) {
                            //mStreetViewPanorama.setPosition("1320 Trafalgar st. Teaneck NJ 07666");
                            mStreetViewPanorama.setPosition(SAN_FRAN);
                        }
                    }
                });

    }
}

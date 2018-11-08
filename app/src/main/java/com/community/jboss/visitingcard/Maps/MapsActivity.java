package com.community.jboss.visitingcard.Maps;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.community.jboss.visitingcard.R;
import com.community.jboss.visitingcard.VisitingCard.ViewVisitingCard;
import com.community.jboss.visitingcard.VisitingCard.VisitingCardActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in_linear, R.anim.fade_out_linear);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean darkThemeEnabled = preferences.getBoolean(getString(R.string.PREF_DARK_THEME), false);
        if (darkThemeEnabled) {
            setTheme(R.style.AppTheme_Dark_NoActionBar);
        }
        setContentView(R.layout.activity_maps);

        View bottomSheet = findViewById(R.id.bottom_sheet);

        if (darkThemeEnabled) {
            //setting dark background to the bottom sheet
            @ColorInt int darkColor = Color.parseColor("#FF303030"); //@android:color/background_material_dark
            bottomSheet.setBackground(new ColorDrawable(darkColor));
        }

        final BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);

        // TODO: Replace the TextView with a ListView containing list of Visiting cards in that locality using geo-fencing

        // TODO: List item click should result in launching of ViewVisitingCard Acitivity with the info of the tapped Visiting card.

        TextView list_item = findViewById(R.id.list_item);
        list_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toVisitingCardView = new Intent(MapsActivity.this, ViewVisitingCard.class);
                startActivity(toVisitingCardView);
            }
        });

        //TODO: Create Custom pins for the selected location
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //TODO: Implement geo-fencing(NOT AS A WHOLE) just visual representation .i.e., a circle of an arbitrary radius with the PIN being the centre of it.
        //TODO: Make the circle color as @color/colorAccent
    }


    // TODO: Replace the stating location with user's current location.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Customise the map's style from JSON
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.dark_map_style));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    protected void onResume() {
        overridePendingTransition(R.anim.fade_in_linear, R.anim.fade_out_linear);
        super.onResume();
    }

    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.fade_in_linear, R.anim.fade_out_linear);
        super.onPause();
    }
}

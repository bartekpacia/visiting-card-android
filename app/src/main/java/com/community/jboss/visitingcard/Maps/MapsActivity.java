package com.community.jboss.visitingcard.Maps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.community.jboss.visitingcard.R;
import com.community.jboss.visitingcard.VisitingCard.ViewVisitingCard;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private static final int RC_FINE_LOCATION = 101;

    private GoogleMap mMap;

    //use it as a blueprint to create new MarkerOptions!
    private MarkerOptions defaultMarkerOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        View bottomSheet = findViewById(R.id.bottom_sheet);
        final BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);

        defaultMarkerOptions = new MarkerOptions();
        defaultMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_visiting_card));

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
        LatLng katowicePosition = new LatLng(50.270, 19.039);

        MarkerOptions katowiceMarkerOptions = defaultMarkerOptions;
        katowiceMarkerOptions
                .position(katowicePosition)
                .title("Katowice - the capital of Silesia region");

        mMap.addMarker(katowiceMarkerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(katowicePosition, 6));
        checkPermissions();
    }

    /**
     * Checks if location permissions are granted. If they're not, opens up an AlertDialog asking user for accessing location
     */
    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, RC_FINE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RC_FINE_LOCATION: {
                //if location permissions are allowed, display user position on map
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                        break;
                    }
                }
                //location is not allowed, close activity
                else {
                    Toast.makeText(MapsActivity.this, getString(R.string.missing_permissions_alert), Toast.LENGTH_LONG).show();
                }
                break;
            }
            default: {
                Log.w(TAG, "Unknown permission request code");
            }
        }
    }
}

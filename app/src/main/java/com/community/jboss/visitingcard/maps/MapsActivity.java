package com.community.jboss.visitingcard.maps;

import android.content.Intent;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import android.widget.Toast;
import com.community.jboss.visitingcard.R;
import com.community.jboss.visitingcard.networking.CardData;
import com.community.jboss.visitingcard.networking.FirebaseConstants;
import com.community.jboss.visitingcard.visitingcard.FavoritesRecyclerAdapter;
import com.community.jboss.visitingcard.visitingcard.OnCardButtonClickListener;
import com.community.jboss.visitingcard.visitingcard.ViewVisitingCard;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private FirebaseFirestore firestore;
    private FirebaseUser user;

    private ArrayList<CardData> cardsList = new ArrayList<>();

    private FavoritesRecyclerAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        View bottomSheet = findViewById(R.id.bottom_sheet);
        final BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        adapter = new FavoritesRecyclerAdapter(this, cardsList, false, new OnCardButtonClickListener() {
            @Override
            public void onClick(int index) {
                firestore.collection(FirebaseConstants.USERS_COLLECTION)
                        .document(user.getEmail())
                        .collection(FirebaseConstants.FAVORITES_COLLECTION)
                        .add(cardsList.get(index))
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(MapsActivity.this, cardsList.get(index).getName() + " added to favorites!", Toast.LENGTH_SHORT).show();
                        });
            }
        });

        recyclerView = bottomSheet.findViewById(R.id.recyclerViewNearby);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();
        firestore.collection(FirebaseConstants.CARDS_COLLECTION).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    CardData cardData = documentSnapshot.toObject(CardData.class);
                    cardsList.add(cardData);
                }
                for (CardData cardData : cardsList) {
                    Log.d("Firebase", "UID:" + cardData.getUid());
                }
                adapter.notifyDataSetChanged();
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
        LatLng poland = new LatLng(51, 19);
        mMap.addMarker(new MarkerOptions().position(poland).title("Marker in Poland"))
                .setIcon(
                        BitmapDescriptorFactory.fromResource(R.drawable.custom_pin)
                );
        mMap.addMarker(new MarkerOptions().position(poland).title("Marker in Poland"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(poland, 17));
    }
}

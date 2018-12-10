package com.community.jboss.visitingcard.visitingcard;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Adapter;
import android.widget.Toast;
import com.community.jboss.visitingcard.R;
import com.community.jboss.visitingcard.networking.CardData;
import com.community.jboss.visitingcard.networking.FirebaseConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {
    private static final String TAG = "FavoritesActivity";

    private FavoritesRecyclerAdapter adapter;
    private ArrayList<CardData> cardsList = new ArrayList<>();
    private RecyclerView recyclerView;

    private FirebaseUser user;
    private FirebaseFirestore firestore;
    private CollectionReference favoritesCollectionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        user = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        favoritesCollectionRef = firestore.collection(FirebaseConstants.USERS_COLLECTION)
                .document(user.getEmail())
                .collection(FirebaseConstants.FAVORITES_COLLECTION);

        adapter = new FavoritesRecyclerAdapter(this, cardsList, true, new OnCardButtonClickListener() {
            @Override
            public void onClick(int index) {
                delete(index);
            }
        });

        recyclerView = findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        refresh();

    }

    private void delete(int index) {
        Log.d(TAG, String.valueOf(index));
        favoritesCollectionRef
                .whereEqualTo(FirebaseConstants.UID, cardsList.get(index).getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "UID:" + cardsList.get(index).getUid());
                            Log.d(TAG, "Size:" + String.valueOf(task.getResult().size()));

                            for (DocumentSnapshot document : task.getResult()) {
                                favoritesCollectionRef.document(document.getId()).delete();
                            }
                            refresh();
                        } else {
                            Log.w(TAG, "Error getting documents" + task.getException());
                        }
                    }
                });
    }

    private void refresh() {
        favoritesCollectionRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    cardsList.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        CardData cardData = documentSnapshot.toObject(CardData.class);

                        Log.d(TAG, cardData.getUid());
                        cardsList.add(cardData);
                    }
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}

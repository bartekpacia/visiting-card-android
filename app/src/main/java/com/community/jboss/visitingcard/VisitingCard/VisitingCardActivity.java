package com.community.jboss.visitingcard.VisitingCard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.community.jboss.visitingcard.Maps.MapsActivity;
import com.community.jboss.visitingcard.R;
import com.community.jboss.visitingcard.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.community.jboss.visitingcard.FirebaseConstants.*;

public class VisitingCardActivity extends AppCompatActivity {
    private static final String TAG = "VisitingCardActivity";
    private static final int RC_PICK_IMAGE = 9002;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    private FirebaseUser user;

    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.button_openMap) Button buttonOpenMap;
    @BindView(R.id.imageView_profileImage) CircleImageView profileImage;
    @BindView(R.id.textView_email) TextView email;
    @BindView(R.id.editText_name) EditText name;
    @BindView(R.id.editText_linkedIn) EditText linkedin;
    @BindView(R.id.editText_github) EditText github;
    @BindView(R.id.editText_twitter) EditText twitter;
    @BindView(R.id.editText_fb) EditText facebook;
    @BindView(R.id.editText_gplus) EditText googleplus;
    @BindView(R.id.editText_phone) EditText phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visiting_card);
        ButterKnife.bind(this);

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firestore.setFirestoreSettings(settings);
        storageReference = FirebaseStorage.getInstance().getReference();
        user = auth.getCurrentUser();

        email.setText(user.getEmail());
        name.setText(user.getDisplayName());
        updateProfileImage();

        firestore.collection(USERS_COLLECTION)
                .document(user.getEmail())
                .get()
                .addOnSuccessListener(snapshot -> {
                    Toast.makeText(VisitingCardActivity.this, R.string.data_download_completed, Toast.LENGTH_SHORT).show();
                    updateViews(snapshot);
                }).addOnFailureListener(e -> Toast.makeText(VisitingCardActivity.this, R.string.data_download_error, Toast.LENGTH_SHORT).show());

        setListeners();
    }

    private void selectProfileImage() {
        Intent imageIntent = new Intent();
        imageIntent.setType("image/*");
        imageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(imageIntent, getString(R.string.select_profile_image)), RC_PICK_IMAGE);
    }

    private void updateViews(DocumentSnapshot snapshot) {

        Map<String, Object> data = new HashMap<>();
        data.put(KEY_LINKEDIN, "");
        data.put(KEY_GITHUB, "");
        data.put(KEY_TWITTER, "");
        data.put(KEY_FB, "");
        data.put(KEY_GPLUS, "");
        data.put(KEY_PHONE, "");

        // Update only if data isn't null
        if (snapshot.getData() != null) {
            data = snapshot.getData();
        }

        linkedin.setText(data.get(KEY_LINKEDIN).toString());
        github.setText(data.get(KEY_GITHUB).toString());
        twitter.setText(data.get(KEY_TWITTER).toString());
        facebook.setText(data.get(KEY_FB).toString());
        googleplus.setText(data.get(KEY_GPLUS).toString());
        phone.setText(data.get(KEY_PHONE).toString());
    }

    private void updateProfileImage() {
        //TODO Fix rotation issues

        Uri url = user.getPhotoUrl();
        Picasso.get()
                .load(url)
                .noFade()
                .into(profileImage);
    }

    /**
     * Uploads user data to Firestore and Storage. Document name is user's gmail, as it's unique.
     */
    private void uploadData() {
        Map<String, Object> data = new HashMap<>();
        data.put(KEY_LINKEDIN, linkedin.getText().toString());
        data.put(KEY_GITHUB, github.getText().toString());
        data.put(KEY_TWITTER, twitter.getText().toString());
        data.put(KEY_FB, facebook.getText().toString());
        data.put(KEY_GPLUS, googleplus.getText().toString());
        data.put(KEY_PHONE, phone.getText().toString());

        firestore.collection(USERS_COLLECTION).document(user.getEmail())
                .set(data)
                .addOnSuccessListener(aVoid -> Toast.makeText(VisitingCardActivity.this, getString(R.string.upload_completed), Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(VisitingCardActivity.this, getString(R.string.upload_failed), Toast.LENGTH_SHORT).show());

        String localUsername = name.getText().toString();
        if (!Objects.equals(user.getDisplayName(), localUsername)) {
            updateUserDisplayName(localUsername);
        }
    }

    private void uploadImage(Uri path) {
        if (path == null) return;

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle(R.string.uploading_image);
        dialog.show();

        //TODO Let user delete his photos/data (required in European Union by GDPR)

        storageReference.child(PROFILE_IMAGES_DIR + "/" + createFilename()).putFile(path)
                .addOnSuccessListener(taskSnapshot -> {
                    updateUserProfileImage(taskSnapshot.getDownloadUrl());
                    Toast.makeText(VisitingCardActivity.this, R.string.upload_completed, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(VisitingCardActivity.this, R.string.upload_failed, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .addOnProgressListener(taskSnapshot -> {
                    int uploadProgress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    dialog.setMessage(uploadProgress + "% " + getString(R.string.completed_progress));
                });
    }

    private void updateUserProfileImage(Uri uri) {
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        user.updateProfile(changeRequest)
                .addOnSuccessListener(aVoid -> {
                    updateProfileImage();
                    Toast.makeText(VisitingCardActivity.this, R.string.profile_update_completed, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(VisitingCardActivity.this, R.string.profile_update_failed, Toast.LENGTH_SHORT).show())
                .addOnCanceledListener(() -> Toast.makeText(VisitingCardActivity.this, R.string.profile_update_canceled, Toast.LENGTH_SHORT).show());
    }

    private void updateUserDisplayName(String newName) {
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();

        user.updateProfile(changeRequest)
                .addOnSuccessListener(aVoid -> Toast.makeText(VisitingCardActivity.this, R.string.profile_update_completed, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(VisitingCardActivity.this, R.string.profile_update_failed, Toast.LENGTH_SHORT).show())
                .addOnCanceledListener(() -> Toast.makeText(VisitingCardActivity.this, R.string.profile_update_canceled, Toast.LENGTH_SHORT).show());
    }

    /**
     * Setup ClickListeners here so they don't clutter {@link #onCreate(Bundle)} code
     */
    private void setListeners() {
        fab.setOnClickListener(view -> uploadData());
        fab.setOnLongClickListener(view -> {
            Toast.makeText(this, R.string.save_changes, Toast.LENGTH_SHORT).show();
            return true;
        });

        buttonOpenMap.setOnClickListener(view -> {
            Intent i = new Intent(VisitingCardActivity.this, MapsActivity.class);
            startActivity(i);
        });

        profileImage.setOnClickListener(view -> selectProfileImage());
    }

    /**
     * Creates unique filename for the profile image.
     *
     * @return
     */
    private String createFilename() {
        Date date = new Date();
        long timestamp = date.getTime() / 100;

        String filename = user.getDisplayName() + "_" + timestamp;
        return filename;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle picking the new profile image
        if ((requestCode == RC_PICK_IMAGE) && (resultCode == RESULT_OK)) {
            if ((data != null) && (data.getData() != null)) {
                Uri path = data.getData();
                uploadImage(path);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(VisitingCardActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
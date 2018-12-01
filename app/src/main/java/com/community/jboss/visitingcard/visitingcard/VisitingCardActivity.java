package com.community.jboss.visitingcard.visitingcard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.community.jboss.visitingcard.maps.MapsActivity;
import com.community.jboss.visitingcard.networking.APIClient;
import com.community.jboss.visitingcard.networking.VisitingCard;
import com.community.jboss.visitingcard.R;
import com.community.jboss.visitingcard.SettingsActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class VisitingCardActivity extends AppCompatActivity {

    public static final String TAG = "VisitingCardActivity";
    public static final int RC_PICK_IMAGE = 9002;
    private static final String API_URL = "https://visitingcard.com/api/"; // Dummy url

    private VisitingCard visitingCard;
    private Retrofit retrofit;

    @BindView(R.id.editText_name) public EditText name;
    @BindView(R.id.editText_surname) public EditText surname;
    @BindView(R.id.editText_email) public EditText email;
    @BindView(R.id.editText_phone) public EditText phone;
    @BindView(R.id.imageView_profileImage) public ImageView profileImage;
    @BindView(R.id.button_map) public Button openMapButton;
    @BindView(R.id.progressBar) public ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visiting_card);
        ButterKnife.bind(this);

        visitingCard = new VisitingCard();
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();

        // TODO: Add a ImageView and a number of EditText to getVisitingCard his/her Visiting Card details (Currently authenticated User)

        // TODO: Add profileImage, Name, Email, PhoneNumber, Github, LinkedIn & Twitter Fields.

        // TODO: Clicking the ImageView should invoke an implicit intent to take an image using camera / pick an image from the Gallery.

        // TODO: Align FAB to Bottom Right and replace it's icon with a SAVE icon
        // TODO: On Click on FAB should make a network call to store the entered information in the cloud using POST method(Do this in NetworkUtils class)
        FloatingActionButton fabUpload = findViewById(R.id.fab);
        fabUpload.setOnClickListener(view -> {
            setCardData();
            uploadCardData();
        });

        openMapButton.setOnClickListener(view -> {
            Intent intent = new Intent(VisitingCardActivity.this, MapsActivity.class);
            startActivity(intent);
        });
        profileImage.setOnClickListener(view -> selectProfileImage());
    }

    private void uploadCardData() {
        APIClient client = retrofit.create(APIClient.class);
        Call<VisitingCard> call = client.uploadVisitingCard(visitingCard);

        call.enqueue(new Callback<VisitingCard>() {
            @Override
            public void onResponse(Call<VisitingCard> call, Response<VisitingCard> response) {
                Toast.makeText(VisitingCardActivity.this, R.string.post_success, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<VisitingCard> call, Throwable t) {
                Toast.makeText(VisitingCardActivity.this, R.string.post_failure, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCardData() {
        visitingCard.setName(name.getText().toString());
        visitingCard.setSurname(surname.getText().toString());
        visitingCard.setEmail(email.getText().toString());
        visitingCard.setPhoneNumber(phone.getText().toString());

        Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();

        visitingCard.setProfileImage(bitmapToBytes(bitmap));
    }

    private void selectProfileImage() {
        Intent imageIntent = new Intent();
        imageIntent.setType("image/*");
        imageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(imageIntent, getString(R.string.select_profile_image)), RC_PICK_IMAGE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RC_PICK_IMAGE && resultCode == RESULT_OK) {
            if ((data != null) && (data.getData() != null)) {
                try {
                    InputStream stream = getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(stream);
                    stream.close();

                    profileImage.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private Bitmap bytesToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}

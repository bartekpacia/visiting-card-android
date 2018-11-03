package com.community.jboss.visitingcard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.community.jboss.visitingcard.VisitingCard.VisitingCardActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1001;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    //List of authentication providers
    List<AuthUI.IdpConfig> providers;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.imageView_profilePhoto) ImageView imageView_profilePhoto;
    @BindView(R.id.textView_username) TextView textView_username;
    @BindView(R.id.textView_email) TextView textView_email;
    @BindView(R.id.button_signOut) Button button_signOut;
    @BindView(R.id.button_signIn) Button button_signIn;
    @BindView(R.id.userInfoLayout) LinearLayout userInfoLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        userInfoLayout.setVisibility(View.INVISIBLE);
        button_signIn.setVisibility(View.GONE);
        button_signOut.setVisibility(View.GONE);

        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        if (firebaseAuth.getCurrentUser() != null) {
            setLayoutReady();
        } else {
            launchSignInIntent();
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Intent toVisitingCard = new Intent(LoginActivity.this, VisitingCardActivity.class);
                    startActivity(toVisitingCard);
                } else Toast.makeText(LoginActivity.this, getString(R.string.login_first), Toast.LENGTH_SHORT).show();
            }
        });

        button_signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance().
                        signOut(LoginActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(LoginActivity.this, getString(R.string.signed_out), Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                            }
                        });
            }
        });

        button_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSignInIntent();
            }
        });
    }

    private void launchSignInIntent() {
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                //Successfully signed in
                setLayoutReady();
            } else {
                //An error occurred
                Toast.makeText(this, getString(R.string.sign_in_failed), Toast.LENGTH_SHORT).show();
                setLayoutLoggedOut();
            }
        }
    }

    /**
     * Invoke this when everything went fine. It displays basic user information.
     */
    private void setLayoutReady() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        Toast.makeText(this, "Hello, " + user.getDisplayName(), Toast.LENGTH_SHORT).show();

        //Load user profile photo
        if (user.getPhotoUrl() != null) {
            //This is a hack to get user photo in higher resoulution
            String photoUrl = user.getPhotoUrl().toString();
            String hiResPhotoUrl = photoUrl.replace("s96-c", "s384-c");

            Picasso.get()
                    .load(hiResPhotoUrl)
                    .resize(384, 384)
                    .into(imageView_profilePhoto);
        } else {
            Picasso.get()
                    .load(R.drawable.ic_person)
                    .resize(384, 384)
                    .into(imageView_profilePhoto);
        }

        textView_username.setText(user.getDisplayName());
        textView_email.setText(user.getEmail());

        userInfoLayout.setVisibility(View.VISIBLE);
        button_signOut.setVisibility(View.VISIBLE);
        button_signIn.setVisibility(View.GONE);
    }


    /**
     * Invoke this when user isn't signed in. It displays a button to sign in.
     */
    private void setLayoutLoggedOut() {
        userInfoLayout.setVisibility(View.GONE);
        button_signOut.setVisibility(View.GONE);
        button_signIn.setVisibility(View.VISIBLE);
    }
}

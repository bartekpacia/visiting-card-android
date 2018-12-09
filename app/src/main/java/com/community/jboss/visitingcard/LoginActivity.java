package com.community.jboss.visitingcard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.community.jboss.visitingcard.VisitingCard.VisitingCardActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private List<AuthUI.IdpConfig> authProviders;

    @BindView(R.id.toolbar) public Toolbar toolbar;
    @BindView(R.id.fab) public FloatingActionButton fab;
    @BindView(R.id.textView_authStatus) public TextView textView_authStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        textView_authStatus.setVisibility(View.INVISIBLE);

        authProviders = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build()
        );

        fab.setOnClickListener(view -> Snackbar.make(view, "Proceed to Visiting Card Layout", Snackbar.LENGTH_LONG)
                .setAction("Yes", snackView -> proceedIfSignedIn()).show());

        //TODO Add possibility to logout
        proceedIfSignedIn();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                user = auth.getCurrentUser();
                textView_authStatus.setVisibility(View.VISIBLE);
                Intent i = new Intent(this, VisitingCardActivity.class);
                startActivity(i);
            } else {
                textView_authStatus.setVisibility(View.INVISIBLE);
                Toast.makeText(this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void proceedIfSignedIn() {
        if (user != null) {
            textView_authStatus.setVisibility(View.VISIBLE);
            Intent i = new Intent(this, VisitingCardActivity.class);
            startActivity(i);
        } else {
            textView_authStatus.setVisibility(View.INVISIBLE);
            startActivityForResult(createSignInIntent(), RC_SIGN_IN);
        }
    }

    private Intent createSignInIntent() {
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(authProviders)
                .build();

        return signInIntent;
    }

}

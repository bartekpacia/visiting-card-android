package com.community.jboss.visitingcard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.community.jboss.visitingcard.VisitingCard.VisitingCardActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.fade_in_linear, R.anim.fade_out_linear);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean darkThemeEnabled = preferences.getBoolean(getString(R.string.PREF_DARK_THEME), false);
        if(darkThemeEnabled)
        {
            setTheme(R.style.AppTheme_Dark_NoActionBar);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TODO: Perform Firebase Authentication using Email Auth or Google Signin.
        // TODO: Have a Signin with google Button.

        //TODO: Move the FAB to bottom Right and replace it's icon with a check icon
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Proceed to Visiting Card Layout", Snackbar.LENGTH_LONG)
                        .setAction("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // TODO: Go to next stage only when the User is Authenticated.
                                Intent toVisitingCard = new Intent(LoginActivity.this, VisitingCardActivity.class);
                                startActivity(toVisitingCard);
                            }
                        }).show();
            }
        });
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
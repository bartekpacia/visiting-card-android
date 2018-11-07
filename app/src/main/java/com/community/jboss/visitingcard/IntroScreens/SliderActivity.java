package com.community.jboss.visitingcard.IntroScreens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.community.jboss.visitingcard.LoginActivity;
import com.community.jboss.visitingcard.R;
import com.community.jboss.visitingcard.VisitingCard.VisitingCardActivity;

public class SliderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.fade_in_linear, R.anim.fade_out_linear);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean darkThemeEnabled = preferences.getBoolean(getString(R.string.PREF_DARK_THEME), false);
        if(darkThemeEnabled)
        {
            setTheme(R.style.AppTheme_Dark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        //TODO: The Slider should appear only on when the app is launched for the first time. - Add appropriate conditions for that.

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Proceed to Login", Snackbar.LENGTH_LONG)
                        .setAction("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent toLogin = new Intent(SliderActivity.this, LoginActivity.class);
                                startActivity(toLogin);
                            }
                        }).show();
            }
        });

        // TODO: Create Introduction slides explaining all the functionalities of the app here.

        // TODO: if you're creating an Adapter for the ViewPager create it in the same Package and name it as SlideAdapter

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

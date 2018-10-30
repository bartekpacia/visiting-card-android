package com.community.jboss.visitingcard.IntroScreens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.community.jboss.visitingcard.LoginActivity;
import com.community.jboss.visitingcard.R;
import com.community.jboss.visitingcard.VisitingCard.VisitingCardActivity;
import com.hololo.tutorial.library.Step;
import com.hololo.tutorial.library.TutorialActivity;

public class SliderActivity extends TutorialActivity {

    String PREFERENCES_NAME;
    String PREFERENCES_KEY;
    boolean firstUse;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PREFERENCES_NAME = getString(R.string.SHARED_PREFS_NAME);
        PREFERENCES_KEY = getString(R.string.FIRST_USE_PREFERENCE_KEY);

        preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        firstUse = preferences.getBoolean(PREFERENCES_KEY, true);
        
        if (!firstUse) {
            Intent intent = new Intent(SliderActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else {
            createInfoSlider();
        }
    }

    private void createInfoSlider() {
        addFragment(new Step.Builder().setTitle(getString(R.string.slide_1_header))
                .setContent(getString(R.string.slide_1_content))
                .setBackgroundColor(Color.parseColor("#ffbb00"))
                .setDrawable(R.drawable.ic_hello_vc) //ADD SVG
                .build());
        addFragment(new Step.Builder().setTitle(getString(R.string.slide_2_header))
                .setContent(getString(R.string.slide_2_content))
                .setBackgroundColor(Color.parseColor("#7cbb00"))
                .setDrawable(R.drawable.ic_cards)
                .build());
        addFragment(new Step.Builder().setTitle(getString(R.string.slide_3_header))
                .setContent(getString(R.string.slide_3_content))
                .setBackgroundColor(Color.parseColor("#00a1f1"))
                .setDrawable(R.drawable.ic_map_marker)
                .build());
    }

    @Override
    public void finishTutorial() {
        super.finishTutorial();

        //Set firstUse to false so Info Slider won't show up again
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREFERENCES_KEY, false);
        editor.apply();

        Intent intent = new Intent(SliderActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}

package net.glassstones.bambammusic.ui.activities;

import android.os.Bundle;
import android.app.Activity;

import net.glassstones.bambammusic.R;

public class ProfileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

}

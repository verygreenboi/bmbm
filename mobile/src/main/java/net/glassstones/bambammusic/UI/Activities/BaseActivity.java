package net.glassstones.bambammusic.ui.activities;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.glassstones.bambammusic.R;
import net.glassstones.bambammusic.intefaces.FragmentInteraction;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity implements FragmentInteraction {

    public abstract int contentResource();

    public abstract boolean hasToolBar();

    public abstract Toolbar toolbar();

    public abstract boolean hasTitle();

    public abstract boolean hasIcon();

    public abstract String title();

    public abstract Drawable icon();

    public abstract boolean hasFab();

    public abstract FloatingActionButton fab();

    public abstract View.OnClickListener onClickListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(contentResource());
        ButterKnife.bind(this);

        if (hasToolBar()) {
            assert getSupportActionBar() != null;
            setSupportActionBar(toolbar());

            if (hasTitle()) {
                TextView title = (TextView) toolbar().findViewById(R.id.tb_title);
                title.setText(title());
                title.setVisibility(View.VISIBLE);
            }

            if (hasIcon()) {
                ImageView icon = (ImageView) toolbar().findViewById(R.id.tb_icon);
                icon.setImageDrawable(icon());
            }


        }

        if (hasFab()) {
            fab().setOnClickListener(onClickListener());
        }

    }
}

package net.glassstones.bambammusic;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;

import net.glassstones.bambammusic.ui.activities.AddCardActivity;
import net.glassstones.bambammusic.ui.fragments.ListCardsFragment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by Thompson on 02/04/2016.
 * For BambamMusic
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class AddCardActivityTest {

    @Test
    public void clickingFabShouldStartAddCardFragment(){
        AddCardActivity activity = Robolectric.setupActivity(AddCardActivity.class);
        FloatingActionButton fab = (FloatingActionButton) activity.findViewById(R.id.fab);
        fab.performClick();
        FragmentManager fm = activity.getSupportFragmentManager();
        assert fm.beginTransaction().replace(R.id.container, new ListCardsFragment()) != null;
    }

}

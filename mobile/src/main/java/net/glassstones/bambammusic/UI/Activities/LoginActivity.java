package net.glassstones.bambammusic.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import net.glassstones.bambammusic.R;
import net.glassstones.bambammusic.models.MediaData;

import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;

public class LoginActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.loginButton)
    Button mLoginButton;
    @Bind(R.id.tos)
    TextView mTos;

    @Override
    public int contentResource() {
        return R.layout.activity_login;
    }

    @Override
    public boolean hasToolBar() {
        return false;
    }

    @Override
    public Toolbar toolbar() {
        return null;
    }

    @Override
    public boolean hasTitle() {
        return false;
    }

    @Override
    public boolean hasIcon() {
        return false;
    }

    @Override
    public String title() {
        return null;
    }

    @Override
    public Drawable icon() {
        return null;
    }

    @Override
    public boolean hasFab() {
        return false;
    }

    @Override
    public FloatingActionButton fab() {
        return null;
    }

    @Override
    public View.OnClickListener onClickListener() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "net.glassstones.bambammusic.debug",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
//            Log.e("TAG", e.getMessage());
//        }

        mLoginButton.setAlpha(0f);

        mLoginButton.setEnabled(false);

        mLoginButton.animate().alpha(1f).setDuration(2000).start();

        ObjectAnimator titleAnim = ObjectAnimator.ofFloat(mTitle, View.TRANSLATION_Y, -250);
        titleAnim.setDuration(1500);
        titleAnim.setInterpolator(new LinearInterpolator());

        ObjectAnimator animator = ObjectAnimator.ofFloat(mLoginButton, View.TRANSLATION_Y, -150);
        animator.setDuration(1500);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!mLoginButton.isEnabled()) {
                    mLoginButton.setEnabled(true);
                    mLoginButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onLoginButtonClicked();
                        }
                    });
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.playTogether(titleAnim, animator);
        animatorSet.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void onLoginButtonClicked() {
        List<String> permissions = Collections.singletonList("public_profile");
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    if (user.isNew()) {
                        makeMeRequest(user);
                    } else {
                        finishActivity();
                    }
                }
            }
        });
    }

    private void makeMeRequest(final ParseUser parseUser) {
        parseUser.put("has_username", false);

        GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(final JSONObject user, GraphResponse response) {
                parseUser.put("name", user.optString("name"));
                parseUser.put("fb_id", user.optString("id"));
                parseUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            finishActivity();
                        }
                    }
                });

            }
        }).executeAsync();
    }

    private void finishActivity() {
        // Start an intent for the dispatch activity
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void sendMediaData(MediaData mediaData) {

    }

    @Override
    public void sendCurentPlayPosition(int currentPosition) {

    }
}

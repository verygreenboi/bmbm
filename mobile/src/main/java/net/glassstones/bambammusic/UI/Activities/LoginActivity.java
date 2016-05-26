package net.glassstones.bambammusic.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import net.glassstones.bambammusic.R;
import net.glassstones.bambammusic.models.MediaData;
import net.glassstones.library.utils.LogHelper;

import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.loginButton)
    Button mLoginButton;
    @Bind(R.id.tos)
    TextView mTos;
    @Bind(R.id.username_wrap)
    LinearLayout mUsernameWrap;
    @Bind(R.id.username_et)
    EditText mUsername;
    @Bind(R.id.confirm_username_Button)
    Button mConfirmBtn;

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

        mUsernameWrap.setAlpha(0f);

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
                mUsernameWrap.setTranslationY(-250);
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
                if (user != null && e == null) {
                    if (user.isNew()) {
                        makeMeRequest(user);
                    } else {
                        if (user.getBoolean("has_username"))
                            finishActivity();
                        else
                            setUsername(user);
                    }
                } else {
                    e.printStackTrace();
                    LogHelper.e("TAG", !TextUtils.isEmpty(e.getMessage()) ? e.getMessage() : "Fail");
                }
            }
        });
    }

    private void setUsername(ParseUser user) {
        makeMeRequest(user);
    }

    private void makeMeRequest(final ParseUser parseUser) {
        if (parseUser.getBoolean("has_username")) {
            finishActivity();
        }
        mLoginButton.setVisibility(View.GONE);
        LogHelper.i(TAG, "Here");
        mUsernameWrap.setAlpha(1f);
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = mUsername.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    Snackbar.make(mUsernameWrap, "Please enter a username.", Snackbar.LENGTH_LONG).show();
                } else {
                    final ParseQuery<ParseUser> user = ParseUser.getQuery();
                    user.whereEqualTo("username", username.toLowerCase());
                    user.getFirstInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser object, ParseException e) {
                            if (e == null) {
                                Snackbar.make(mUsernameWrap, "Username taken. Try another one", Snackbar.LENGTH_LONG).show();
                            } else {
                                if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                                    saveUser(parseUser, username);
                                } else {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void saveUser(final ParseUser parseUser, final String username) {
        GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(final JSONObject user, GraphResponse response) {
                parseUser.put("name", user.optString("name"));
                parseUser.put("fb_id", user.optString("id"));
                parseUser.put("f_username", username);
                parseUser.setUsername(username.toLowerCase());
                parseUser.put("has_username", true);
                // 1. Instantiate an AlertDialog.Builder with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage("Saving user");
                final AlertDialog dialog = builder.create();
                dialog.show();
                parseUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        dialog.dismiss();
                        if (e == null) {
                            finishActivity();
                        } else {
                            Snackbar.make(mUsernameWrap, "Oops!!! Something bad happened", Snackbar.LENGTH_LONG).show();
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

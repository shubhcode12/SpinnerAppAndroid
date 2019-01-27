package sol.earningapp.smtech.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adefruandta.spinningwheel.SpinningWheelView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.Random;

import sol.earningapp.smtech.R;
import sol.earningapp.smtech.managers.LoginManager;
import sol.earningapp.smtech.managers.SpinManager;
import sol.earningapp.smtech.models.Score;
import sol.earningapp.smtech.models.User;
import sol.earningapp.smtech.utils.AESCrypt;
import sol.earningapp.smtech.utils.ConnectionChecker;
import sol.earningapp.smtech.utils.SharedPreferenceValue;
import sol.earningapp.smtech.utils.StaticAccess;

public class SpinActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SpinManager.SpinFeedbackListener, LoginManager.LoginFeedbackListener {
    private AdView mAdViewTop;
    private AdView mAdViewBottom, mAdViewBottom2;
    private SpinningWheelView wheelView;
    private RelativeLayout rotate;
    private TextView tvSpinRemain, tvTotalPoint;
    private SpinManager spinManager;
    private SpinActivity activity;
    private int spinRemain = 12;
    private String totalPoint = "0";
    private InterstitialAd mInterstitialAd;
    private LoginManager loginManager;
    private Button btnWithdrawRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin);
        activity = this;
        loginManager = new LoginManager(activity, false, this);

        MobileAds.initialize(this, getString(R.string.admob_id));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.per_spin_ads));
        spinManager = new SpinManager(activity, false, this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        btnWithdrawRequest = findViewById(R.id.btnWithdrawRequest);
        tvSpinRemain = findViewById(R.id.tvSpinRemain);
        tvTotalPoint = findViewById(R.id.tvTotalPoint);
        mAdViewTop = findViewById(R.id.adView);
        mAdViewBottom = findViewById(R.id.adView2);
        mAdViewBottom2 = findViewById(R.id.adView3);

        initUI();
        if (ConnectionChecker.isOnline(activity)) {
            spinManager.getUserLastScore(SharedPreferenceValue.getLoggedinUser(activity));
            loginManager.getUserByID(SharedPreferenceValue.getLoggedinUser(activity));
        } else
            Toast.makeText(activity, "No internet connection available", Toast.LENGTH_SHORT).show();
        setWithdrawButtonListener();
    }

    private void setWithdrawButtonListener() {

        btnWithdrawRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, WithdrawRequestActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAds();
        showAdds();

    }

    private void loadAds() {
//        ads:adUnitId="ca-app-pub-4836336224499913/6703144349"></com.google.android.gms.ads.AdView>
        //mAdViewTop.setAdUnitId("ca-app-pub-4836336224499913/6703144349");

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.spin, menu);
        return true;
    }*/
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_Profile) {

            startActivity(new Intent(SpinActivity.this, ProfileActivity.class));
            // Handle the camera action
        } else if (id == R.id.nav_Dashboard) {
            startActivity(new Intent(SpinActivity.this, MainActivity.class));

        } else if (id == R.id.nav_Share) {
            shareAppLink();

        } else if (id == R.id.nav_Withdraw) {
            startActivity(new Intent(SpinActivity.this, WithdrawActivity.class));

        } else if (id == R.id.nav_Logout) {
            SharedPreferenceValue.clearLoggedInuserData(SpinActivity.this);
            startActivity(new Intent(SpinActivity.this, LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void shareAppLink() {
        final String appPackageName = getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Download App");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello i am using Hot Paisa- spin for real Cash app download it from play store , and put my number as referral "
                + payTimeMobile + " Check out the App at: https://play.google.com/store/apps/details?id=" + appPackageName);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void initUI() {
        wheelView = (SpinningWheelView) findViewById(R.id.wheel);
        rotate = (RelativeLayout) findViewById(R.id.rotate);
        // Can be array string or list of object
        wheelView.setItems(R.array.dummy2);
        wheelView.setEnabled(false);
        // Set listener for rotation event
        wheelView.setOnRotationListener(new SpinningWheelView.OnRotationListener<String>() {
            // Call once when start rotation
            @Override
            public void onRotation() {
                Log.d("XXXX", "On Rotation");

            }

            // Call once when stop rotation
            @Override
            public void onStopRotation(String item) {
                Log.d("XXXX", "On Rotation");
                spinRemain--;
                int pointsOfToDay = SharedPreferenceValue.getTodayPointLimit(activity);
                pointsOfToDay += Integer.parseInt(item);
                if (pointsOfToDay <= 250) {
                    Score score = new Score();
                    score.setCurrentDate(StaticAccess.getDateTimeNow());
                    score.setScoreID(AESCrypt.getID());
                    score.setSpinRemain(spinRemain);
                    score.setUserID(SharedPreferenceValue.getLoggedinUser(activity));
                    score.setPoints(String.valueOf(Integer.parseInt(totalPoint) + Integer.parseInt(item)));
                    spinManager.savePoints(score);
                    showAdsFinal();
                } else {
                    int diff = pointsOfToDay - 250;
                    pointsOfToDay = pointsOfToDay - diff;
                    Score score = new Score();
                    score.setCurrentDate(StaticAccess.getDateTimeNow());
                    score.setScoreID(AESCrypt.getID());
                    score.setSpinRemain(spinRemain);
                    score.setUserID(SharedPreferenceValue.getLoggedinUser(activity));
                    score.setPoints(String.valueOf(Integer.parseInt(totalPoint) + (Integer.parseInt(item) - diff)));
                    spinManager.savePoints(score);
                    showAdsFinal();
                }
                SharedPreferenceValue.setTodayPointLimit(activity, pointsOfToDay);
            }
        });

        // If true: user can rotate by touch
        // If false: user can not rotate by touch
        //wheelView.setEnabled(true);
        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // max angle 50
                // duration 10 second
                // every 50 ms rander rotation
                if (spinRemain > 0) {
                    wheelView.rotate(50, generateRandom(2000, 3000), generateRandom(30, 60));
                } else {
                    Toast.makeText(activity, "No Spin Remain. Check Tomorrow", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showAdds() {
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                showAdsFinal();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                //mInterstitialAd.loadAd(new AdRequest.Builder().build());

            }
        });

    }

    void showAdsFinal() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }

    private int generateRandom(int min, int max) {
        // max - min + 1 will create a number in the range of min and max, including max. If you don´t want to include it, just delete the +1.
        // adding min to it will finally create the number in the range between min and max
        return new Random().nextInt(max - min + 1) + min;
    }

    @Override
    public void pointSaveSuccess() {
        spinManager.getUserLastScore(SharedPreferenceValue.getLoggedinUser(activity));
    }

    @Override
    public void pointSaveFailed() {
        spinRemain++;
    }

    @Override
    public void noLastPointFound() {
        this.spinRemain = 12;
        tvSpinRemain.setText("Today Remain Spin: " + String.valueOf(spinRemain));
        tvTotalPoint.setText("My Total Points: 0");

    }

    @Override
    public void userLastScore(Score score) {
        tvSpinRemain.setText("Today Remain Spin: " + String.valueOf(score.getSpinRemain()));
        tvTotalPoint.setText("My Total Points: " + score.getPoints());
        this.totalPoint = score.getPoints();
        this.spinRemain = score.getSpinRemain();
    }

    @Override
    public void updateScoreFailed() {

    }

    @Override
    public void updateScoreSuccess() {

    }

    @Override
    public void noScoreForTodayUpdateScoreOnly(Score score) {
        this.totalPoint=score.getPoints();
        tvTotalPoint.setText("My Total Points: " + score.getPoints());
        SharedPreferenceValue.setTodayPointLimit(activity, 0);
    }

    private String payTimeMobile = "";

    @Override
    public void getLoggedinUser(User user) {
        payTimeMobile = user.getMobile();
        if (user.getEmail().equalsIgnoreCase(StaticAccess.ADMIN_EMAIL)) {
            btnWithdrawRequest.setVisibility(View.VISIBLE);
        } else {
            btnWithdrawRequest.setVisibility(View.GONE);
        }
    }

    @Override
    public void noUserFound() {

    }

    @Override
    public void mobileError() {

    }

    @Override
    public void passwordError() {

    }

    @Override
    public void updateSuccess() {

    }

    @Override
    public void updateFailed() {

    }
}

// to check the github repo

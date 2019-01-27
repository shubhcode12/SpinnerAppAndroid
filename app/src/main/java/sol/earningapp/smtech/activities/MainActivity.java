package sol.earningapp.smtech.activities;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.List;

import javax.xml.datatype.Duration;

import sol.earningapp.smtech.R;
import sol.earningapp.smtech.managers.SpinManager;
import sol.earningapp.smtech.managers.WithdrawManager;
import sol.earningapp.smtech.models.Score;
import sol.earningapp.smtech.models.Withdraw;
import sol.earningapp.smtech.utils.ApplicationMode;
import sol.earningapp.smtech.utils.ConnectionChecker;
import sol.earningapp.smtech.utils.SharedPreferenceValue;

public class MainActivity extends AppCompatActivity implements WithdrawManager.WithdrawFeedbackListener {

    private TextView tvTotalEarn, tvLastWithdraw;
    private AdView adView, adView2, adView3;
    private InterstitialAd mInterstitialAd;
    private WithdrawManager withdrawManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    private void initUI() {
        tvTotalEarn = findViewById(R.id.tvTotalEarn);
        tvLastWithdraw = findViewById(R.id.tvLastWithdraw);
        adView = findViewById(R.id.adViewD);
        adView2 = findViewById(R.id.adView2D);
        adView3 = findViewById(R.id.adView3D);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.dashboard_interstitial_ads));
        loadAds();
        showInterstitialAdds();
        if (ConnectionChecker.isOnline(MainActivity.this)) {
            new SpinManager(MainActivity.this, false, new SpinManager.SpinFeedbackListener() {
                @Override
                public void pointSaveSuccess() {

                }

                @Override
                public void pointSaveFailed() {

                }

                @Override
                public void noLastPointFound() {
                    tvTotalEarn.setText("My Total Earn Points: 0");
                }

                @Override
                public void userLastScore(Score score) {
                    tvTotalEarn.setText("My Total Earn Points: " + score.getPoints());

                }

                @Override
                public void updateScoreSuccess() {

                }

                @Override
                public void updateScoreFailed() {

                }

                @Override
                public void noScoreForTodayUpdateScoreOnly(Score score) {
                    tvTotalEarn.setText("My Total Earn Points: " + score.getPoints());

                }
            }).getUserLastScore(SharedPreferenceValue.getLoggedinUser(MainActivity.this));
            withdrawManager = new WithdrawManager(MainActivity.this, false, this);
            withdrawManager.getUserLastWithdraw(SharedPreferenceValue.getLoggedinUser(MainActivity.this));
        } else {
            Toast.makeText(this, "No internet . try to connect with internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAds() {
//        ads:adUnitId="ca-app-pub-4836336224499913/6703144349"></com.google.android.gms.ads.AdView>
        //mAdViewTop.setAdUnitId("ca-app-pub-4836336224499913/6703144349");

        MobileAds.initialize(this, "ca-app-pub-1295679460207480~1411532058"); // Real

        if (ApplicationMode.devMode) {
            AdRequest adRequest = new AdRequest.Builder()
                   // .addTestDevice("35E1AEC74E43C6B3529C4F77AC4CB10F")
                    .build();
            adView.loadAd(adRequest);
        } else {
            AdRequest adRequest = new AdRequest.Builder()
                    //.addTestDevice("35E1AEC74E43C6B3529C4F77AC4CB10F")
                    .build();
            adView.loadAd(adRequest);
        }
        if (ApplicationMode.devMode) {
            AdRequest adRequest2 = new AdRequest.Builder()
                    //.addTestDevice("35E1AEC74E43C6B3529C4F77AC4CB10F")
                    .build();
            adView2.loadAd(adRequest2);
        } else {
            AdRequest adRequest2 = new AdRequest.Builder()
                    //.addTestDevice("35E1AEC74E43C6B3529C4F77AC4CB10F")
                    .build();
            adView2.loadAd(adRequest2);

        }
        if (ApplicationMode.devMode) {
            AdRequest adRequest3 = new AdRequest.Builder()
                   // .addTestDevice("35E1AEC74E43C6B3529C4F77AC4CB10F")
                    .build();
            adView3.loadAd(adRequest3);
        } else {
            AdRequest adRequest3 = new AdRequest.Builder()
                    //.addTestDevice("35E1AEC74E43C6B3529C4F77AC4CB10F")
                    .build();
            adView3.loadAd(adRequest3);

            adView3.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int i) {
                    Toast.makeText(getApplicationContext(),"Error code - " + i, Toast.LENGTH_LONG);

                }
            });

        }
    }

    private void showInterstitialAdds() {
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                //mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showAdsFinal();
            }
        }, 5000);
    }

    void showAdsFinal() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }

    @Override
    public void withdrawSaveSuccess() {

    }

    @Override
    public void withdrawSaveFailed() {

    }

    @Override
    public void noLastWithdrawFound() {
        tvLastWithdraw.setText("My Last Withdraw: 0");
    }

    @Override
    public void userLastWithdraw(Withdraw withdraw) {
        tvLastWithdraw.setText("My Last Withdraw: " + withdraw.getWithdrawPoints());


    }

    @Override
    public void withDrawRequests(List<Withdraw> withdrawList) {

    }

    @Override
    public void noRequestsAvailable() {

    }
}

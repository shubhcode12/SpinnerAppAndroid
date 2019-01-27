package sol.earningapp.smtech.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.List;

import sol.earningapp.smtech.R;
import sol.earningapp.smtech.managers.SpinManager;
import sol.earningapp.smtech.managers.WithdrawManager;
import sol.earningapp.smtech.models.Score;
import sol.earningapp.smtech.models.Withdraw;
import sol.earningapp.smtech.utils.AESCrypt;
import sol.earningapp.smtech.utils.ApplicationMode;
import sol.earningapp.smtech.utils.ConnectionChecker;
import sol.earningapp.smtech.utils.SharedPreferenceValue;
import sol.earningapp.smtech.utils.StaticAccess;

public class WithdrawActivity extends AppCompatActivity implements View.OnClickListener, WithdrawManager.WithdrawFeedbackListener {

    private Button btnWithdraw;
    private TextView tvTotalEarn;
    private EditText etPoints;
    private WithdrawActivity activity;
    private WithdrawManager withdrawManager;
    private AdView adView;
    private InterstitialAd mInterstitialAd;
    private Score lastScore = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        initUI();
    }

    private void initUI() {
        activity = this;
        btnWithdraw = findViewById(R.id.btnWithdraw);
        tvTotalEarn = findViewById(R.id.tvTotalEarn);
        etPoints = findViewById(R.id.etPoints);
        adView = findViewById(R.id.adViewWithdraw);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.withdraw_activity_interestitial_ads));
        btnWithdraw.setOnClickListener(this);
        loadAds();
        showInterstitialAdds();
        withdrawManager = new WithdrawManager(activity, false, this);
        getLastScore();
    }

    private void getLastScore() {
        if (ConnectionChecker.isOnline(activity)) {
            new SpinManager(activity, false, new SpinManager.SpinFeedbackListener() {
                @Override
                public void pointSaveSuccess() {
                }

                @Override
                public void pointSaveFailed() {

                }

                @Override
                public void noLastPointFound() {
                    tvTotalEarn.setText("My Total Earn Points: 0");
                    showAdsFinal();
                }

                @Override
                public void userLastScore(Score score) {
                    tvTotalEarn.setText("My Total Earn Points: " + score.getPoints());
                    showAdsFinal();
                    lastScore = score;
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
                    lastScore = score;

                }
            }).getUserLastScore(SharedPreferenceValue.getLoggedinUser(activity));
        } else {
            Toast.makeText(this, "No internet . try to connect with internet", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnWithdraw:
                makeWithdraw();
                break;
        }
    }

    private void makeWithdraw() {
        if (ConnectionChecker.isOnline(activity))
            if (TextUtils.isEmpty(etPoints.getText().toString())) {

            } else {
                if (lastScore != null) {
                    long withdrawAmount = Long.parseLong(etPoints.getText().toString());
                    long availableAmount = Long.parseLong(lastScore.getPoints());
                    if (withdrawAmount > availableAmount) {
                        Toast.makeText(activity, "Your withdraw point must <= available points", Toast.LENGTH_SHORT).show();
                    } else {
                        if (withdrawAmount >= StaticAccess.MINIMUM_WITHDRAW_AMOUNT) {
                            Withdraw withdraw = new Withdraw();
                            withdraw.setWithdrawID(AESCrypt.getID());
                            withdraw.setUserID(SharedPreferenceValue.getLoggedinUser(activity));
                            withdraw.setPaid(false);
                            withdraw.setWithdrawPoints(String.valueOf(withdrawAmount));
                            withdraw.setWithdrawDate(StaticAccess.getDateTimeNow());
                            withdrawManager.saveWithdraw(withdraw);
                            long pointsRemain = availableAmount - withdrawAmount;
                            lastScore.setPoints(String.valueOf(pointsRemain));
                        } else {
                            Toast.makeText(activity, "You must be reach 2000 point to withdraw", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {

                }
            }
        else
            Toast.makeText(activity, "No internet connection available", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void withdrawSaveSuccess() {
        if (lastScore != null)
            new SpinManager(activity, true, new SpinManager.SpinFeedbackListener() {
                @Override
                public void pointSaveSuccess() {

                }

                @Override
                public void pointSaveFailed() {

                }

                @Override
                public void noLastPointFound() {

                }

                @Override
                public void userLastScore(Score score) {

                }

                @Override
                public void updateScoreFailed() {
                    Toast.makeText(activity, "Error while processing the request", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void updateScoreSuccess() {
                    Toast.makeText(activity, "Withdraw request submit successfully", Toast.LENGTH_SHORT).show();
                    getLastScore();
                }

                @Override
                public void noScoreForTodayUpdateScoreOnly(Score score) {

                }
            }).updateScore(lastScore);
        else {

        }
    }

    @Override
    public void withdrawSaveFailed() {
        Toast.makeText(activity, "Error while processing the request", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void noLastWithdrawFound() {

    }

    @Override
    public void userLastWithdraw(Withdraw withdraw) {

    }

    @Override
    public void withDrawRequests(List<Withdraw> withdrawList) {

    }

    @Override
    public void noRequestsAvailable() {

    }

    private void loadAds() {
//        ads:adUnitId="ca-app-pub-4836336224499913/6703144349"></com.google.android.gms.ads.AdView>
        //mAdViewTop.setAdUnitId("ca-app-pub-4836336224499913/6703144349");
        if (ApplicationMode.devMode) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("35E1AEC74E43C6B3529C4F77AC4CB10F")
                    .build();
            adView.loadAd(adRequest);
        } else {
            AdRequest adRequest = new AdRequest.Builder()
                    //.addTestDevice("35E1AEC74E43C6B3529C4F77AC4CB10F")
                    .build();
            adView.loadAd(adRequest);
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

    }

    void showAdsFinal() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }
}

package sol.earningapp.smtech.managers;

import android.app.ProgressDialog;
import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import sol.earningapp.smtech.models.Score;
import sol.earningapp.smtech.utils.StaticAccess;

public class SpinManager {
    FirebaseDatabase db;
    DatabaseReference scoreTbl;
    ProgressDialog progressDialog;
    Context mContext;
    private boolean visibleProgress = false;
    private SpinFeedbackListener spinFeedbackListener;

    public SpinManager(Context mContext, boolean visibleProgress, SpinFeedbackListener spinFeedbackListener) {
        this.mContext = mContext;
        this.visibleProgress = visibleProgress;
        this.spinFeedbackListener = spinFeedbackListener;
        db = FirebaseDatabase.getInstance();
        scoreTbl = db.getReference(StaticAccess.SCORES_TABLE);
        progressDialog = new ProgressDialog(mContext);
    }

    //@insert point of a user
    public void savePoints(Score score) {
        if (visibleProgress) {
            showProgress();
        }
        scoreTbl.push().setValue(score, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                    hideProgress();
                    spinFeedbackListener.pointSaveFailed();

                } else {
                    System.out.println("Data saved successfully.");
                    hideProgress();
                    spinFeedbackListener.pointSaveSuccess();
                }

            }
        });
    }


    public void getUserLastScore(final long userID) {
        final List<Score> scoreListToday = new ArrayList<>();
        final List<Score> scoreListALl = new ArrayList<>();
        if (visibleProgress) {
            showProgress();
        }
        scoreTbl.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.getValue(Score.class).getUserID() == userID)
                            scoreListALl.add(ds.getValue(Score.class));
                        if (ds.getValue(Score.class).getUserID() == userID && ds.getValue(Score.class).getCurrentDate().equalsIgnoreCase(StaticAccess.getDateTimeNow()))
                            scoreListToday.add(ds.getValue(Score.class));
                    }
                    if (scoreListToday.size() > 0) {
                        spinFeedbackListener.userLastScore(scoreListToday.get(scoreListToday.size() - 1));
                        hideProgress();
                    } else {
                        spinFeedbackListener.noLastPointFound();
                        if (scoreListALl.size() > 0){
                            spinFeedbackListener.noScoreForTodayUpdateScoreOnly(scoreListALl.get(scoreListALl.size() - 1));
                        }
                            hideProgress();
                    }
                    // hideProgress();
                } else {
                    spinFeedbackListener.noLastPointFound();
                    hideProgress();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgress();
            }
        });
    }

    public void showProgress() {
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

    }

    public void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void updateScore(final Score lastScore) {
        if (visibleProgress) {
            showProgress();
        }
        Query query = scoreTbl.orderByChild("scoreID");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Score scoreDetect = snapshot.getValue(Score.class);
                    if (scoreDetect.getScoreID() == lastScore.getScoreID()) {
                        snapshot.getRef().child("points").setValue(lastScore.getPoints());
                    }
                }
                spinFeedbackListener.updateScoreSuccess();
                hideProgress();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
                spinFeedbackListener.updateScoreFailed();
                hideProgress();

            }
        });
    }

    public interface SpinFeedbackListener {
        void pointSaveSuccess();

        void pointSaveFailed();

        void noLastPointFound();

        void userLastScore(Score score);

        void updateScoreSuccess();

        void updateScoreFailed();

        void noScoreForTodayUpdateScoreOnly(Score score);
    }


}

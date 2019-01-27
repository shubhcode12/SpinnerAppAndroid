package sol.earningapp.smtech.managers;

import android.app.ProgressDialog;
import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import sol.earningapp.smtech.models.Score;
import sol.earningapp.smtech.models.Withdraw;
import sol.earningapp.smtech.utils.StaticAccess;

public class WithdrawManager {
    FirebaseDatabase db;
    DatabaseReference withdrawTbl;
    ProgressDialog progressDialog;
    Context mContext;
    private boolean visibleProgress = false;
    private WithdrawFeedbackListener withdrawFeedbackListener;

    public WithdrawManager(Context mContext, boolean visibleProgress, WithdrawFeedbackListener withdrawFeedbackListener) {
        this.mContext = mContext;
        this.visibleProgress = visibleProgress;
        this.withdrawFeedbackListener = withdrawFeedbackListener;
        db = FirebaseDatabase.getInstance();
        withdrawTbl = db.getReference(StaticAccess.WITHDRAW_TABLE);
        progressDialog = new ProgressDialog(mContext);
    }

    //@insert point of a user
    public void saveWithdraw(Withdraw withdraw) {
        if (visibleProgress) {
            showProgress();
        }
        withdrawTbl.push().setValue(withdraw, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                    hideProgress();
                    withdrawFeedbackListener.withdrawSaveFailed();

                } else {
                    System.out.println("Data saved successfully.");
                    hideProgress();
                    withdrawFeedbackListener.withdrawSaveSuccess();
                }

            }
        });
    }

    public void getUserLastWithdraw(final long userID) {
        final List<Withdraw> withdrawList = new ArrayList<>();
        if (visibleProgress) {
            showProgress();
        }
        withdrawTbl.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.getValue(Score.class).getUserID() == userID)
                            withdrawList.add(ds.getValue(Withdraw.class));
                    }
                    if (withdrawList.size() > 0) {
                        withdrawFeedbackListener.userLastWithdraw(withdrawList.get(withdrawList.size() - 1));
                        hideProgress();
                    } else {
                        withdrawFeedbackListener.noLastWithdrawFound();
                        hideProgress();
                    }
                    // hideProgress();
                } else {
                    withdrawFeedbackListener.noLastWithdrawFound();
                    hideProgress();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgress();
            }
        });
    }

    public void getAllWithdrawRequestList() {
        final List<Withdraw> withdrawList = new ArrayList<>();
        if (visibleProgress) {
            showProgress();
        }
        withdrawTbl.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (!ds.getValue(Withdraw.class).isPaid())
                            withdrawList.add(ds.getValue(Withdraw.class));
                    }
                    if (withdrawList.size() > 0) {
                        withdrawFeedbackListener.withDrawRequests(withdrawList);
                        hideProgress();
                    } else {
                        withdrawFeedbackListener.noRequestsAvailable();
                        hideProgress();
                    }
                    // hideProgress();
                } else {
                    withdrawFeedbackListener.noRequestsAvailable();
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

    public interface WithdrawFeedbackListener {
        void withdrawSaveSuccess();

        void withdrawSaveFailed();

        void noLastWithdrawFound();

        void userLastWithdraw(Withdraw withdraw);

        void withDrawRequests(List<Withdraw> withdrawList);

        void noRequestsAvailable();
    }


}

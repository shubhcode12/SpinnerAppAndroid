package sol.earningapp.smtech.activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.List;

import sol.earningapp.smtech.R;
import sol.earningapp.smtech.adapters.UserListAdapter;
import sol.earningapp.smtech.managers.LoginManager;
import sol.earningapp.smtech.managers.WithdrawManager;
import sol.earningapp.smtech.models.User;
import sol.earningapp.smtech.models.Withdraw;

public class WithdrawRequestActivity extends AppCompatActivity implements WithdrawManager.WithdrawFeedbackListener, UserListAdapter.UserClickedListener {
    private RecyclerView rvUserList;
    private UserListAdapter adapter;
    private WithdrawRequestActivity activity;
    private WithdrawManager withdrawManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_request);
        initUI();
    }

    private void initUI() {
        activity = this;
        withdrawManager = new WithdrawManager(activity, true, this);

        rvUserList = (RecyclerView) findViewById(R.id.rvUserList);
        LinearLayoutManager lm = new LinearLayoutManager(activity);
        rvUserList.setLayoutManager(lm);
        rvUserList.setHasFixedSize(true);
        withdrawManager.getAllWithdrawRequestList();
    }

    @Override
    public void withdrawSaveSuccess() {

    }

    @Override
    public void withdrawSaveFailed() {

    }

    @Override
    public void noLastWithdrawFound() {

    }

    @Override
    public void userLastWithdraw(Withdraw withdraw) {

    }

    @Override
    public void withDrawRequests(List<Withdraw> withdrawList) {
        adapter = new UserListAdapter(activity, withdrawList, this);
        rvUserList.setAdapter(adapter);
    }

    @Override
    public void noRequestsAvailable() {
        Toast.makeText(activity, "No Request Available", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserClicked(final Withdraw withdraw) {
        new LoginManager(activity, true, new LoginManager.LoginFeedbackListener() {
            @Override
            public void getLoggedinUser(User user) {
                showUserInfoDialog(user, withdraw.getWithdrawPoints());
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
        }).getUserByID(withdraw.getUserID());
    }

    private void showUserInfoDialog(User user, String withdrawPoints) {
        String userInfo = "Name: " + user.getName() + "\n" +
                "Paytm Mobile: " + user.getMobile() + "\n" +
                "Paypal Email: " + user.getEmail() + "\n" +
                "Withdraw Points: " + withdrawPoints;
        new AlertDialog.Builder(activity)
                .setTitle("User Info")
                .setMessage(userInfo)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}

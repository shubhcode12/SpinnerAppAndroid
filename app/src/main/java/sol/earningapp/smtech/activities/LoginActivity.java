package sol.earningapp.smtech.activities;
/* SmTech Developers */
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;

import sol.earningapp.smtech.R;
import sol.earningapp.smtech.managers.LoginManager;
import sol.earningapp.smtech.models.User;
import sol.earningapp.smtech.utils.ConnectionChecker;
import sol.earningapp.smtech.utils.SharedPreferenceValue;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, LoginManager.LoginFeedbackListener {
    private TextView tvSignup;
    private EditText etCreMobile;
    private EditText etCrePass;
    private Button btnLogin;
    private LoginManager loginManager;
    private LoginActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(LoginActivity.this);
        initUI();
    }
    private void initUI() {
        activity = this;
        tvSignup = findViewById(R.id.tvSignup);
        etCreMobile = findViewById(R.id.etCreMobile);
        etCrePass = findViewById(R.id.etCrePass);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        loginManager = new LoginManager(activity, true, this);
        checkAlreadyLoginOrNot();
    }
    private void checkAlreadyLoginOrNot() {
        if (SharedPreferenceValue.getLoggedinUser(activity) != -1) {
            startDashboard();
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvSignup:
                startLoginActivity();
                break;
            case R.id.btnLogin:
                performLogin();
                break;
        }
    }
    private void performLogin() {
        if (ConnectionChecker.isOnline(activity))
            loginManager.loginUser(etCreMobile.getText().toString(),
                    etCrePass.getText().toString());
        else
            Toast.makeText(activity, "No Internet Connection available", Toast.LENGTH_SHORT).show();
    }
    private void startLoginActivity() {
        startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        finish();
    }
    @Override
    public void getLoggedinUser(User user) {
        if (user != null) {
            SharedPreferenceValue.clearLoggedInuserData(activity);
            SharedPreferenceValue.setLoggedInUser(activity, user.getUserID());
            startDashboard();
        }
    }

    private void startDashboard() {
        startActivity(new Intent(LoginActivity.this, SpinActivity.class));
        finish();
    }

    @Override
    public void noUserFound() {
        Toast.makeText(activity, "Wrong Credential.Login failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void mobileError() {
        etCreMobile.setError(getString(R.string.mobile_required_text));
    }

    @Override
    public void passwordError() {
        etCrePass.setError("Please provide valid password that minimum 6 character");
    }

    @Override
    public void updateFailed() {

    }

    @Override
    public void updateSuccess() {

    }
}

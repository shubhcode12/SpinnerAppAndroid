package sol.earningapp.smtech.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import sol.earningapp.smtech.R;
import sol.earningapp.smtech.managers.SignupManager;
import sol.earningapp.smtech.utils.ConnectionChecker;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener, SignupManager.SignUpFeedbackListener {
    private TextView tvLoginNow;
    private ImageButton ibtnBack;
    private EditText etName, etMobile, etPass, etCfPass, etRef, etEmail;
    private Button ibtnSignUp;
    private SignupManager signupManager;
    private SignupActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initUI();
    }

    // init ui elements
    private void initUI() {
        activity = this;
        signupManager = new SignupManager(activity, true, this);
        tvLoginNow = findViewById(R.id.tvLoginNow);
        ibtnBack = findViewById(R.id.ibtnBack);
        etName = findViewById(R.id.etName);
        etMobile = findViewById(R.id.etMobile);
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);
        etCfPass = findViewById(R.id.etCfPass);
        etRef = findViewById(R.id.etRef);
        ibtnSignUp = findViewById(R.id.ibtnSignUp);


        tvLoginNow.setOnClickListener(this);
        ibtnBack.setOnClickListener(this);
        ibtnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvLoginNow:
                startLoginActivity();
                break;
            case R.id.ibtnBack:
                startLoginActivity();
                break;
            case R.id.ibtnSignUp:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        if (ConnectionChecker.isOnline(activity))
            signupManager.signUpUser(etName.getText().toString(),
                    etMobile.getText().toString(), etEmail.getText().toString(),
                    etPass.getText().toString(), etCfPass.getText().toString(),
                    etRef.getText().toString());
        else
            Toast.makeText(activity, "No internet Connection available", Toast.LENGTH_SHORT).show();
    }

    private void startLoginActivity() {
        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void signUpSuccess() {
        Toast.makeText(activity, "Sign up success.Now you can login To Your Account", Toast.LENGTH_SHORT).show();

        if (TextUtils.isEmpty(etRef.getText().toString())) {

        } else {
            signupManager.checkReferral(etRef.getText().toString());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startLoginActivity();
                }
            }, 2300);
        }
    }

    @Override
    public void signUpFailed() {
        Toast.makeText(activity, "Error Occurred while processing the request", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void nameError() {
        etName.setError("Please provide a valid first name");
    }

    @Override
    public void mobileError() {
        etMobile.setError("Please provide a valid last name");

    }


    @Override
    public void passwordError() {
        etCfPass.setError("Please provide a valid password with minimum >5 character");

    }

    @Override
    public void validationError() {
        Toast.makeText(activity, "Fillup the form properly", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void referrPointAddedToReferrer() {

    }

    @Override
    public void noPointAddedToReferrer() {

    }

    @Override
    public void userAlreadyExists() {
        Toast.makeText(activity, "Mobile Already used. try with another one", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void imeiAlreadyExists() {
        Toast.makeText(activity, "You can not create multiple account form same device.", Toast.LENGTH_SHORT).show();
    }
}

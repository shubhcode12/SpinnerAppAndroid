package sol.earningapp.smtech.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sol.earningapp.smtech.R;
import sol.earningapp.smtech.managers.LoginManager;
import sol.earningapp.smtech.models.User;
import sol.earningapp.smtech.utils.AESCrypt;
import sol.earningapp.smtech.utils.SharedPreferenceValue;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, LoginManager.LoginFeedbackListener {

    private EditText etName, etPass, etMobile, etEmail;
    private CheckBox ckShowPass;
    private Button ibtnUpdate;
    private LoginManager userManager;
    private ProfileActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initUI();
    }

    private void initUI() {
        activity = this;
        userManager = new LoginManager(activity, false, this);
        etName = findViewById(R.id.etName);
        etPass = findViewById(R.id.etUpdatePass);
        etMobile = findViewById(R.id.etMobile);
        etEmail = findViewById(R.id.etEmail);
        ckShowPass = findViewById(R.id.ckShowPass);
        ibtnUpdate = findViewById(R.id.ibtnUpdate);
        ibtnUpdate.setOnClickListener(this);
        ckShowPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ckShowPass.isChecked()) {
                    etPass.setInputType(InputType.TYPE_CLASS_TEXT);
                } else
                    etPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });
        userManager.getUserByID(SharedPreferenceValue.getLoggedinUser(activity));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtnUpdate:
                updateUserInfo();
                break;

        }
    }

    private void updateUserInfo() {
        if (TextUtils.isEmpty(etName.getText().toString()) &&
                TextUtils.isEmpty(etMobile.getText().toString()) &&
                TextUtils.isEmpty(etEmail.getText().toString()) &&
                TextUtils.isEmpty(etPass.getText().toString())) {
            Toast.makeText(activity, "Fill the form properly", Toast.LENGTH_SHORT).show();
        } else {

            if (userID != -1) {
                if (emailValidator(etEmail.getText().toString())) {
                    User user = new User();
                    user.setUserID(userID);
                    user.setName(etName.getText().toString());
                    user.setMobile(etMobile.getText().toString());
                    user.setEmail(etEmail.getText().toString());
                    try {
                        user.setPassword(AESCrypt.encrypt(etPass.getText().toString()));
                        userManager.updateUser(user);
                    } catch (Exception e) {
                        Toast.makeText(activity, "Somthing Wrongs", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(activity, "Please provide a valid email", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity, "Something Wrongs", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean emailValidator(String email) {
        if (TextUtils.isEmpty(email)) {
            return true; //making email optional
        } else {
            Pattern pattern;
            Matcher matcher;
            final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            pattern = Pattern.compile(EMAIL_PATTERN);
            matcher = pattern.matcher(email);
            return matcher.matches();
        }
    }

    private long userID = -1;

    @Override
    public void getLoggedinUser(User user) {
        this.userID = user.getUserID();
        etName.setText(user.getName());
        etMobile.setText(user.getMobile());
        etEmail.setText(user.getEmail() != null ? user.getEmail() : "");
        try {
            etPass.setText(AESCrypt.decrypt(user.getPassword()));
        } catch (Exception e) {

        }
    }

    @Override
    public void noUserFound() {
        Toast.makeText(activity, "Something wrongs", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void mobileError() {

    }

    @Override
    public void passwordError() {

    }

    @Override
    public void updateSuccess() {
        userManager.getUserByID(SharedPreferenceValue.getLoggedinUser(activity));
        Toast.makeText(activity, "Information updated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateFailed() {
        Toast.makeText(activity, "Update failed", Toast.LENGTH_SHORT).show();

    }
}


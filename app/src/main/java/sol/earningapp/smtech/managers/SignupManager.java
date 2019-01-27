package sol.earningapp.smtech.managers;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sol.earningapp.smtech.models.Score;
import sol.earningapp.smtech.models.User;
import sol.earningapp.smtech.utils.AESCrypt;
import sol.earningapp.smtech.utils.StaticAccess;


public class SignupManager {
    FirebaseDatabase db;
    DatabaseReference usersTbl;
    ProgressDialog progressDialog;
    Context mContext;
    private boolean visibleProgress = false;
    private SignUpFeedbackListener signUpFeedbackListener;

    public SignupManager(Context mContext, boolean visibleProgress, SignUpFeedbackListener signUpFeedbackListener) {
        this.mContext = mContext;
        this.visibleProgress = visibleProgress;
        this.signUpFeedbackListener = signUpFeedbackListener;
        db = FirebaseDatabase.getInstance();
        usersTbl = db.getReference(StaticAccess.USERS_TABLE);
        progressDialog = new ProgressDialog(mContext);
    }

    //@signup a user
    public void signUpUser(final String name, String mobile, String email, String password, String cfPassword, String ref) {

        boolean isValid = validateAllField(name, mobile, password, cfPassword);
        if (isValid && emailValidator(email)) {
            User aUser = new User();
            aUser.setUserID(AESCrypt.getID());
            aUser.setName(name);
            aUser.setMobile(mobile);
            aUser.setEmail(email);
            try {
                aUser.setPassword(AESCrypt.encrypt(cfPassword));
                aUser.setReferral(ref);
                aUser.setIMEI(StaticAccess.getIMEI(mContext));
                if (visibleProgress) {
                    showProgress();
                }
                checkMobile(aUser);
            } catch (Exception e) {
                signUpFeedbackListener.signUpFailed();
            }

        } else {
            signUpFeedbackListener.validationError();
        }
    }

    private void checkMobile(final User user) {
        usersTbl.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getValue(User.class).getMobile().equalsIgnoreCase(user.getMobile())) {
                        detectUser = ds.getValue(User.class);
                        hideProgress();
                        break;
                    }
                }
                if (detectUser != null) {
                    signUpFeedbackListener.userAlreadyExists();
                    hideProgress();
                } else {
                    //// no existing user found by the nubmer so make sign up
                    checkIMEI(user);
                }
                // hideProgress();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgress();
            }
        });
    }

    private void checkIMEI(final User user) {
        usersTbl.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getValue(User.class).getIMEI().equalsIgnoreCase(user.getIMEI())) {
                        detectUser = ds.getValue(User.class);
                        hideProgress();
                        break;
                    }
                }
                if (detectUser != null) {
                    signUpFeedbackListener.imeiAlreadyExists();
                    hideProgress();
                } else {
                    //// no existing user found by the nubmer so make sign up
                    signUpNow(user);
                }
                // hideProgress();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgress();
            }
        });
    }

    private void signUpNow(User user) {
        /// actual sign up method
        usersTbl.push().setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                    hideProgress();
                    signUpFeedbackListener.signUpFailed();

                } else {
                    System.out.println("Data saved successfully.");
                    hideProgress();
                    signUpFeedbackListener.signUpSuccess();
                }

            }
        });
    }

    //validate signup user information
    private boolean validateAllField(String name, String mobile, String password, String cfPassword) {
        boolean isNameOK = false;
        boolean isMobile = false;
        boolean isPasswordOK = false;
        if (!TextUtils.isEmpty(name)) {
            isNameOK = true;
        } else {
            isNameOK = false;
            signUpFeedbackListener.nameError();
        }
        if (!TextUtils.isEmpty(mobile)) {
            isMobile = true;
        } else {
            isMobile = false;
            signUpFeedbackListener.mobileError();

        }
        if (passwordValidator(password, cfPassword)) {
            isPasswordOK = true;
        } else {
            isPasswordOK = false;
            signUpFeedbackListener.passwordError();

        }

        if (isNameOK && isMobile && isPasswordOK) {
            return true;
        } else {
            return false;
        }

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

    public boolean emailValidator(String email) {
        if (TextUtils.isEmpty(email)) {
            return true;/// making email optional
        } else {
            Pattern pattern;
            Matcher matcher;
            final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            pattern = Pattern.compile(EMAIL_PATTERN);
            matcher = pattern.matcher(email);
            return matcher.matches();
        }
    }

    public boolean passwordValidator(String password, String cfPassword) {
        boolean isEmptyCheckOK = false;
        boolean isLengthOK = false;
        boolean isBothSame = false;
        if (!TextUtils.isEmpty(password)) {
            isEmptyCheckOK = true;
            if (password.length() > 5 && cfPassword.length() > 5) {
                isLengthOK = true;
                if (password.equalsIgnoreCase(cfPassword)) {
                    isBothSame = true;
                } else {
                    isBothSame = false;
                }
            } else {
                isLengthOK = false;
            }
        } else {
            isEmptyCheckOK = false;
        }

        if (isEmptyCheckOK && isLengthOK && isBothSame) {
            return true;
        } else {
            return false;
        }
    }

    User detectUser = null;

    public void checkReferral(final String referrMobile) {
        if (visibleProgress) {
            showProgress();
        }
        usersTbl.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getValue(User.class).getMobile().equalsIgnoreCase(referrMobile)) {
                        detectUser = ds.getValue(User.class);
                        hideProgress();
                        break;
                    }
                }
                if (detectUser != null) {
                    new SpinManager(mContext, false, new SpinManager.SpinFeedbackListener() {
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
                            long points = Long.parseLong(score.getPoints());
                            score.setPoints(String.valueOf(points + StaticAccess.REFERRAL_POINTS));
                            new SpinManager(mContext, false, new SpinManager.SpinFeedbackListener() {
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
                                public void updateScoreSuccess() {
                                    signUpFeedbackListener.referrPointAddedToReferrer();
                                }

                                @Override
                                public void updateScoreFailed() {
                                    signUpFeedbackListener.noPointAddedToReferrer();
                                }

                                @Override
                                public void noScoreForTodayUpdateScoreOnly(Score score) {

                                }
                            }).updateScore(score);
                        }

                        @Override
                        public void updateScoreSuccess() {

                        }

                        @Override
                        public void updateScoreFailed() {

                        }

                        @Override
                        public void noScoreForTodayUpdateScoreOnly(Score score) {
                            long points = Long.parseLong(score.getPoints());
                            score.setPoints(String.valueOf(points + StaticAccess.REFERRAL_POINTS));
                            new SpinManager(mContext, false, new SpinManager.SpinFeedbackListener() {
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
                                public void updateScoreSuccess() {
                                    signUpFeedbackListener.referrPointAddedToReferrer();
                                }

                                @Override
                                public void updateScoreFailed() {
                                    signUpFeedbackListener.noPointAddedToReferrer();
                                }

                                @Override
                                public void noScoreForTodayUpdateScoreOnly(Score score) {

                                }
                            }).updateScore(score);
                        }
                    }).getUserLastScore(detectUser.getUserID());
                    signUpFeedbackListener.referrPointAddedToReferrer();

                    hideProgress();
                } else {
                    signUpFeedbackListener.noPointAddedToReferrer();
                }
                // hideProgress();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgress();
            }
        });

    }

    public interface SignUpFeedbackListener {
        void signUpSuccess();

        void signUpFailed();

        void nameError();

        void mobileError();

        void passwordError();

        void validationError();

        void referrPointAddedToReferrer();

        void noPointAddedToReferrer();

        void userAlreadyExists();

        void imeiAlreadyExists();
    }


}

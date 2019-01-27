package sol.earningapp.smtech.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class StaticAccess {
    /// TABLE NAMES
    public static final String USERS_TABLE = "Users";
    public static final String SCORES_TABLE = "Scores";
    public static final String WITHDRAW_TABLE = "Withdraw";
    public static final String KEY_FRIENDS_LIST_ACTIVITY = "friend_list";
    public static final long MINIMUM_WITHDRAW_AMOUNT = 2000;
    public static final long REFERRAL_POINTS = 50;
    public static final String ADMIN_EMAIL = "manviarora971@gmail.com";

    public static String getDateTimeNow() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    }

    public static String parseCreatedDate(String time) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(time);
            return new SimpleDateFormat("yyyy-MM-dd").format(date);
        } catch (ParseException e) {
            return "invalid date";
        }
    }

    public static String getMiniFromStartEndTime(String startTime, String endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        try {
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);
            long hour = TimeUnit.MILLISECONDS.toHours(endDate.getTime() - startDate.getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(endDate.getTime() - startDate.getTime());
            long second = TimeUnit.MILLISECONDS.toSeconds(endDate.getTime() - startDate.getTime());
            return String.valueOf(hour + ":" + minutes + ":" + second + " sec");
        } catch (ParseException e) {
            return "00:00:00 sec";
        }

    }

    public static String getIMEI(final Context context) {
        String id = "";
        try {
            final TelephonyManager ts =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return id;
            }
            if (ts != null) {
                id = ts.getDeviceId();
            }
        } catch (Exception e) {

        }
        return id;
    }
}

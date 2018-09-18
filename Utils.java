package com.demoapp.com.demoapp.Utils;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.demoapp.CustomTypefaceSpan;
import com.demoapp.LoginActivity;
import com.demoapp.MainActivity;
import com.demoapp.R;
import com.demoapp.com.demoapp.AsyncTask.Asyntask;
import com.demoapp.com.demoapp.Dialog.LocationEnableDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by user 2 on 2/14/2017.
 */

public class Utils {


    public static boolean isNetworkConnected(Context ctx) {
        if (ctx == null) {
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) ctx.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }
        return false;
    }


    public static void mActionBarNotifactionAllCategory(Context context, String category, String title, String message,
                                                        String conCategory, Bitmap bitrmap, String tabposition) {
        SharedPreferences sp = context.getSharedPreferences("timetrav", Context.MODE_PRIVATE);
        String userid_one = sp.getString("user_id", "");
        String token = sp.getString("user_token", "");
        Intent notificationIntent1 = null;
        if (userid_one.length() > 0) {
            notificationIntent1 = new Intent(context, MainActivity.class);
        } else {
            notificationIntent1 = new Intent(context, LoginActivity.class);
        }
        notificationIntent1
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        notificationIntent1.setAction(Intent.ACTION_MAIN);
        notificationIntent1.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent contentIntent1 = PendingIntent.getActivity(context, 0, notificationIntent1,
                PendingIntent.FLAG_UPDATE_CURRENT);

        long[] vibrate1 = {0, 100, 200, 300};
        Uri uri1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder1 = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.profilenewicon).setLargeIcon(bitrmap).setContentTitle(title)
                .setContentText(message).setVibrate(vibrate1).setSound(uri1).setLights(Color.BLUE, 500, 500);
        mBuilder1.setAutoCancel(true);
        mBuilder1.setContentIntent(contentIntent1);
        NotificationManager mNotificationManager1 = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (category.equals(conCategory)) {
            mNotificationManager1.notify(Integer.parseInt(conCategory), mBuilder1.build());
        }
    }

    public static Typeface mTypeface(Context ctx, int typeface) {
        Typeface font1 = null;
        if (typeface == 1) {
            font1 = Typeface.createFromAsset(ctx.getAssets(), "fonts/Consolas.ttf");
        } else if (typeface == 2) {
            font1 = Typeface.createFromAsset(ctx.getAssets(), "fonts/humanst.ttf");
        } else if (typeface == 3) {
            font1 = Typeface.createFromAsset(ctx.getAssets(), "fonts/roboto_medium.ttf");
        } else if (typeface == 4) {
            font1 = Typeface.createFromAsset(ctx.getAssets(), "fonts/roboto_medium.ttf");
        }
        return font1;
    }

    public static void mColorchange(Context ctx, ActionBar actionBar) {
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setIcon(R.drawable.backarrow_new);
        actionBar.setBackgroundDrawable(ctx.getResources().getDrawable(R.color.colorblack));

        SpannableStringBuilder SS = new SpannableStringBuilder(" ");
        SS.setSpan(new CustomTypefaceSpan("", Utils.mTypeface(ctx, 3)), 1, SS.length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        SS.setSpan(new ForegroundColorSpan(ctx.getResources().getColor(R.color.colorblack)), 0, SS.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SS.setSpan(new RelativeSizeSpan(0.8f), 0, SS.length(), 0);
        actionBar.setTitle(SS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = ((Activity) ctx).getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ctx.getResources().getColor(R.color.colorblack));

            int color = ctx.getResources().getColor(R.color.colorblack);
            Bitmap bm = BitmapFactory.decodeResource(ctx.getResources(), R.mipmap.ic_launcher);
            ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription("Vyapari", bm, color);
            ((Activity) ctx).setTaskDescription(taskDescription);
        }

    }

    @SuppressWarnings("deprecation")
    public static void mCustomToast(Context ctx, String text, int color) {

        Toast toast = new Toast(ctx);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 290);
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_toast, null);
        TextView txt = (TextView) view.findViewById(R.id.tv_customtoast);
        txt.setGravity(Gravity.CENTER);
        txt.setTypeface(Utils.mTypeface(ctx, 1));
        if (color == 1) {
            txt.setTextColor(ctx.getResources().getColor(R.color.colorwhite));
            txt.setBackgroundColor(ctx.getResources().getColor(R.color.colorblack));
        } else if (color == 2) {
            txt.setTextColor(ctx.getResources().getColor(R.color.colorblack));
            txt.setBackgroundColor(ctx.getResources().getColor(R.color.colorwhite));
        }
        txt.setText(text);
        toast.setView(view);
        toast.show();
    }

    public static void mSendRequest(Context ctx, String token, String user_id_one, String user_id_two,
                                    String sendrqe) throws JSONException {
        String json = "";
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.accumulate("User_One_Id", user_id_one);
            jsonObject.accumulate("token", token);
            jsonObject.accumulate("User_Two_Id", user_id_two);

            json = jsonObject.toString();

            String data = "{" + " \"data\" " + ":" + json + "}";

            if (sendrqe.equals(Constants.FRIEND_REQUEST)) {
                Asyntask async = new Asyntask(ctx, new URL(Constants.base_url + Constants.businessrequest_url));
//                async.setShow_progress(false);
                async.execute(data);
            } else if (sendrqe.equals(Constants.FRIEND_ACCEPTED)) {
                Asyntask async = new Asyntask(ctx, new URL(Constants.base_url + Constants.accept_request_url));
                async.setShow_progress(false);
                async.execute(data);
            } else if (sendrqe.equals(Constants.USER_PROFILE)) {
                Asyntask async = new Asyntask(ctx, new URL(Constants.base_url + Constants.usertwodetails_url));
                async.setShow_progress(false);
                async.execute(data);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void universalImageUpload(String imagepath, ImageView imageview, Context ctx) {
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true)
                .resetViewBeforeLoading(true).showImageOnFail(R.mipmap.profilenewicon)
                .showImageOnLoading(R.mipmap.profilenewicon)
                .build();

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(ctx));
        // END - UNIVERSAL IMAGE LOADER SETUP

        ImageLoader imageLoader = ImageLoader.getInstance();
        if (!imagepath.equals("")) {
            imageLoader.displayImage(imagepath, imageview, options);
        } else {
            imageview.setImageDrawable(ctx.getResources().getDrawable(R.drawable.profilenewicon));
        }

    }


    public static void universalImageUploadnew(String userimage, int imagepath, ImageView imageview, Context ctx) {

        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true)
                .resetViewBeforeLoading(true).showImageOnFail(R.mipmap.profilenewicon)
                .showImageOnLoading(R.mipmap.profilenewicon)
                .build();

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(ctx));
        // END - UNIVERSAL IMAGE LOADER SETUP

        ImageLoader imageLoader = ImageLoader.getInstance();

        if (imagepath == 5) {
            imageview.setImageDrawable(ctx.getResources().getDrawable(R.drawable.disfive));
        } else if (imagepath == 10) {
            imageview.setImageDrawable(ctx.getResources().getDrawable(R.drawable.disten));
        } else if (imagepath == 15) {
            imageview.setImageDrawable(ctx.getResources().getDrawable(R.drawable.disfifteen));
        } else if (imagepath == 20) {
            imageview.setImageDrawable(ctx.getResources().getDrawable(R.drawable.distwenty));
        } else if (imagepath == 25) {
            imageview.setImageDrawable(ctx.getResources().getDrawable(R.drawable.distwentyfive));
        } else if (imagepath == 30) {
            imageview.setImageDrawable(ctx.getResources().getDrawable(R.drawable.disthirty));
        } else if (imagepath == 35) {
            imageview.setImageDrawable(ctx.getResources().getDrawable(R.drawable.disthirtyfive));
        } else if (imagepath == 40) {
            imageview.setImageDrawable(ctx.getResources().getDrawable(R.drawable.disfourty));
        } else if (imagepath == 45) {
            imageview.setImageDrawable(ctx.getResources().getDrawable(R.drawable.disfourtyfive));
        } else if (imagepath == 50) {
            imageview.setImageDrawable(ctx.getResources().getDrawable(R.drawable.disfifty));
        } else if (imagepath == 55) {
            imageview.setImageDrawable(ctx.getResources().getDrawable(R.drawable.disfiftyfive));
        } else if (imagepath == 60) {
            imageview.setImageDrawable(ctx.getResources().getDrawable(R.drawable.dissixty));
        } else if (imagepath == 65) {
            imageview.setImageDrawable(ctx.getResources().getDrawable(R.drawable.dissixtyfive));
        } else if (imagepath == 70) {
            imageview.setImageDrawable(ctx.getResources().getDrawable(R.drawable.disseventy));
        } else if (imagepath == 75) {
            imageview.setImageDrawable(ctx.getResources().getDrawable(R.drawable.disseventyfive));
        } else if (imagepath == 0) {
            imageLoader.displayImage(userimage, imageview, options);
        } else {
            imageview.setImageDrawable(ctx.getResources().getDrawable(R.drawable.profilenewicon));
        }

    }

    public static void mLocationEnabler(Context ctx) {
        LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        if (!gps_enabled && !network_enabled) {
            LocationEnableDialog dlg = new LocationEnableDialog(ctx);
            dlg.show();
            dlg.setCancelable(false);
//            dlg.setCanceledOnTouchOutside(false);
        }
    }

    private static final int PERMISSION_REQUEST_CODE = 200;

    public static boolean checkPermission(Context ctx) {
        int result = ContextCompat.checkSelfPermission(ctx, ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(ctx, CAMERA);
        int result2 = ContextCompat.checkSelfPermission(ctx, WRITE_EXTERNAL_STORAGE);
        int result3 = ContextCompat.checkSelfPermission(ctx, READ_CONTACTS);
        int result4 = ContextCompat.checkSelfPermission(ctx, RECEIVE_SMS);
        int result5 = ContextCompat.checkSelfPermission(ctx, READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED && result5 == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{ACCESS_FINE_LOCATION, CAMERA, WRITE_EXTERNAL_STORAGE, READ_CONTACTS, RECEIVE_SMS, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    public static String mDateTime(String date1) {

        int day, month, year;

        String str, date, time, finalmonth;

        str = date1;

        date = str.substring(0, str.indexOf(' '));
        time = str.substring(str.indexOf(' ') + 1);

        String tim[] = time.split(":");

        int hours = Integer.parseInt(tim[0]);
        int minutes = Integer.parseInt(tim[1]);
        // int hours = Integer.parseInt(tim[0]);
        String hours1, minutes1;
        if (hours < 10) {
            hours1 = "0" + hours;
        } else {
            hours1 = "" + hours;
        }
        if (minutes < 10) {
            minutes1 = "0" + minutes;
        } else {
            minutes1 = "" + minutes;
        }

        if (hours < 12) {
            time = hours1 + ":" + minutes1;
        } else {
            time = hours1 + ":" + minutes1;
        }

        String str1[] = date.split("-");
        day = Integer.parseInt(str1[2]);
        month = Integer.parseInt(str1[1]);
        year = Integer.parseInt(str1[0]);

        finalmonth = getMonthName(month);

        return day + " " + finalmonth + " at " + time;

    }

    public static String getMonthName(int month) {
        switch (month) {
            case 1:
                return "Jan";

            case 2:
                return "Feb";

            case 3:
                return "Mar";

            case 4:
                return "Apr";

            case 5:
                return "May";

            case 6:
                return "June";

            case 7:
                return "July";

            case 8:
                return "Aug";

            case 9:
                return "Sep";

            case 10:
                return "Oct";

            case 11:
                return "Nov";

            case 12:
                return "Dec";
        }

        return "";
    }

    public static LatLngBounds getBounds(LatLng location, int mDistanceInMeters) {
        double latRadian = Math.toRadians(location.latitude);

        double degLatKm = 110.574235;
        double degLongKm = 110.572833 * Math.cos(latRadian);
        double deltaLat = mDistanceInMeters / 1000.0 / degLatKm;
        double deltaLong = mDistanceInMeters / 1000.0 / degLongKm;

        double minLat = location.latitude - deltaLat;
        double minLong = location.longitude - deltaLong;
        double maxLat = location.latitude + deltaLat;
        double maxLong = location.longitude + deltaLong;

//        Log.d("", "Min: " + Double.toString(minLat) + "," + Double.toString(minLong));
//        Log.d("", "Max: " + Double.toString(maxLat) + "," + Double.toString(maxLong));

        return new LatLngBounds(new LatLng(minLat, minLong), new LatLng(maxLat, maxLong));
    }

}

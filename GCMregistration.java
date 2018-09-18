package com.demoapp.com.demoapp.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.demoapp.LoginActivity;
import com.demoapp.MainActivity;
import com.demoapp.SignupActivity;
import com.demoapp.com.demoapp.AsyncTask.HttpHelper;
import com.demoapp.com.demoapp.Dialog.OTPDialog;
import com.demoapp.com.demoapp.Dto.Registerdto;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMregistration {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    String SENDER_ID = "609628164485";// 129176368736 //
    // "461338624077";//461338624077
    String response;
    String mobile;
    String pwd;
    GoogleCloudMessaging gcm;
    Context context;
    String regid, data, customer_status;
    // String data;
    Registerdto register;
    long totalSize = 0;
    SignupActivity signup;
    private boolean fromlogin = true;
    LoginActivity login;
    Registerdto register2;

    public String initial(Context ctx, Registerdto register, SignupActivity signup) {
        this.context = ctx;
        this.register = register;
        this.signup = signup;
        if (checkPlayServices()) {

            gcm = GoogleCloudMessaging.getInstance(context);
            registerInBackground();
        }
        return regid;
    }

    public String initial(Context ctx, Registerdto register2, LoginActivity login) {
        this.context = ctx;
        this.register2 = register2;
        this.login = login;
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(context);
            registerInBackground();
        }
        return regid;
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            // check if playservices avaliable are not.
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) context, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("Play services", "This device is not supported.");
                ((Activity) context).finish();
            }
            return false;
        }
        return true;
    }

    private void registerInBackground() {
        new AsyncTask<String, Object, String>() {
            ProgressDialog progressBar;

            protected void onPreExecute() {
                ProgressDialog progressDialog = new ProgressDialog((Activity) context);
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                progressBar = progressDialog;
                progressBar.show();
            }

            @Override
            protected String doInBackground(String... params) {
                String msg = "";

                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context.getApplicationContext());
                    }

                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    SharedPreferences sp = context.getApplicationContext().getSharedPreferences("timetrav",
                            context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("devicekey", "" + gcm.register(SENDER_ID));
                    editor.commit();

                    if (fromlogin) {
                        fromlogin();
                    } else {
                        fromsignup();
                    }

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                    Utils.mCustomToast(context, "Device Registration Failed", 1);

                }
                return msg;
            }

            protected void onPostExecute(String result) {
                progressBar.dismiss();
                JSONObject jobj;
                if (fromlogin) {
                    try {
                        jobj = new JSONObject(response);
                        Log.i("Server REponse", "response " + response);

                        if (jobj.getInt("status") == 1) {

                            SharedPreferences sp = context.getApplicationContext().getSharedPreferences("timetrav",
                                    Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("user_token", jobj.getString("token"));
                            editor.putString("user_id", jobj.getString("UserID"));
                            editor.putString("UserName", jobj.getString("UserName"));

                            editor.commit();

                            Intent i = new Intent(context, MainActivity.class);
                            context.startActivity(i);
                            ((Activity) context).finish();

                        } else if (jobj.getInt("status") == 0) {
                            Utils.mCustomToast(context, jobj.getString("message"), 1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("@@@exception", "responce " + e.toString());
                    }

                } else if (!fromlogin) {
                    try {
                        jobj = new JSONObject(response);
                        if (jobj.getInt("status") == 1) {

                            SharedPreferences sp = context.getApplicationContext().getSharedPreferences("timetrav",
                                    Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("user_id", jobj.getString("UserID"));
                            editor.putString("mobile", jobj.getString("mobile"));
                            editor.commit();

                            OTPDialog dlg = new OTPDialog(context, signup, register);
                            dlg.show();
//                            ((Activity) context).finish();

                        } else if (jobj.getInt("status") == 0) {
                            Utils.mCustomToast(context, jobj.getString("message"), 1);
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }

            ;
        }.execute(null, null, null);
    }

    public void fromsignup() {

        String json = "";
        // 3. build jsonObject
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("GCMKey", regid);
            jsonObject.accumulate("device_unique_id", Build.SERIAL);
            jsonObject.accumulate("user_email", register.getMail());
            jsonObject.accumulate("user_name", register.getName());
//            jsonObject.accumulate("user_gender", register.getGender());
            jsonObject.accumulate("user_mobile", register.getMobile());
            jsonObject.accumulate("user_password", register.getPwd());

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            String data = "{" + " \"data\" " + ":" + json + "}";

            if (Utils.isNetworkConnected(context)) {
                response = HttpHelper.postRequestToServer(data, new URL(Constants.base_url + Constants.signup_url));
            } else {
                Utils.mCustomToast(context, "Ckeck Your Internet Connectivity..", 1);
            }

        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void fromlogin() {

        String json = "";
        // 3. build jsonObject
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.accumulate("devicekey", regid);
            jsonObject.accumulate("devicetype", "android");
            jsonObject.accumulate("MobileNumber", register2.getMobile());
            jsonObject.accumulate("password", register2.getPwd());
            jsonObject.accumulate("latitude", register2.getLatitude());
            jsonObject.accumulate("longitude", register2.getLongitude());

            json = jsonObject.toString();

            String data = "{" + " \"data\" " + ":" + json + "}";

            if (Utils.isNetworkConnected(context)) {
                response = HttpHelper.postRequestToServer(data, new URL(Constants.base_url + Constants.login_url));
            } else {
                Utils.mCustomToast(context, "Ckeck Your Internet Connectivity..", 1);
            }
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void setFrom_login(boolean b) {
        this.fromlogin = b;

    }

}

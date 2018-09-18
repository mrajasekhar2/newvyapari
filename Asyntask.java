package com.demoapp.com.demoapp.AsyncTask;

import java.net.URL;

import org.json.JSONException;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.Toast;

import com.demoapp.com.demoapp.Utils.Utils;

public class Asyntask extends AsyncTask<String, Object, String> {

    private ProgressDialog progressBar;
    private static Context ctx;
    private boolean show_progress = true;
    private URL url;
    private ResponseListner responseListener;
    private String network;

    public Asyntask(Context ctx, URL url) {
        Asyntask.ctx = ctx;
        this.url = url;
        responseListener = (ResponseListner) ((Activity) ctx);
    }

    public Asyntask(Context ctx, URL url, ResponseListner listner) {
        Asyntask.ctx = ctx;
        this.url = url;
        responseListener = listner;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (show_progress) {
            ProgressDialog progressDialog = new ProgressDialog((Activity) ctx);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressBar = progressDialog;
            progressBar.show();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String response = "";
        if (hasConnection()) {
            response = HttpHelper.postRequestToServer(params[0], url);
        } else {
            network = "No Connection";
            System.out.println("post request - " + response);
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (show_progress)
            progressBar.dismiss();
        if (network != null) {
            noConnectionAlert();
            return;
        }
        if (result == null || result.isEmpty()) {
            nullResponseAlert();
        } else {
            try {
                responseListener.serverResponse(result, url.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean hasConnection() {
        ConnectivityManager cm = (ConnectivityManager) ctx
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }
        NetworkInfo mobileNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }
        return false;
    }

    private void noConnectionAlert() {
        Utils.mCustomToast(ctx, "No Internet Connection..", 1);
    }

    private void nullResponseAlert() {
        Utils.mCustomToast(ctx, "We are sorry..!\nUnable to reach the server..", 1);
    }

    public void setShow_progress(boolean show_progress) {
        this.show_progress = show_progress;
    }
}

package com.demoapp.com.demoapp.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpHelper {
    static int connectionTimeout = 100000;
    static int readTimeout = 100000;

    public final static HttpClient final_httpClient = new DefaultHttpClient();

    public static String postRequestToServer(String body, URL url) {

        String response = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // connection.setRequestProperty("Content-Type",
            // "application/json");
            connection.setRequestMethod("POST");
            // connection.setConnectTimeout(connectionTimeout);
            // connection.setReadTimeout(readTimeout);
            // enabled by Shashi
            connection.setDoOutput(true);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
            outputStreamWriter.write(body);
            outputStreamWriter.flush();
            outputStreamWriter.close();
            // int respCode = connection.getResponseCode();
            // String me = con nection.getResponseMessage();
            BufferedReader bufferedReader = null;
            if (connection.getResponseCode() == 200) {
                bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                bufferedReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            // InputStream inputStream = connection.getInputStream();

            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\r\n");
            }
            response = stringBuilder.toString();

        } catch (IOException e) {
            System.out.println("Exception - " + e);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Exception - " + e);
            e.printStackTrace();
        }
        return response;
    }
}

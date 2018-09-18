package com.demoapp.com.demoapp.adapter;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.demoapp.R;
import com.demoapp.com.demoapp.AsyncTask.Asyntask;
import com.demoapp.com.demoapp.AsyncTask.ResponseListner;
import com.demoapp.com.demoapp.Dto.Contacts_dto;
import com.demoapp.com.demoapp.Utils.Constants;
import com.demoapp.com.demoapp.Utils.Utils;

public class ContactsAdapter extends BaseAdapter implements ResponseListner {

    Context ctx;
    List<Contacts_dto> c_list;
    List<String> list;

    ContactsAdapter contacts_adapter;
    boolean isforinvite = false;

    public ContactsAdapter(Context ctx, List<Contacts_dto> c_list, boolean isforinvite) {
        this.ctx = ctx;
        contacts_adapter = this;
        this.c_list = c_list;
        this.isforinvite = isforinvite;
        list = new ArrayList<String>();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return c_list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    class ViewHolder {
        TextView c_name, cno, tv_invitebutton;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {

        View vi = arg1;
        ViewHolder holder;
        if (vi == null) {
            holder = new ViewHolder();
            Context context = arg2.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.contcts_item, null);
            holder.c_name = (TextView) vi.findViewById(R.id.ct_name);
            holder.cno = (TextView) vi.findViewById(R.id.ct_no);
            holder.tv_invitebutton = (TextView) vi.findViewById(R.id.tv_invitebutton);

            holder.c_name.setTypeface(Utils.mTypeface(context, 1));
            holder.cno.setTypeface(Utils.mTypeface(context, 1));
            holder.tv_invitebutton.setTypeface(Utils.mTypeface(context, 1));
            vi.setTag(holder);
        } else

            holder = (ViewHolder) vi.getTag();

        final int position = arg0;
        holder.c_name.setText(c_list.get(position).getContactName());
        holder.cno.setText(c_list.get(position).getContactNumber());
        if (isforinvite) {
            holder.tv_invitebutton.setText("INVITE");
        } else {
            holder.tv_invitebutton.setText("CONNECT");
        }
        holder.tv_invitebutton.setEnabled(true);
        holder.tv_invitebutton.setBackground(ctx.getResources().getDrawable(R.drawable.button_background));

        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                int k = Integer.parseInt(list.get(i).toString());
                if (k == position) {

                    holder.tv_invitebutton.setText(" Invitation Sent...");
                    holder.tv_invitebutton.setEnabled(false);
                    holder.tv_invitebutton.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        }

        holder.tv_invitebutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                SharedPreferences sp = ctx.getSharedPreferences("timetrav", Context.MODE_PRIVATE);
                String user_id = sp.getString("user_id", "");
                String token = sp.getString("user_token", "");
                list.add("" + position);

//                String data = "";
//                if (isforinvite) {
//                    Asyntask async;
//                    try {
//                        data = URLEncoder.encode("devicekey", "UTF-8") + "=" + URLEncoder.encode(devicekey, "UTF-8")
//                                + "&";
//                        data += URLEncoder.encode("businessid", "UTF-8") + "=" + URLEncoder.encode(user_ids, "UTF-8")
//                                + "&";
//                        data += URLEncoder.encode("username", "UTF-8") + "="
//                                + URLEncoder.encode(c_list.get(position).getContactName(), "UTF-8") + "&";
//                        data += URLEncoder.encode("mobile", "UTF-8") + "="
//                                + URLEncoder.encode(c_list.get(position).getContactNumber(), "UTF-8");
//
//                        async = new Asyntask(ctx, new URL(Constants.base_url + Constants.invitefromcontacts_url),
//                                contacts_adapter);
//                        async.execute(data);
//                    } catch (MalformedURLException e) {
//                        e.printStackTrace();
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//                }

                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.accumulate("UserID", user_id);
                    jsonObject.accumulate("token", token);
                    jsonObject.accumulate("UserName", c_list.get(position).getContactName());
                    jsonObject.accumulate("MobileNumber", c_list.get(position).getContactNumber());

                    json = jsonObject.toString();

                    String data = "{" + " \"data\" " + ":" + json + "}";
                    if (Utils.isNetworkConnected(ctx)) {
                        Asyntask asynce = new Asyntask(ctx, new URL(Constants.base_url + Constants.invitefromcontacts_url), contacts_adapter);
                        asynce.execute(data);
                    } else {
                        Utils.mCustomToast(ctx, "Check Your Internet Connectivity..", 1);
                    }

                } catch (JSONException e1) {
                    e1.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

            }
        });
        return vi;
    }

    @Override
    public void serverResponse(String response, String path) throws JSONException, Exception {
        if (path.equals(Constants.base_url + Constants.invitefromcontacts_url)) {
            JSONObject jobj = new JSONObject(response);
            // {"status":1,"msg":"success"}
            if (jobj.getInt("status") == 1) {
                Utils.mCustomToast(ctx, "Invitation sent Successfully", 1);
                notifyDataSetChanged();
            } else {
                Utils.mCustomToast(ctx, jobj.getString("msg"), 1);
            }

        }

    }

}

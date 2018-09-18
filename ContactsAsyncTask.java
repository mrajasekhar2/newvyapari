package com.demoapp.com.demoapp.AsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.demoapp.com.demoapp.Dto.Contacts_dto;
import com.demoapp.com.demoapp.adapter.ContactsAdapter;

public class ContactsAsyncTask extends AsyncTask<String, Object, List<Contacts_dto>> {
    // private ProgressDialog progressBar;
    private ProgressDialog progressBar;
    Context ctx;
    ListView listview;
    ListView contactslist;
    LinearLayout layout;

    public ContactsAsyncTask(Context ctx, ListView lv) {
        this.ctx = ctx;
        listview = lv;
    }

    public ContactsAsyncTask(Context ctx, ListView lv, ListView contacts, LinearLayout layout) {
        this.ctx = ctx;
        listview = lv;
        this.contactslist = contacts;
        this.layout = layout;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ProgressDialog progressDialog = new ProgressDialog((Activity) ctx);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressBar = progressDialog;
        progressBar.show();

    }

    @Override
    protected List<Contacts_dto> doInBackground(String... params) {
        List<Contacts_dto> c_list = new ArrayList<Contacts_dto>();

        ContentResolver cr = ctx.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            // get name
            Contacts_dto con_dto = new Contacts_dto();
            int nameFiledColumnIndex = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
            String contact = cursor.getString(nameFiledColumnIndex);

            String[] PHONES_PROJECTION = new String[]{"_id", "display_name", "data1", "data3"};//
            String contactId = cursor.getString(cursor.getColumnIndex(PhoneLookup._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
            // name type ..
            while (phone.moveToNext()) {
                int i = phone.getInt(0);
                String str = phone.getString(1);

                str = phone.getString(2);
                str = phone.getString(3);
                String contact1 = phone.getString(2).replace("-", "");
                contact1 = contact1.replace(" ", "");
                if (contact1.startsWith("+91")) {
                    contact1 = contact1.substring(3);
                }

                // if (contact1.length() > 10) {
                // contact1 = contact1.substring(3);
                // }
                con_dto.setContactName(phone.getString(1));
                con_dto.setContactNumber(contact1);
                c_list.add(con_dto);
            }
            phone.close();
            // addr
            Cursor addrCur = cr.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                    new String[]{"_id", "data1", "data2", "data3"},
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
            while (addrCur.moveToNext()) {
                int i = addrCur.getInt(0);
                String str = addrCur.getString(1);
                str = addrCur.getString(2);
                str = addrCur.getString(3);
            }
            addrCur.close();

            // email
            Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    new String[]{"_id", "data1", "data2", "data3"},
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
            while (emailCur.moveToNext()) {
                int i = emailCur.getInt(0);
                String str = emailCur.getString(1);
                str = emailCur.getString(2);
                str = emailCur.getString(3);
            }
            emailCur.close();

        }
        cursor.close();
        int s = c_list.size();
        Collections.sort(c_list, new DateComparator());

        for (int i = 0; i < c_list.size(); i++) {
            String sss = c_list.get(i).getContactName();
            for (int j = i + 1; j < c_list.size(); j++) {

                String ss = c_list.get(j).getContactName();
                if (sss.equals(ss)) {
                    c_list.remove(j);
                    j--;
                }
            }
        }
        return c_list;
    }

    @Override
    protected void onPostExecute(List<Contacts_dto> result) {
        super.onPostExecute(result);
        progressBar.dismiss();

//        List<String> contacts = HomeActivity.mobileno_list;
//        if (contacts != null && contacts.size() > 0) {
//
//            for (int i = 0; i < contacts.size(); i++) {
//                for (int j = 0; j < result.size(); j++) {
//                    String userContact = result.get(j).getContactNumber();
//                    if (userContact.startsWith("+91")) {
//                        userContact = userContact.substring(3);
//                    }
//                    if (contacts.get(i).equals(userContact)) {
//                        result.remove(j);
//                        j--;
//                    }
//
//                }
//            }
//        }
        if (result != null && result.size() != 0) {
            if (contactslist != null && layout != null) {
                contactslist.setVisibility(View.VISIBLE);
                layout.setVisibility(View.GONE);
            }

            ContactsAdapter adapter = new ContactsAdapter(ctx, result, true);
            listview.setAdapter(adapter);
        } else {
            if (contactslist != null && layout != null) {
                contactslist.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
            }

        }
    }

    class DateComparator implements Comparator<Contacts_dto> {
        public int compare(Contacts_dto p1, Contacts_dto p2) {

            String time1 = p1.getContactName();
            String time2 = p2.getContactName();

            if (p1.getContactName().equalsIgnoreCase(p2.getContactName()))
                // return p1.getContactName().compareTo(p2.getContactName());
                return ((Comparable) time2).compareTo(time1);
            else
                // }
                return ((Comparable) time1).compareTo(time2);

        }
    }
}
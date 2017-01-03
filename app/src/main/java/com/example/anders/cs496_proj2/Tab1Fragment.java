package com.example.anders.cs496_proj2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.facebook.FacebookSdk;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Tab1Fragment extends Fragment {
    View view;
    private JSONArray contact_list;
    private ArrayList<String> str_contact_list;
    private ArrayAdapter<String> adapter;
    private ListView list_view;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.tab1, container, false);list_view = (ListView) view.findViewById(R.id.tab1);
        contact_list = new JSONArray();
        str_contact_list = new ArrayList<>();


        new LoadFBFriendsList().execute();

        return view;
    }

    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            JSONArray jarray = getContactList();
            appendPhoneContacts(jarray, "contact");
            str_contact_list = parse_JSONArray(contact_list);
            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, str_contact_list);
            list_view.setAdapter(adapter);
            list_view.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Tab1OnClickFragment dialog_fragment = new Tab1OnClickFragment();
                    Bundle args = new Bundle();
                    try {
                        JSONObject item = contact_list.getJSONObject(position);
                        args.putString("name", item.getString("name"));
                        if (item.getString("from").compareTo("contact") == 0) {
                            args.putString("number", item.getString("number"));
                        } else {
                            args.putString("thumbnail", item.getString("thumbnail"));
                        }
                        args.putString("from", item.getString("from"));

                        dialog_fragment.setArguments(args);
                        dialog_fragment.show(getActivity().getSupportFragmentManager(), "name and phone number");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(getActivity(), "Until you grant the permission, we cannot display the contacts", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private JSONArray getContactList() {
        JSONArray contact_list = new JSONArray();

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        String sort_order = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;

        Cursor cursor_contacts = getActivity().getContentResolver().query(uri, projection, null, null, sort_order);

        if (cursor_contacts.getCount() == 0) {
            cursor_contacts.close();
            return null;
        } else {
            try {
                cursor_contacts.moveToFirst();
                do {
                    JSONObject jobject = new JSONObject();
                    String name_str = cursor_contacts.getString(cursor_contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phone_num_str = cursor_contacts.getString(cursor_contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    jobject.put("name", name_str);
                    jobject.put("number", phone_num_str);
                    contact_list.put(jobject);
                } while (cursor_contacts.moveToNext());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return contact_list;
    }

    private void appendPhoneContacts(JSONArray jarray, String from) {
        if(jarray == null) {
            Toast.makeText(getActivity(), "Unable to connect to server", Toast.LENGTH_LONG);
        }
        for (int i = 0; i < jarray.length(); i++) {
            try {
                jarray.getJSONObject(i).put("from",  from);
                contact_list.put(jarray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<String> parse_JSONArray(JSONArray jarray) {
        ArrayList<String> str_list = new ArrayList<String>();

        for (int i = 0; i < jarray.length(); i++) {
            try {
                JSONObject jobject = jarray.getJSONObject(i);
                String str = jobject.getString("name");
                str_list.add(str);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return str_list;
    }

    public class Contact {
        String phonenum;
        String name;

        public Contact() {}

        public Contact(String phonenum, String name) {
            this.phonenum = phonenum;
            this.name = name;
        }

        public String getPhonenum() {
            return phonenum;
        }
        public void setPhonenum(String phonenum) {
            this.phonenum = phonenum;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }

    public class LoadFBFriendsList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            StringBuffer response = null;
            JSONArray jarray = null;
            try {
                URL url = new URL("http://ec2-52-79-95-160.ap-northeast-2.compute.amazonaws.com:3000/fb_profile");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + conn.getResponseCode());
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                response = new StringBuffer();
                String input_line;

                while ((input_line = in.readLine()) != null) {
                    System.out.println("input_line : " + input_line);
                    response.append(input_line);
                }
                in.close();

                jarray = new JSONArray(response.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            appendPhoneContacts(jarray, "facebook");
            str_contact_list = parse_JSONArray(contact_list);

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... params) {

        }

        @Override
        protected void onPostExecute(Void param) {
            showContacts();
        }
    }
}

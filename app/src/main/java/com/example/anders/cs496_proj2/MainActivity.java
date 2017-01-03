package com.example.anders.cs496_proj2;
//
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Output;
import android.os.AsyncTask;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ViewFlipper;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Toolbar toolbar;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout tabLayout;
    private CallbackManager callbackManager;


    private ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.facebook_login_activity);
        LoginButton login_button = (LoginButton) findViewById(R.id.login_button);
        login_button.setReadPermissions(Arrays.asList("user_friends"));
        callbackManager = CallbackManager.Factory.create();
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                setContentView(R.layout.activity_loading);
                ViewFlipper flipper = (ViewFlipper) findViewById(R.id.loading_flipper);

                flipper.setFlipInterval(400);
                flipper.startFlipping();

                AccessToken.setCurrentAccessToken(loginResult.getAccessToken());

                new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/me/taggable_friends",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            LoginManager.getInstance().logOut();
                            try {
                                //System.out.println(response.toString());
                                JSONObject res = response.getJSONObject();
                                final JSONArray data = res.getJSONArray("data");
                                new AfterGetFriendsList().execute(data);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                ).executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    public void saveInDB(JSONArray data) {
        StringBuffer response = new StringBuffer();

        try {
            URL init_url = new URL("http://ec2-52-79-95-160.ap-northeast-2.compute.amazonaws.com:3000/initialize_db");
            HttpURLConnection init_conn = (HttpURLConnection) init_url.openConnection();
            init_conn.setRequestMethod("GET");
            if (init_conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + init_conn.getResponseCode());
            }
            init_conn.disconnect();

            for (int i = 0; i < data.length(); i++) {
                //System.out.println("check");
                URL url = new URL("http://ec2-52-79-95-160.ap-northeast-2.compute.amazonaws.com:3000/save_profile");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Context-Type", "application/json");

                OutputStream out_stream = conn.getOutputStream();

                out_stream.write(data.getJSONObject(i).toString().getBytes("UTF-8"));
                //System.out.println(data.getJSONObject(i).toString());
                out_stream.close();

                conn.connect();
                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + conn.getResponseCode());
                }

                conn.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.removeOnPageChangeListener(listener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    Tab1Fragment fragmentA = new Tab1Fragment();
                    return fragmentA;
                case 1:
                    Tab2Fragment fragmentB = new Tab2Fragment();
                    return fragmentB;
                case 2:
                    Tab3Fragment fragmentC = new Tab3Fragment();
                    return fragmentC;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "A";
                case 1:
                    return "B";
                case 2:
                    return "C";
            }
            return null;
        }
    }

    public class AfterGetFriendsList extends AsyncTask<JSONArray, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(JSONArray... data) {
            System.out.println(data.toString());
            for (int i = 0; i < data[0].length(); i++) {
                try {
                    String thumbnail_url_str = data[0].getJSONObject(i).getJSONObject("picture").getJSONObject("data").getString("url");
                    thumbnail_url_str.replace("\\/", "/");
                    //System.out.println(thumbnail_url_str);

                    URL thumbnail_url = new URL(thumbnail_url_str);
                    Bitmap thumbnail_bitmap = BitmapFactory.decodeStream(thumbnail_url.openConnection().getInputStream());

                    data[0].getJSONObject(i).put("thumbnail", getStringFromBitmap(thumbnail_bitmap));
                    System.out.println(getStringFromBitmap(thumbnail_bitmap));

                    data[0].getJSONObject(i).remove("picture");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return data[0];
        }

        @Override
        protected void onProgressUpdate(Void... params) {}

        @Override
        protected void onPostExecute(JSONArray data) { new ClearDB().execute(data); }
    }

    public class ClearDB extends AsyncTask<JSONArray, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(JSONArray... data) {
            StringBuffer response;
            try {
                URL init_url = new URL("http://ec2-52-79-95-160.ap-northeast-2.compute.amazonaws.com:3000/initialize_db");
                HttpURLConnection init_conn = (HttpURLConnection) init_url.openConnection();
                init_conn.setRequestMethod("GET");
                if (init_conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + init_conn.getResponseCode());
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(init_conn.getInputStream(), "UTF-8"));

                response = new StringBuffer();
                String input_line;

                while ((input_line = in.readLine()) != null) {
                    System.out.println("input_line : " + input_line);
                    response.append(input_line);
                }
                in.close();

                init_conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data[0];
        }

        @Override
        protected void onProgressUpdate(Void... params) {}

        @Override
        protected void onPostExecute(JSONArray data) {
            new InitializeDB().execute(data);
        }
    }

    public class InitializeDB extends AsyncTask<JSONArray, Void, Void> {
        @Override
        protected Void doInBackground(JSONArray... data) {
            try {
                for (int i = 0; i < data[0].length(); i++) {
                    System.out.println("check");
                    URL url = new URL("http://ec2-52-79-95-160.ap-northeast-2.compute.amazonaws.com:3000/save_profile");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                    OutputStream out_stream = conn.getOutputStream();

                    out_stream.write(data[0].getJSONObject(i).toString().getBytes("UTF-8"));
                    out_stream.close();

                    conn.connect();
                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed : HTTP error code : "
                                + conn.getResponseCode());
                    }

                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... params) {}

        @Override
        protected void onPostExecute(Void null_value) {
            setContentView(R.layout.activity_main);

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            mViewPager.addOnPageChangeListener(listener);

            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
        }
    }

    public String getStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] b = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public Bitmap getBitmapFromString(String string) {
        byte[] b = Base64.decode(string, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }
}
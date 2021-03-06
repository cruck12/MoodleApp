package com.sahil.moodleapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Courses_Fragment.OnFragmentInteractionListener,
        Grades_Fragment.OnFragmentInteractionListener,
        Assignments_Fragment.OnFragmentInteractionListener,
        Notifications_Fragment.OnFragmentInteractionListener,
        Profile_Fragment.OnFragmentInteractionListener{

    String URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final WifiManager manager = (WifiManager) super.getSystemService(WIFI_SERVICE);
        final DhcpInfo dhcp = manager.getDhcpInfo();
        String gateway = LoginActivity.intToIp(dhcp.gateway);
        URL = "http://"+gateway +":8000";


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setTitle(R.string.app_name);

        // The first option of the navigation drawer is highlighted and the fragment is displayed.
        if(savedInstanceState==null) {
            navigationView.getMenu().getItem(0).setChecked(true);
            onNavigationItemSelected(navigationView.getMenu().getItem(0));
            View header = navigationView.getHeaderView(0);
            TextView name = (TextView) header.findViewById(R.id.textView_ProfileName);
            TextView email = (TextView) header.findViewById(R.id.textView_ProfileEmail);
            SharedPreferences settings = getApplication().getSharedPreferences("profileData", MODE_PRIVATE);
            name.setText(settings.getString("firstName",""));
            email.setText(settings.getString("email",""));
        }

    }

    //implement proper backstack
    final String BackStack= "back";
    private Boolean backExit = false;
    @Override
    public void onBackPressed() {
        final MoodleAppApplication moodleAppApplication=(MoodleAppApplication) getApplicationContext();
        RequestQueue mqueue= moodleAppApplication.getmRequestQueue();
        mqueue.cancelAll("");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(getFragmentManager().getBackStackEntryCount()>0) {
            getFragmentManager().popBackStack();
        } else if (true) {
            super.onBackPressed();
        } else {
/*            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            backExit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backExit = false;
                }
            }, 3 * 1000);
*/        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (id == R.id.nav_courses) {
            fragment = new Courses_Fragment();
            fragmentManager.beginTransaction().replace(R.id.Starting_Frame, fragment).addToBackStack(BackStack).commit();
        } else if (id == R.id.nav_grades) {
            fragment = new Grades_Fragment();
            fragmentManager.beginTransaction().replace(R.id.Starting_Frame, fragment).addToBackStack(BackStack).commit();
        } else if (id == R.id.nav_assignments) {
            fragment = new Assignments_Fragment();
            fragmentManager.beginTransaction().replace(R.id.Starting_Frame, fragment).addToBackStack(BackStack).commit();
        } else if (id == R.id.nav_profile) {
            fragment = new Profile_Fragment();
            fragmentManager.beginTransaction().replace(R.id.Starting_Frame, fragment).addToBackStack(BackStack).commit();
        } else if (id == R.id.nav_logout) {
            logoutUser();
    }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logoutUser(){
        CustomJsonRequest request = new CustomJsonRequest(URL + "/default/logout.json",null
                ,new Response.Listener<String>(){
            @Override
            //Parse LOGIN
            public void onResponse(String response1){
                try {
                    JSONObject response = new JSONObject(response1);
                    SharedPreferences pref1 = getApplicationContext().getSharedPreferences("loginData", MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = pref1.edit();
                    editor1.putBoolean("loginSuccess", false);
                    editor1.apply();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    //switch off the regular notiication check when logging out
                    AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    manager.cancel(LoginActivity.pintent);

                    startActivity(intent);
                }
                catch (JSONException e){

                }
            }
        }
                ,new Response.ErrorListener() {
            @Override
            //Handle Errors
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(),volleyError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        request.setTag("logoutRequest");
        final MoodleAppApplication moodleAppApplication=(MoodleAppApplication) getApplicationContext();
        RequestQueue mqueue= moodleAppApplication.getmRequestQueue();
        mqueue.add(request);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //can leave it empty
    }
}

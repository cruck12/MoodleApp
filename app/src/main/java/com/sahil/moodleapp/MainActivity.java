package com.sahil.moodleapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Courses_Fragment.OnFragmentInteractionListener,
        Grades_Fragment.OnFragmentInteractionListener,
        Assignments_Fragment.OnFragmentInteractionListener,
        Notifications_Fragment.OnFragmentInteractionListener,
        Profile_Fragment.OnFragmentInteractionListener{

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

        //Filling in the profile details
        SharedPreferences settings = getApplication().getSharedPreferences("profileData", MODE_PRIVATE);
        final TextView firstName =(TextView) findViewById(R.id.profile_firstName);
        final TextView lastName =(TextView) findViewById(R.id.profile_lastName);
        final TextView email =(TextView) findViewById(R.id.profile_email);
        final TextView username =(TextView) findViewById(R.id.profile_username);
        final TextView entryNo =(TextView) findViewById(R.id.profile_entryNo);
        final TextView type =(TextView) findViewById(R.id.profile_type);
        firstName.setText(firstName.getText()+"\t:\t"+settings.getString("firstName",""));
        lastName.setText(lastName.getText()+"\t:\t"+settings.getString("lastName",""));
        email.setText(email.getText()+"\t:\t"+settings.getString("email",""));
        username.setText(username.getText()+"\t:\t"+settings.getString("username",""));
        entryNo.setText(entryNo.getText()+"\t:\t"+settings.getString("entryNo",""));
        type.setText(type.getText()+"\t:\t"+settings.getString("type",""));


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // The first option of the navigation drawer is highlighted and the fragment is displayed.
        navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
    }

    //implement proper backstack
    final String BackStack= "back";
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
        FragmentManager fragmentManager = getFragmentManager();
        if (id == R.id.nav_courses) {
            fragment = new Courses_Fragment();
            fragmentManager.beginTransaction().replace(R.id.Starting_Frame, fragment).addToBackStack(BackStack).commit();
        } else if (id == R.id.nav_grades) {
            fragment = new Grades_Fragment();
            fragmentManager.beginTransaction().replace(R.id.Starting_Frame, fragment).addToBackStack(BackStack).commit();
        } else if (id == R.id.nav_assignments) {
            fragment = new Assignments_Fragment();
            fragmentManager.beginTransaction().replace(R.id.Starting_Frame, fragment).addToBackStack(BackStack).commit();
        } else if (id == R.id.nav_notifications) {
            fragment = new Notifications_Fragment();
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
        final WifiManager manager = (WifiManager) super.getSystemService(WIFI_SERVICE);
        final DhcpInfo dhcp = manager.getDhcpInfo();
        String gateway = LoginActivity.intToIp(dhcp.gateway);
        String URL = "http://"+gateway +":8000";
        JsonObjectRequest request = new JsonObjectRequest(URL + "/default/logout.json",null
                ,new Response.Listener<JSONObject>(){
            @Override
            //Parse LOGIN
            public void onResponse(JSONObject response){
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
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

package com.sahil.moodleapp;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
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

        // The first option of the navigation drawer is highlighted and the fragment is displayed.
        if(savedInstanceState==null) {
            navigationView.getMenu().getItem(0).setChecked(true);
            onNavigationItemSelected(navigationView.getMenu().getItem(0));
        }
        else{
        }
    }

    //implement proper backstack
    final String BackStack= "back";
    private Boolean backExit = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(getFragmentManager().getBackStackEntryCount()>0) {
            getFragmentManager().popBackStack();
        } else if (backExit) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            backExit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backExit = false;
                }
            }, 3 * 1000);
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
            showCourses();
        } else if (id == R.id.nav_grades) {
            fragment = new Grades_Fragment();
            fragmentManager.beginTransaction().replace(R.id.Starting_Frame, fragment).addToBackStack(BackStack).commit();
            showGrades();
        } else if (id == R.id.nav_assignments) {
            fragment = new Assignments_Fragment();
            fragmentManager.beginTransaction().replace(R.id.Starting_Frame, fragment).addToBackStack(BackStack).commit();
            showAssignments();
        } else if (id == R.id.nav_notifications) {
            fragment = new Notifications_Fragment();
            fragmentManager.beginTransaction().replace(R.id.Starting_Frame, fragment).addToBackStack(BackStack).commit();
            showNotifications();
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

    private void showNotifications()
    {
        CustomJsonRequest request = new CustomJsonRequest(URL+"/default/notifications.json",null
                ,new Response.Listener<String>(){
            @Override
            //Parse LOGIN
            public void onResponse(String response1){
                RelativeLayout layout = (RelativeLayout) findViewById(R.id.notifs_rel_layout);
                try {
                    JSONObject response = new JSONObject(response1);
                    JSONArray notifications = response.getJSONArray("notifications");
                    for(int i=0;i<notifications.length();i++){
                        final JSONObject notification = notifications.getJSONObject(i);
                        String htmlDesc = notification.getString("description");
                        //the layout on which you are working

                        //set the properties for button
                        Button btnTag = new Button(getApplicationContext());
                        btnTag.setId(i + 1);
                        RelativeLayout.LayoutParams lay= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        if(i!=0){
                            lay.addRule(RelativeLayout.BELOW,i);
                        }
                        else{
                            lay.addRule(RelativeLayout.BELOW,R.id.notifs_label);
                        }
                        btnTag.setLayoutParams(lay);

                        //extracting the text from the complex description given
                        String desc="";
                        boolean flag=true;

                        for(int j=0;j<htmlDesc.length();j++)
                        {
                            char ch=htmlDesc.charAt(j);
                            if(ch=='<')
                                flag=false;
                            else if(ch=='>')
                                flag=true;
                            else if(flag)
                                desc=desc+ch;
                        }
                        btnTag.setText(desc);
                        /*btnTag.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        });*/
                        layout.addView(btnTag);
                    }
                }
                catch(JSONException e){
                    Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
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
        //request.setTag("notifsRequest");
        final MoodleAppApplication moodleAppApplication=(MoodleAppApplication) getApplicationContext();
        RequestQueue mqueue= moodleAppApplication.getmRequestQueue();
        mqueue.add(request);
    }

    private void showAssignments() {
        final MoodleAppApplication moodleAppApplication=(MoodleAppApplication) getApplicationContext();
        RequestQueue mqueue= moodleAppApplication.getmRequestQueue();
        CustomJsonRequest request1 = new CustomJsonRequest(URL + "/courses/list.json", null
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try{
                    JSONObject response = new JSONObject(s);
                    JSONArray courses = response.getJSONArray("courses");
                    for(int i=0;i<courses.length();i++){
                        String code = courses.getJSONObject(i).getString("code");
                        showAssign(code);
                    }
                }
                catch (JSONException e){
                    Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(),volleyError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        mqueue.add(request1);
    }

    private void showAssign(String code) {
        CustomJsonRequest request = new CustomJsonRequest(URL + "/courses/course.json/" + code + "/assignments", null
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                RelativeLayout layout = (RelativeLayout) findViewById(R.id.assign_rel_layout);
                try {
                    JSONObject request = new JSONObject(s);
                    JSONArray assignments = request.getJSONArray("assignments");
                    for(int i=0; i<assignments.length();i++){
                        final JSONObject assignment = assignments.getJSONObject(i);

                        String name = assignment.getString("name");

                        //set the properties for button
                        Button btnTag = new Button(getApplicationContext());
                        btnTag.setId(i+1);
                        RelativeLayout.LayoutParams lay= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        if(i!=0){
                            lay.addRule(RelativeLayout.BELOW,i);
                        }
                        else{
                            lay.addRule(RelativeLayout.BELOW,R.id.assignment_label);
                        }
                        btnTag.setLayoutParams(lay);
                        btnTag.setText(name.split(":")[0]);

                        btnTag.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent intent = new Intent(getApplicationContext(), A1.class);
                                    intent.putExtra("createdAt", assignment.getString("created_at"));
                                    intent.putExtra("assignName", assignment.getString("name"));
                                    intent.putExtra("assignDescription", assignment.getString("description"));
                                    intent.putExtra("deadline", assignment.getString("deadline"));
                                    intent.putExtra("courseId", assignment.getInt("registered_course_id"));
                                    intent.putExtra("assignId", assignment.getInt("id"));
                                    startActivity(intent);
                                }
                                catch (JSONException e){
                                    Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        layout.addView(btnTag);
                    }
                }
                catch (JSONException e){
                    Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(),volleyError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        final MoodleAppApplication moodleAppApplication=(MoodleAppApplication) getApplicationContext();
        RequestQueue mqueue= moodleAppApplication.getmRequestQueue();
        mqueue.add(request);
    }

    private void showGrades() {
        CustomJsonRequest request = new CustomJsonRequest(URL+"/grades.json",null
                ,new Response.Listener<String>(){
            @Override
            //Parse LOGIN
            public void onResponse(String response){
                FrameLayout layout = (FrameLayout) findViewById(R.id.grades_layout);
                //the layout on which you are working
                TableLayout table = (TableLayout)findViewById(R.id.table_Grades);
                try {
                    JSONObject response1 = new JSONObject(response);
                    JSONArray courses = response1.getJSONArray("courses");
                    JSONArray grades = response1.getJSONArray("grades");
                    for(int i=0;i<courses.length();i++){
                        JSONObject course = courses.getJSONObject(i);
                        JSONObject grade = grades.getJSONObject(i);
                        String code = course.getString("code");


                        // create a new TableRow
                        TableRow row = new TableRow(getApplicationContext());

                        // create a new TextView for showing xml data
                        TextView t1 = new TextView(getApplicationContext());
                        // set the text
                        t1.setText( code);
                        //set text color
                        int color =ContextCompat.getColor(getApplicationContext(),R.color.textColor);
                        t1.setTextColor(color);
                        // add the TextView  to the new TableRow
                        row.addView(t1);

                        TextView t2 = new TextView(getApplicationContext());
                        t2.setText(grade.getString("name") + " " );
                        t2.setTextColor(color);
                        row.addView(t2);

                        TextView t3 = new TextView(getApplicationContext());
                        t3.setText(grade.getInt("score") + " " );
                        t3.setTextColor(color);
                        row.addView(t3);

                        TextView t4 = new TextView(getApplicationContext());
                        t4.setText(grade.getInt("out_of") + " ");
                        t4.setTextColor(color);
                        row.addView(t4);

                        // add the TableRow to the TableLayout
                        table.addView(row, new TableLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT));
                    };
                }
                catch(JSONException e){
                    Toast.makeText(getApplicationContext(),"test2 "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
        request.setTag("gradeRequest");
        final MoodleAppApplication moodleAppApplication=(MoodleAppApplication) getApplicationContext();
        RequestQueue mqueue= moodleAppApplication.getmRequestQueue();
        mqueue.add(request);
    }



    private void showCourses() {
        CustomJsonRequest request = new CustomJsonRequest(URL+"/courses/list.json",null
                ,new Response.Listener<String>(){
            @Override
            //Parse LOGIN
            public void onResponse(String response1){
                RelativeLayout layout = (RelativeLayout) findViewById(R.id.course_rel_layout);
                try {
                    JSONObject response = new JSONObject(response1);
                    JSONArray courses = response.getJSONArray("courses");
                    for(int i=0;i<courses.length();i++){
                        final JSONObject course = courses.getJSONObject(i);
                        String code = course.getString("code");
                        //the layout on which you are working

                        //set the properties for button
                        Button btnTag = new Button(getApplicationContext());
                        btnTag.setId(i+1);
                        RelativeLayout.LayoutParams lay= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        if(i!=0){
                            lay.addRule(RelativeLayout.BELOW,i);
                        }
                        else{
                            lay.addRule(RelativeLayout.BELOW,R.id.courses_label);
                        }
                        btnTag.setLayoutParams(lay);
                        btnTag.setText(code);

                        btnTag.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent intent = new Intent(getApplicationContext(), C1.class);
                                    intent.putExtra("courseCode", course.getString("code"));
                                    intent.putExtra("courseName", course.getString("name"));
                                    intent.putExtra("courseDescription", course.getString("description"));
                                    intent.putExtra("courseCredits", course.getInt("credits"));
                                    intent.putExtra("courseId", course.getInt("id"));
                                    intent.putExtra("courseLtp", course.getString("l_t_p"));

                                    startActivity(intent);
                                }
                                catch (JSONException e){

                                }
                            }
                        });
                        //add button to the layout
                        layout.addView(btnTag);
                    }
                }
                catch(JSONException e){

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
        request.setTag("courseRequest");
        final MoodleAppApplication moodleAppApplication=(MoodleAppApplication) getApplicationContext();
        RequestQueue mqueue= moodleAppApplication.getmRequestQueue();
        mqueue.add(request);
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

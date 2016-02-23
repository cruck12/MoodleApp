package com.sahil.moodleapp;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class C1GradeFragment extends Fragment {
    String URL;
    String courseCode;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final WifiManager manager = (WifiManager) super.getActivity().getSystemService(getActivity().WIFI_SERVICE);
        final DhcpInfo dhcp = manager.getDhcpInfo();
        String gateway = LoginActivity.intToIp(dhcp.gateway);
        URL = "http://"+gateway +":8000";

        courseCode=getActivity().getIntent().getExtras().getString("courseCode");

        View rootView = inflater.inflate(R.layout.fragment_c1_grades, container,
                false);
        showGrades();
        return rootView;

    }

    private void showGrades() {
        CustomJsonRequest request = new CustomJsonRequest(URL+"/courses/course.json/"+courseCode+"/grades",null
                ,new Response.Listener<String>(){
            @Override
            //Parse LOGIN
            public void onResponse(String response){
                RelativeLayout layout = (RelativeLayout) getActivity().findViewById(R.id.c1_grades_layout);
                //the layout on which you are working
                TableLayout table = (TableLayout) getActivity().findViewById(R.id.c1_table_Grades);
                try {
                    JSONObject response1 = new JSONObject(response);
                    JSONArray grades = response1.getJSONArray("grades");
                    for(int i=0;i<grades.length();i++){
                        JSONObject grade = grades.getJSONObject(i);

                        // create a new TableRow
                        TableRow row = new TableRow(getContext());

                        // create a new TextView for showing xml data
                        TextView t1 = new TextView(getContext());
                        // set the text
                        t1.setText( courseCode);
                        //set text color
                        int color = ContextCompat.getColor(getActivity().getApplicationContext(),R.color.textColor);
                        t1.setTextColor(color);
                        // add the TextView  to the new TableRow
                        row.addView(t1);

                        TextView t2 = new TextView(getContext());
                        t2.setText(grade.getString("name") + " " );
                        t2.setTextColor(color);
                        row.addView(t2);

                        TextView t3 = new TextView(getContext());
                        t3.setText(grade.getInt("score") + " " );
                        t3.setTextColor(color);
                        row.addView(t3);

                        TextView t4 = new TextView(getContext());
                        t4.setText(grade.getInt("out_of") + " ");
                        t4.setTextColor(color);
                        row.addView(t4);

                        // add the TableRow to the TableLayout
                        table.addView(row, new TableLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT));
                    };
                }
                catch(JSONException e){
                    Toast.makeText(getContext(),"test2 "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
                ,new Response.ErrorListener() {
            @Override
            //Handle Errors
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getContext(),volleyError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        request.setTag("gradeRequest");
        final MoodleAppApplication moodleAppApplication=(MoodleAppApplication) getActivity().getApplicationContext();
        RequestQueue mqueue= moodleAppApplication.getmRequestQueue();
        mqueue.add(request);
    }
}

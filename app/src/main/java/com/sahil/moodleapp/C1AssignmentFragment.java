package com.sahil.moodleapp;

import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sahil on 2/23/2016.
 */
public class C1AssignmentFragment extends Fragment {
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

        View rootView = inflater.inflate(R.layout.fragment_c1_assignment, container,
                false);

        showAssignments();
        return rootView;
    }

    private void showAssignments() {
        final MoodleAppApplication moodleAppApplication=(MoodleAppApplication) getActivity().getApplicationContext();
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
                        if(code.equalsIgnoreCase(courseCode))
                            showAssign(code);
                    }
                }
                catch (JSONException e){
                    Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getContext(),volleyError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        mqueue.add(request1);
    }

    private void showAssign(String code) {
        CustomJsonRequest request = new CustomJsonRequest(URL + "/courses/course.json/" + code + "/assignments", null
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                RelativeLayout layout = (RelativeLayout) getActivity().findViewById(R.id.c1_assignment_layout);
                try {
                    JSONObject request = new JSONObject(s);
                    JSONArray assignments = request.getJSONArray("assignments");
                    for(int i=0; i<assignments.length();i++){
                        final JSONObject assignment = assignments.getJSONObject(i);

                        String name = assignment.getString("name");

                        //set the properties for button
                        Button btnTag = new Button(getActivity().getApplicationContext());
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
                                    Intent intent = new Intent(getContext(), A1.class);
                                    intent.putExtra("createdAt", assignment.getString("created_at"));
                                    intent.putExtra("assignName", assignment.getString("name"));
                                    intent.putExtra("assignDescription", assignment.getString("description"));
                                    intent.putExtra("deadline", assignment.getString("deadline"));
                                    intent.putExtra("courseId", assignment.getInt("registered_course_id"));
                                    intent.putExtra("assignId", assignment.getInt("id"));
                                    startActivity(intent);
                                }
                                catch (JSONException e){
                                    Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        layout.addView(btnTag);
                    }
                }
                catch (JSONException e){
                    Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getContext(),volleyError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        final MoodleAppApplication moodleAppApplication=(MoodleAppApplication) getActivity().getApplicationContext();
        RequestQueue mqueue= moodleAppApplication.getmRequestQueue();
        mqueue.add(request);
    }


}

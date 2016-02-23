package com.sahil.moodleapp;

import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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


public class C1ThreadFragment extends Fragment {
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

        View rootView = inflater.inflate(R.layout.fragment_c1_thread, container, false);

        showThreads();

        return rootView;
    }

    private void showThreads(){
        CustomJsonRequest request = new CustomJsonRequest(URL+"/courses/course.json/"+courseCode+"/threads",null
                ,new Response.Listener<String>(){
            @Override
            //Parse LOGIN
            public void onResponse(String response){
                RelativeLayout layout = (RelativeLayout) getActivity().findViewById(R.id.c1_thread_layout);

                try {
                    JSONObject response1 = new JSONObject(response);
                    JSONArray threads = response1.getJSONArray("course_threads");
                    for(int i=0;i<threads.length();i++){
                        final JSONObject thread = threads.getJSONObject(i);

                        final String title=thread.getString("title");

                        //set the properties for button
                        Button btnTag = new Button(getActivity().getApplicationContext());
                        btnTag.setId(i+1);
                        RelativeLayout.LayoutParams lay= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        if(i!=0){
                            lay.addRule(RelativeLayout.BELOW,i);
                        }
                        else{
                            lay.addRule(RelativeLayout.BELOW,R.id.threads_label);
                        }
                        btnTag.setLayoutParams(lay);
                        btnTag.setText(title);

                        btnTag.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent intent = new Intent(getContext(), ThreadActivity.class);
                                    intent.putExtra("title",title);
                                    intent.putExtra("description",thread.getString("description"));
                                    intent.putExtra("created",thread.getString("created_at"));
                                    intent.putExtra("tid", thread.getString("id"));
                                    intent.putExtra("updated", thread.getString("updated_at"));
                                    intent.putExtra("userid",thread.getString("user_id"));
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        layout.addView(btnTag);}
                }
                catch(JSONException e){
                    Toast.makeText(getContext(), "test2 " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

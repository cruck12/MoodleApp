package com.sahil.moodleapp;

import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ThreadActivity extends AppCompatActivity {

    String title,created,tid,updated,userid,description;

    String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);

        final WifiManager manager = (WifiManager) super.getSystemService(WIFI_SERVICE);
        final DhcpInfo dhcp = manager.getDhcpInfo();
        String gateway = LoginActivity.intToIp(dhcp.gateway);
        URL = "http://"+gateway +":8000";

        created=getIntent().getExtras().getString("created");
        title=getIntent().getExtras().getString("title");
        tid=getIntent().getExtras().getString("tid");
        updated=getIntent().getExtras().getString("updated");
        userid=getIntent().getExtras().getString("userid");
        description=getIntent().getExtras().getString("description");

        setTitle(title);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.thread_layout);

        TextView t1 = new TextView(getApplicationContext());
        // set the text
        t1.setText(title);
        //set text color
        final int color = ContextCompat.getColor(getApplicationContext(), R.color.textColor);

        t1.setTextColor(color);
        t1.setId(R.id.thread_title);
        t1.setTextSize(TypedValue.COMPLEX_UNIT_PT,14);
        RelativeLayout.LayoutParams lay= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lay.addRule(RelativeLayout.BELOW,R.id.thread_activity_head);
        t1.setLayoutParams(lay);
        layout.addView(t1);


        TextView t2 = new TextView(getApplicationContext());
        // set the text
        t2.setText(description);
        //set text color
        t2.setTextColor(color);
        t2.setId(R.id.thread_description);
        t2.setTextSize(TypedValue.COMPLEX_UNIT_PT,12);
        RelativeLayout.LayoutParams lay1= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lay1.addRule(RelativeLayout.BELOW,R.id.thread_title);
        t2.setLayoutParams(lay1);
        layout.addView(t2);

        TextView t3 = new TextView(getApplicationContext());
        // set the text
        t3.setText("Created On: " +created);
        //set text color
        t3.setTextColor(color);
        t3.setId(R.id.thread_created);
        t3.setTextSize(TypedValue.COMPLEX_UNIT_PT,6);
        RelativeLayout.LayoutParams lay2= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lay2.addRule(RelativeLayout.BELOW,R.id.thread_description);
        t3.setLayoutParams(lay2);
        layout.addView(t3);

        // create a new TextView for showing xml data
        TextView t4 = new TextView(getApplicationContext());
        // set the text
        t4.setText("Updated On: "+updated);
        //set text color
        t4.setTextColor(color);
        t4.setId(R.id.thread_updated);
        t4.setTextSize(TypedValue.COMPLEX_UNIT_PT,6);
        RelativeLayout.LayoutParams lay3= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lay3.addRule(RelativeLayout.BELOW,R.id.thread_created);
        t4.setLayoutParams(lay3);
        layout.addView(t4);


        TextView t5 = new TextView(getApplicationContext());
        // set the text
        t5.setText("Created by id: " + userid);
        //set text color
        t5.setTextColor(color);
        t5.setId(R.id.thread_by);
        t5.setTextSize(TypedValue.COMPLEX_UNIT_PT, 6);
        RelativeLayout.LayoutParams lay4= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lay4.addRule(RelativeLayout.BELOW,R.id.thread_updated);
        t5.setLayoutParams(lay4);
        layout.addView(t5);


        CustomJsonRequest request = new CustomJsonRequest(URL+"/threads/thread.json/"+tid,null
                ,new Response.Listener<String>(){
            @Override
            //Parse LOGIN
            public void onResponse(String response){

                try {
                    JSONObject response1 = new JSONObject(response);
                    JSONArray comments=response1.getJSONArray("comments");
                    JSONArray commentUsers=response1.getJSONArray("comment_users");
                    for(int i=0;i<comments.length();i++){

                        RelativeLayout layout = (RelativeLayout) findViewById(R.id.thread_layout);
                        final String commentUser=commentUsers.getJSONObject(i).getString("first_name")+" "+commentUsers.getJSONObject(i).getString("last_name");
                        final String comment=comments.getJSONObject(i).getString("description");
                        String display="<b>"+commentUser+"</b> : "+comment;
                        TextView txt=new TextView(getApplicationContext());
                        txt.setText(Html.fromHtml(display));
                        txt.setTextColor(color);
                        txt.setId(i+10);
                        txt.setTextSize(TypedValue.COMPLEX_UNIT_PT, 9);
                        RelativeLayout.LayoutParams lay= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        if(i==0)
                            lay.addRule(RelativeLayout.BELOW, R.id.thread_by);
                        else
                            lay.addRule(RelativeLayout.BELOW, i+9);
                        txt.setLayoutParams(lay);
                        layout.addView(txt);

                    }
                }
                catch(JSONException e){
                    Toast.makeText(getApplicationContext(), "test2 " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

        sendComment();
    }

    private void sendComment()
    {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.thread_layout_outer);

        Button sendButton = new Button(getApplicationContext());
        sendButton.setText("SEND");
        RelativeLayout.LayoutParams lay2= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lay2.addRule(RelativeLayout.BELOW, R.id.comText);
        sendButton.setLayoutParams(lay2);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EditText comment_send = (EditText) findViewById(R.id.comText);
                    String desc=comment_send.getText().toString().replaceAll(" ","%20");
                    CustomJsonRequest request = new CustomJsonRequest(URL + "/threads/post_comment.json?thread_id=" + tid + "&description=" + desc, null
                            , new Response.Listener<String>() {
                        @Override
                        //Parse LOGIN
                        public void onResponse(String response) {

                            try {
                                JSONObject response1 = new JSONObject(response);
                                boolean success = response1.getBoolean("success");
                                if (success) {
                                    Toast.makeText(getApplicationContext(),"Comment successfully posted", Toast.LENGTH_SHORT).show();
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Comment failed to post", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                            , new Response.ErrorListener() {
                        @Override
                        //Handle Errors
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    final MoodleAppApplication moodleAppApplication = (MoodleAppApplication) getApplicationContext();
                    RequestQueue mqueue = moodleAppApplication.getmRequestQueue();
                    mqueue.add(request);
                } catch (Exception e) {

                }
            }
        });
        layout.addView(sendButton);

    }
}

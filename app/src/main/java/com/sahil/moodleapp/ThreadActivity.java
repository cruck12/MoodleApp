package com.sahil.moodleapp;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
        int color = ContextCompat.getColor(getApplicationContext(), R.color.textColor);

        t1.setTextColor(color);
        t1.setId(R.id.thread_title);
        t1.setTextSize(TypedValue.COMPLEX_UNIT_PT,12);
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
        t2.setTextSize(TypedValue.COMPLEX_UNIT_PT,10);
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
        t5.setText("Created by id: "+userid);
        //set text color
        t5.setTextColor(color);
        t5.setId(R.id.thread_by);
        t5.setTextSize(TypedValue.COMPLEX_UNIT_PT,6);
        RelativeLayout.LayoutParams lay4= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lay4.addRule(RelativeLayout.BELOW,R.id.thread_updated);
        t5.setLayoutParams(lay4);
        layout.addView(t5);




    }
}

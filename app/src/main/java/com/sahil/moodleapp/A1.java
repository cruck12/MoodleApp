package com.sahil.moodleapp;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class A1 extends AppCompatActivity {

    String created,assignName,assignDesc,deadline;
    int courseid,assignid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a1);
        created=getIntent().getExtras().getString("createdAt");
        assignName=getIntent().getExtras().getString("assignName");
        assignDesc=getIntent().getExtras().getString("assignDescription");
        deadline=getIntent().getExtras().getString("deadline");
        courseid=getIntent().getExtras().getInt("courseid");
        assignid=getIntent().getExtras().getInt("assignid");

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.assignment_activity);

        // create a new TextView for showing xml data
        TextView t1 = new TextView(getApplicationContext());
        // set the text
        t1.setText(assignName);
        //set text color
        int color = ContextCompat.getColor(getApplicationContext(),R.color.textColor);

        t1.setTextColor(color);
        t1.setId(R.id.assignment_name);
        t1.setTextSize(TypedValue.COMPLEX_UNIT_PT,8);
        RelativeLayout.LayoutParams lay= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lay.addRule(RelativeLayout.BELOW,R.id.assignment_activity_head);
        t1.setLayoutParams(lay);
        layout.addView(t1);

        // create a new TextView for showing xml data
        TextView t2 = new TextView(getApplicationContext());
        // set the text
        t2.setText(assignDesc);
        //set text color
        t2.setTextColor(color);
        t2.setId(R.id.assignment_description);
        t2.setTextSize(TypedValue.COMPLEX_UNIT_PT,6);
        RelativeLayout.LayoutParams lay1= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lay1.addRule(RelativeLayout.BELOW,R.id.assignment_name);
        t2.setLayoutParams(lay1);
        layout.addView(t2);

        // create a new TextView for showing xml data
        TextView t3 = new TextView(getApplicationContext());
        // set the text
        t3.setText(created);
        //set text color
        t3.setTextColor(color);
        t3.setId(R.id.created);
        t3.setTextSize(TypedValue.COMPLEX_UNIT_PT,6);
        RelativeLayout.LayoutParams lay2= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lay2.addRule(RelativeLayout.BELOW,R.id.assignment_description);
        t3.setLayoutParams(lay2);
        layout.addView(t3);

        // create a new TextView for showing xml data
        TextView t4 = new TextView(getApplicationContext());
        // set the text
        t4.setText(deadline);
        //set text color
        t4.setTextColor(color);
        t4.setId(R.id.deadline);
        t4.setTextSize(TypedValue.COMPLEX_UNIT_PT,6);
        RelativeLayout.LayoutParams lay3= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lay3.addRule(RelativeLayout.BELOW,R.id.created);
        t4.setLayoutParams(lay3);
        layout.addView(t4);

    }
}

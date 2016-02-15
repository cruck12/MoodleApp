package com.sahil.moodleapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    boolean loginState = false;
    final String URL = "http://103.27.8.44:8000/";
    RequestQueue mqueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //initialize request queues
        mqueue= Volley.newRequestQueue(this.getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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


    //Login according to given userid & pass
    public void loginUser(View view){
        if(loginState){

        }
        else{
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo network = cm.getActiveNetworkInfo();
            boolean isConnected = network != null && network.isConnectedOrConnecting();
            if (isConnected) {
                final EditText editText_username =(EditText) findViewById(R.id.editText_Username);
                final EditText editText_password =(EditText) findViewById(R.id.editText_Password);
                String username = editText_username.getText().toString();
                String password = editText_password.getText().toString();
                login(username,password);
            } else {
                DialogFragment showInternet = new showInternetDialogFragment();
                showInternet.show(getFragmentManager(),"showInternet");
            }
        }
    }

    private void login(String username, String password) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,URL + "/default/login.json?userid="+username+"&password="+password,null
                ,new Response.Listener<JSONObject>(){
            @Override
            //Parse LOGIN
            public void onResponse(JSONObject response){
                try {
                    boolean success = response.getBoolean("success");
                    if(success){
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }
                catch (JSONException e){

                }
            }
        }
                ,new Response.ErrorListener() {
            @Override
            //Handle Errors
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

        mqueue.add(request);


    }

    public static class showInternetDialogFragment extends DialogFragment{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Please Connect to the Internet to continue").setTitle("Not Connected to Internet")
                    .setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener(){
                        //Cancels the dialog
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showInternetDialogFragment.this.getDialog().cancel();
                        }
                    })
                    .setPositiveButton(R.string.connect, new DialogInterface.OnClickListener() {
                        //Starts activity to open WiFi settings
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });
            return builder.create();
        }
    }

}

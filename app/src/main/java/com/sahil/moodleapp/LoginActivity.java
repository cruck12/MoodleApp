package com.sahil.moodleapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;

public class LoginActivity extends AppCompatActivity {

    boolean loginState = false;
//    final String URL = "http://103.27.8.44:8000";
    String URL;
    RequestQueue mqueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //default implementation of handling cookies
        CookieManager cookieManager= new CookieManager();
        CookieHandler.setDefault(cookieManager);

        //initialize request queues
        final MoodleAppApplication moodleAppApplication=(MoodleAppApplication) getApplicationContext();
        mqueue= moodleAppApplication.getmRequestQueue();
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

    //method to convert an IP address in int form to the general string form we all love
    //done via bitshifting multiples of 8
    public static String intToIp(int addr) {
        return  ((addr & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF));
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
                final WifiManager manager = (WifiManager) super.getSystemService(WIFI_SERVICE);
                final DhcpInfo dhcp = manager.getDhcpInfo();
                String gateway = intToIp(dhcp.gateway);
                URL = "http://"+gateway +":8000";
                final EditText editText_username =(EditText) findViewById(R.id.editText_Username);
                final EditText editText_password =(EditText) findViewById(R.id.editText_Password);
                String username = editText_username.getText().toString();
                String password = editText_password.getText().toString();
                login(username,password,gateway);
            } else {
                DialogFragment showInternet = new showInternetDialogFragment();
                showInternet.show(getFragmentManager(),"showInternet");
            }
        }
    }

    private void login(String username, String password, final String gateway) {
        JsonObjectRequest request = new JsonObjectRequest(URL + "/default/login.json?userid="+username+"&password="+password,null
                ,new Response.Listener<JSONObject>(){
            @Override
            //Parse LOGIN
            public void onResponse(JSONObject response){
                try {
                    boolean success = response.getBoolean("success");
                    JSONObject user = response.getJSONObject("user");
                    Toast.makeText(getApplicationContext(),gateway+response.getBoolean("success")+user.getString("username"), Toast.LENGTH_SHORT).show();
                    if(success)
                    {
                        //Data received from JSON response. This data is further needed in other activity, hence is shared.
                        SharedPreferences settings = getApplicationContext().getSharedPreferences("profileData",MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("firstName",user.getString("first_name"));
                        editor.putString("lastName",user.getString("last_name"));
                        editor.putString("email",user.getString("email"));
                        editor.putString("username",user.getString("username"));
                        editor.putString("entryNo",user.getString("entry_no"));
                        editor.putString("type",user.getString("type"));
                        editor.commit();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }
                catch (JSONException e){
//                    Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
                ,new Response.ErrorListener() {
            @Override
            //Handle Errors
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(),gateway+volleyError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        request.setTag("loginRequest");

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

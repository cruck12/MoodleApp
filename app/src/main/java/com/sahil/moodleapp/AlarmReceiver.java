package com.sahil.moodleapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        //obtaining the correct IP for url
        final MoodleAppApplication moodleAppApplication = (MoodleAppApplication) context.getApplicationContext();
        RequestQueue mqueue = moodleAppApplication.getmRequestQueue();

        final WifiManager manager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        final DhcpInfo dhcp = manager.getDhcpInfo();
        final String gateway = LoginActivity.intToIp(dhcp.gateway);
        String URL = "http://" + gateway + ":8000";

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        boolean isConnected = network != null && network.isConnectedOrConnecting();

        if (isConnected) {
            //Checking for notifications
            CustomJsonRequest request = new CustomJsonRequest(URL + "/default/notifications.json", null
                    , new Response.Listener<String>() {
                @Override
                //Parse LOGIN
                public void onResponse(String response1) {
                    try {
                        JSONObject response=new JSONObject(response1);
                        JSONArray notif = response.getJSONArray("notifications");
                        for (int j = 0; j < notif.length(); j++) {

                            if (notif.getJSONObject(j).getInt("is_seen")==0) {
                                //if a particular notification is unseen, display it
                                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                                String desc = notif.getJSONObject(j).getString("description");
                                String description="";
                                boolean flag=true;
                                for(int i=0;i<desc.length();i++)
                                {
                                    char ch=desc.charAt(i);
                                    if(ch=='<')
                                        flag=false;
                                    else if(ch=='>')
                                        flag=true;
                                    else if(flag)
                                        description=description+ch;
                                }
                                mBuilder.setSmallIcon(R.drawable.icon);
                                mBuilder.setContentTitle("New notification");
                                mBuilder.setContentText(description);

                                //Adding the functionality for the notification. It opens MainActivity when tapped
                                Intent resultIntent = new Intent(context, MainActivity.class);
                                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                                stackBuilder.addParentStack(MainActivity.class);
                                stackBuilder.addNextIntent(resultIntent);
                                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                                mBuilder.setContentIntent(resultPendingIntent);
                                mBuilder.setAutoCancel(true);

                                //Code for displaying the obtained notification
                                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                mNotificationManager.notify(1, mBuilder.build());
                            }

                        }
                    } catch (JSONException e) {
                        Toast.makeText(context.getApplicationContext(), e.getMessage()+"*", Toast.LENGTH_SHORT).show();
                    }
                }
            }
                    , new Response.ErrorListener() {
                @Override
                //Handle Errors
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(context.getApplicationContext(), gateway + volleyError.getMessage()+"/", Toast.LENGTH_SHORT).show();

                }
            });
            request.setTag("notifRequest");

            mqueue.add(request);
        }
    }
}

package com.sahil.moodleapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
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
    public void onReceive(final Context context, Intent intent)
    {

        final MoodleAppApplication moodleAppApplication = (MoodleAppApplication) context.getApplicationContext();
        RequestQueue mqueue = moodleAppApplication.getmRequestQueue();
        final WifiManager manager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        final DhcpInfo dhcp = manager.getDhcpInfo();
        final String gateway = LoginActivity.intToIp(dhcp.gateway);
        String URL = "http://"+gateway +":8000";


        JsonObjectRequest request = new JsonObjectRequest(URL + "/default/notifications.json",null
                ,new Response.Listener<JSONObject>(){
            @Override
            //Parse LOGIN
            public void onResponse(JSONObject response){
                try {
                    JSONArray notif= response.getJSONArray("notifications");
                    for(int j=0;j<notif.length();j++)
                    {
                        if(!(notif.getJSONObject(j).getBoolean("is_seen")))
                        {
                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                            String description=notif.getJSONObject(j).getString("description");
                            //String date=notif.getJSONObject(j).getString("created_at");
                            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                            mBuilder.setContentTitle("New notification");
                            mBuilder.setContentText(description);

                            Intent resultIntent = new Intent(context, MainActivity.class);
                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                            stackBuilder.addParentStack(MainActivity.class);
                            stackBuilder.addNextIntent(resultIntent);
                            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.setContentIntent(resultPendingIntent);
                            mBuilder.setAutoCancel(true);

                            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            mNotificationManager.notify(1, mBuilder.build());
                        }

                    }
                }
                catch (JSONException e)
                {
                    Toast.makeText(context.getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
                ,new Response.ErrorListener() {
            @Override
            //Handle Errors
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context.getApplicationContext(),gateway+volleyError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        request.setTag("notifRequest");

        mqueue.add(request);
    }
}

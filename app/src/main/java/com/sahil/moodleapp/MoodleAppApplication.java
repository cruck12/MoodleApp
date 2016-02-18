package com.sahil.moodleapp;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Sahil on 2/18/2016.
 */
public class MoodleAppApplication extends Application{
        private static RequestQueue mRequestQueue;

        @Override
        public void onCreate() {
            super.onCreate();
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        // Getter for RequestQueue or just make it public
        public RequestQueue getmRequestQueue(){
            return mRequestQueue;
        }
}

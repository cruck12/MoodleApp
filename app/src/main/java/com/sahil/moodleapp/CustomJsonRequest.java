package com.sahil.moodleapp;

import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

public class CustomJsonRequest extends StringRequest {
    //SourceCode help : http://stackoverflow.com/questions/16626032/volley-post-get-parameters?lq=1

    private Map<String, String> params;
    private Listener<String> listener;

    public CustomJsonRequest(String url, Map<String, String> params,
                             Listener<String> responseListener, ErrorListener errorListener) {
        super(Method.GET,url,responseListener, errorListener);
        this.listener = responseListener;
        this.params = params;

    }

    @Override
    public Map<String, String> getParams() throws AuthFailureError {
        return params;
    }



    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        // since we don't know which of the two underlying network vehicles
        // will Volley use, we have to handle and store session cookies manually
        MoodleAppApplication.get().checkSessionCookie(response.headers);

        return super.parseNetworkResponse(response);
    }

    /* (non-Javadoc)
     * @see com.android.volley.Request#getHeaders()
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();

        if (headers == null
                || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }

        MoodleAppApplication.get().addSessionCookie(headers);

        return headers;
    }
    @Override
    protected void deliverResponse(String response) {
        listener.onResponse(response);
    }
}

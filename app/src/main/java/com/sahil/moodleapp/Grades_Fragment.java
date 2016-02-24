package com.sahil.moodleapp;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Grades_Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Grades_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Grades_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String URL;
    private OnFragmentInteractionListener mListener;

    public Grades_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Grades_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Grades_Fragment newInstance(String param1, String param2) {
        Grades_Fragment fragment = new Grades_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        final WifiManager manager = (WifiManager) super.getActivity().getSystemService(getActivity().WIFI_SERVICE);
        final DhcpInfo dhcp = manager.getDhcpInfo();
        String gateway = LoginActivity.intToIp(dhcp.gateway);
        URL = "http://"+gateway +":8000";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_grades_, container,
                false);
        showGrades();
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void showGrades() {
        CustomJsonRequest request = new CustomJsonRequest(URL+"/grades.json",null
                ,new Response.Listener<String>(){
            @Override
            //Parse LOGIN
            public void onResponse(String response){
                FrameLayout layout = (FrameLayout) getActivity().findViewById(R.id.grades_layout);
                //the layout on which you are working
                TableLayout table = (TableLayout)getActivity().findViewById(R.id.table_Grades);
                try {
                    JSONObject response1 = new JSONObject(response);
                    JSONArray courses = response1.getJSONArray("courses");
                    JSONArray grades = response1.getJSONArray("grades");
                    for(int i=0;i<courses.length();i++){
                        JSONObject course = courses.getJSONObject(i);
                        JSONObject grade = grades.getJSONObject(i);
                        String code = course.getString("code");


                        // create a new TableRow
                        TableRow row = new TableRow(getContext());

                        // create a new TextView for showing xml data
                        TextView t1 = new TextView(getContext());
                        // set the text
                        t1.setText( code);
                        //set text color
                        int color = ContextCompat.getColor(getActivity().getApplicationContext(),R.color.textColor);
                        t1.setTextColor(color);
                        // add the TextView  to the new TableRow
                        row.addView(t1);

                        TextView t2 = new TextView(getContext());
                        t2.setText(grade.getString("name") + " " );
                        t2.setTextColor(color);
                        row.addView(t2);

                        TextView t3 = new TextView(getContext());
                        t3.setText(grade.getInt("score") + " " );
                        t3.setTextColor(color);
                        row.addView(t3);

                        TextView t4 = new TextView(getContext());
                        t4.setText(grade.getInt("out_of") + " ");
                        t4.setTextColor(color);
                        row.addView(t4);

                        // add the TableRow to the TableLayout
                        table.addView(row, new TableLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT));
                    };
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
        request.setTag("");
        final MoodleAppApplication moodleAppApplication=(MoodleAppApplication) getActivity().getApplicationContext();
        RequestQueue mqueue= moodleAppApplication.getmRequestQueue();
        mqueue.add(request);
    }
}

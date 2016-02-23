package com.sahil.moodleapp;

import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
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
 * {@link Assignments_Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Assignments_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Assignments_Fragment extends Fragment {
    String URL;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Assignments_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Assignments_Fragments.
     */
    // TODO: Rename and change types and number of parameters
    public static Assignments_Fragment newInstance(String param1, String param2) {
        Assignments_Fragment fragment = new Assignments_Fragment();
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
        View rootView = inflater.inflate(R.layout.fragment_assignments, container,
                false);
        showAssignments();
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

    private void showAssignments() {
        final MoodleAppApplication moodleAppApplication=(MoodleAppApplication) getActivity().getApplicationContext();
        RequestQueue mqueue= moodleAppApplication.getmRequestQueue();
        CustomJsonRequest request1 = new CustomJsonRequest(URL + "/courses/list.json", null
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try{
                    JSONObject response = new JSONObject(s);
                    JSONArray courses = response.getJSONArray("courses");
                    for(int i=0;i<courses.length();i++){
                        String code = courses.getJSONObject(i).getString("code");
                        showAssign(code);
                    }
                }
                catch (JSONException e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getContext(),volleyError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        mqueue.add(request1);
    }

    private void showAssign(String code) {
        CustomJsonRequest request = new CustomJsonRequest(URL + "/courses/course.json/" + code + "/assignments", null
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                RelativeLayout layout = (RelativeLayout) getActivity().findViewById(R.id.assign_rel_layout);
                try {
                    JSONObject request = new JSONObject(s);
                    JSONArray assignments = request.getJSONArray("assignments");
                    for(int i=0; i<assignments.length();i++){
                        final JSONObject assignment = assignments.getJSONObject(i);

                        String name = assignment.getString("name");

                        //set the properties for button
                        Button btnTag = new Button(getActivity().getApplicationContext());
                        btnTag.setId(i+1);
                        RelativeLayout.LayoutParams lay= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        if(i!=0){
                            lay.addRule(RelativeLayout.BELOW,i);
                        }
                        else{
                            lay.addRule(RelativeLayout.BELOW,R.id.assignment_label);
                        }
                        btnTag.setLayoutParams(lay);
                        btnTag.setText(name.split(":")[0]);

                        btnTag.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent intent = new Intent(getContext(), A1.class);
                                    intent.putExtra("createdAt", assignment.getString("created_at"));
                                    intent.putExtra("assignName", assignment.getString("name"));
                                    intent.putExtra("assignDescription", assignment.getString("description"));
                                    intent.putExtra("deadline", assignment.getString("deadline"));
                                    intent.putExtra("courseId", assignment.getInt("registered_course_id"));
                                    intent.putExtra("assignId", assignment.getInt("id"));
                                    startActivity(intent);
                                }
                                catch (JSONException e){
                                    Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        layout.addView(btnTag);
                    }
                }
                catch (JSONException e){
                    Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getContext(),volleyError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        final MoodleAppApplication moodleAppApplication=(MoodleAppApplication) getActivity().getApplicationContext();
        RequestQueue mqueue= moodleAppApplication.getmRequestQueue();
        mqueue.add(request);
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
}

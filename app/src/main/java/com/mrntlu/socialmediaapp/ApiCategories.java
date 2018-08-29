package com.mrntlu.socialmediaapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;


public class ApiCategories extends Fragment {

    View v;
    RecyclerView listView;
    ProgressBar listviewLoadProgress;
    CategoriesAdapter categoriesAdapter;
    Activity activity;
    int category;

    private RequestQueue mQueue;
    private final String API_TOKEN="481cfa6f70112be63d18faaf10a597dd";

    ArrayList<Uri> thumbLinks=new ArrayList<Uri>();
    ArrayList<Uri> imageLinks=new ArrayList<Uri>();

    public ApiCategories() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ApiCategories(Activity activity, int category) {
        this.activity = activity;
        this.category = category;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_api_categories, container, false);
        listView = (RecyclerView) v.findViewById(R.id.recyvclerView);
        listviewLoadProgress=(ProgressBar)v.findViewById(R.id.listviewLoadProgress);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        listviewLoadProgress.setVisibility(View.VISIBLE);

        categoriesAdapter = new CategoriesAdapter(getActivity(),thumbLinks,imageLinks);

        final GridLayoutManager gridLayoutManager=new GridLayoutManager(activity,2);
        listView.setLayoutManager(gridLayoutManager);
        listView.setAdapter(categoriesAdapter);
        mQueue = Volley.newRequestQueue(activity);
        jsonParser(category);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void jsonParser(int categories){
        String url="https://wall.alphacoders.com/api2.0/get.php?auth="+API_TOKEN+"&method=category&id="+categories+"&page=1&sort=rating";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONArray jsonArray = response.getJSONArray("wallpapers");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    thumbLinks.add(Uri.parse(jsonArray.getJSONObject(i).getString("url_thumb")));
                                    imageLinks.add(Uri.parse(jsonArray.getJSONObject(i).getString("url_image")));
                                }
                                categoriesAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }
}

package com.mrntlu.socialmediaapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainScreenFragments extends Fragment {

    private final String API_TOKEN="481cfa6f70112be63d18faaf10a597dd";
    private RequestQueue mQueue;
    private final String PARSE_URL="https://wall.alphacoders.com/api2.0/get.php?auth="+API_TOKEN+"&method=";//"&page="+;

    View v;
    XRecyclerView listView;
    ProgressBar listviewLoadProgress;
    CategoriesAdapter categoriesAdapter;
    Activity activity;
    int page;
    String method;

    ArrayList<Uri> thumbLinks=new ArrayList<Uri>();
    ArrayList<Uri> imageLinks=new ArrayList<Uri>();
    ArrayList<Integer> imageID=new ArrayList<Integer>();

    public MainScreenFragments() {
    }

    @SuppressLint("ValidFragment")
    public MainScreenFragments(Activity activity, String method) {
        this.activity = activity;
        this.method = method;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_api_categories, container, false);
        listView = (XRecyclerView) v.findViewById(R.id.recyvclerView);
        listviewLoadProgress=(ProgressBar)v.findViewById(R.id.listviewLoadProgress);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        listviewLoadProgress.setVisibility(View.VISIBLE);
        page=1;
        categoriesAdapter = new CategoriesAdapter(getActivity(),thumbLinks,imageLinks,listviewLoadProgress,imageID);

        final GridLayoutManager gridLayoutManager=new GridLayoutManager(activity,2);
        listView.setLayoutManager(gridLayoutManager);
        listView.setAdapter(categoriesAdapter);
        mQueue = Volley.newRequestQueue(activity);
        jsonParser(1);
        listView.setPullRefreshEnabled(false);
        listView.setLimitNumberToCallLoadMore(30);
        listView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                listView.refreshComplete();
            }

            @Override
            public void onLoadMore() {
                page++;
                jsonParser(page);
                listView.loadMoreComplete();
                if (page>=30){
                    listView.setLoadingMoreEnabled(false);
                }
            }
        });
    }

    private void jsonParser(int page){

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, PARSE_URL+method+"&page="+page, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONArray jsonArray = response.getJSONArray("wallpapers");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    thumbLinks.add(Uri.parse(jsonArray.getJSONObject(i).getString("url_thumb")));
                                    imageLinks.add(Uri.parse(jsonArray.getJSONObject(i).getString("url_image")));
                                    imageID.add(jsonArray.getJSONObject(i).getInt("id"));
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

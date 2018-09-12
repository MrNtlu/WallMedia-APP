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
import android.widget.SearchView;
import android.widget.Toast;

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

import es.dmoral.toasty.Toasty;

public class SearchFragment extends Fragment {
    private final String API_TOKEN="481cfa6f70112be63d18faaf10a597dd";
    private RequestQueue mQueue;

    View v;
    XRecyclerView listView;
    ProgressBar listviewLoadProgress;
    ApiCategoriesAdapter apiCategoriesAdapter;
    Activity activity;
    int page;
    int pageLimit;
    String searchText;

    ArrayList<Uri> thumbLinks=new ArrayList<Uri>();
    ArrayList<Uri> imageLinks=new ArrayList<Uri>();
    ArrayList<Integer> imageID=new ArrayList<Integer>();

    @SuppressLint("ValidFragment")
    public SearchFragment(Activity activity, String searchText) {
        this.activity = activity;
        this.searchText = searchText;
    }

    public SearchFragment() {
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
        apiCategoriesAdapter = new ApiCategoriesAdapter(getActivity(),thumbLinks,imageLinks,listviewLoadProgress,imageID);

        final GridLayoutManager gridLayoutManager=new GridLayoutManager(activity,2);
        listView.setLayoutManager(gridLayoutManager);
        listView.setAdapter(apiCategoriesAdapter);
        mQueue = Volley.newRequestQueue(activity);
        jsonParser(1);
        listView.setPullRefreshEnabled(false);
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
                if (page>=pageLimit/30){
                    listView.setLoadingMoreEnabled(false);
                }
            }
        });
    }

    private void jsonParser(int page){
        String url="https://wall.alphacoders.com/api2.0/get.php?auth="+API_TOKEN+"&method=search&term="+searchText+"&page="+page;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                pageLimit=response.getInt("total_match");
                                if (pageLimit<=0){
                                    Toasty.error(activity,getString(R.string.no_image_found), Toast.LENGTH_LONG).show();
                                    listviewLoadProgress.setVisibility(View.GONE);
                                }
                                JSONArray jsonArray = response.getJSONArray("wallpapers");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    thumbLinks.add(Uri.parse(jsonArray.getJSONObject(i).getString("url_thumb")));
                                    imageLinks.add(Uri.parse(jsonArray.getJSONObject(i).getString("url_image")));
                                    imageID.add(jsonArray.getJSONObject(i).getInt("id"));
                                }
                                apiCategoriesAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toasty.error(activity,getString(R.string.error_occured_2),Toast.LENGTH_SHORT).show();
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

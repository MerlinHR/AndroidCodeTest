package com.example.androidcodetest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stringRequest = createStringRequest();
        // Add the request to the RequestQueue
        queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);


    }

    private ScrollListener scrollListener;

    RecyclerViewAdapter adapter;
    RequestQueue queue;
    String test = "";
    String nextPage = "";
    String url = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=6&playlistId=UU_A--fhX5gea0i4UtpD99Gg&key=AIzaSyAzCIbbuhbfcQxuX40NyIk9f6zZLJe_lmc";
    ArrayList<String> nameList = new ArrayList<String>();
    ArrayList<String> thumbnailList = new ArrayList<String>();
    ArrayList<String> idList = new ArrayList<String>();

    JSONObject resp = new JSONObject();
    JSONArray items;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);


    // Instantiate the RequestQueue.
    public void rv(ArrayList<String> nameList) {
        RecyclerView recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RecyclerViewAdapter(this, nameList, thumbnailList);
        //adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        adapter.setClickListener(this);

        scrollListener = new ScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                createStringRequest();
                queueAdd();
            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);
    }

    public void queueAdd() {
        queue.add(stringRequest);
    }

    StringRequest stringRequest;

    StringRequest createStringRequest () {
        stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        test = response;
                        try {
                            resp = new JSONObject(response);
                            nextPage = resp.get("nextPageToken").toString();
                            items = new JSONArray(resp.get("items").toString());

                            for (int i = 0; i < items.length(); i++) {
                                JSONObject numberedItem = new JSONObject(items.get(i).toString());
                                JSONObject numberedSnippet = new JSONObject(numberedItem.get("snippet").toString());
                                String name = numberedSnippet.get("title").toString();
                                JSONObject thumbnail = new JSONObject(numberedSnippet.get("thumbnails").toString());
                                JSONObject thumbDefault = new JSONObject(thumbnail.get("high").toString());
                                JSONObject resourceId = new JSONObject(numberedSnippet.get("resourceId").toString());
                                String id = resourceId.get("videoId").toString();
                                idList.add(id);
                                nameList.add(name);
                                thumbnailList.add(thumbDefault.get("url").toString());
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        url = "https://www.googleapis.com/youtube/v3/playlistItems?" + "pageToken=" + nextPage + "&part=snippet&maxResults=50&playlistId=UU_A--fhX5gea0i4UtpD99Gg&key=AIzaSyAzCIbbuhbfcQxuX40NyIk9f6zZLJe_lmc";

                        rv(nameList);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        return stringRequest;
    }

    public void onItemClick(View view, int position) {
        String id = idList.get(position);
        Intent infoIntent = new Intent(this, InfoActivity.class);
        infoIntent.putExtra("id", id);
        startActivity(infoIntent);
    }

}

package com.example.androidcodetest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        id = id();
        url = "https://www.googleapis.com/youtube/v3/videos?id=" + id + "&part=snippet&key=AIzaSyAzCIbbuhbfcQxuX40NyIk9f6zZLJe_lmc";
        url1 = "https://www.googleapis.com/youtube/v3/videos?id=" + id + "&part=contentDetails&key=AIzaSyAzCIbbuhbfcQxuX40NyIk9f6zZLJe_lmc";
        createStringRequest();
        createStringRequest1();
        thumbnailView = findViewById(R.id.Thumbnail);
        titleView = findViewById(R.id.Title);
        durationView = findViewById(R.id.textView2);
        dateView = findViewById(R.id.textView3);
        descriptionView = findViewById(R.id.textView4);


        queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);
        queue.add(stringRequest1);

    }

    RequestQueue queue;

    String id = "";

    String url;
    String url1;
    String title;
    String description;
    String duration;
    String thumbURL;
    String publishDate;
    ImageView thumbnailView;
    TextView titleView;
    TextView durationView;
    TextView dateView;
    TextView descriptionView;

    StringRequest stringRequest;
    StringRequest stringRequest1;

    String dateConverter(String date) {
        return date.substring(0,10);
    }

    String timeConverter(String duration) {
        String convertedDuration = "";
        duration = duration.substring(2);

        if (duration.contains("H")){
            convertedDuration = convertedDuration.concat(duration.substring(0,duration.indexOf("H")) + ":");
            duration = duration.substring(duration.indexOf("H")+1);
        } else {
            convertedDuration = "00:";
        }
        if (duration.contains("M")){
            if (duration.indexOf("M")==2){
            convertedDuration = convertedDuration.concat(duration.substring(0,duration.indexOf("M"))+ ":");
            duration = duration.substring(duration.indexOf("M")+1);}
            else{
                convertedDuration = convertedDuration.concat("0" + duration.substring(0,duration.indexOf("M"))+ ":");
                duration = duration.substring(duration.indexOf("M")+1);
            }
        }
        if (duration.indexOf("S")==2){
            convertedDuration = convertedDuration.concat(duration.substring(0,duration.indexOf("S"))+ ":");
        }
        else{
            convertedDuration = convertedDuration.concat("0" + duration.substring(0,duration.indexOf("S")));
        }
        return convertedDuration;
    }

    void createStringRequest() {
        stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject resp = new JSONObject(response);
                            JSONArray items = new JSONArray(resp.get("items").toString());
                            JSONObject item = new JSONObject(items.get(0).toString());
                            JSONObject snippet = new JSONObject(item.get("snippet").toString());
                            title = snippet.get("title").toString();
                            description = snippet.get("description").toString();
                            publishDate = snippet.get("publishedAt").toString();
                            JSONObject thumbnail = new JSONObject(snippet.get("thumbnails").toString());
                            JSONObject thumbLarge = new JSONObject(thumbnail.get("standard").toString());
                            thumbURL = thumbLarge.get("url").toString();
                            String date = dateConverter(publishDate);

                            titleView.setText(title);
                            dateView.setText("Uploaded: " + date);
                            descriptionView.setText(description);


                            new DownloadImageTask(thumbnailView).execute(thumbURL);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }

    void createStringRequest1() {

    stringRequest1 = new StringRequest(Request.Method.GET, url1,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject resp = new JSONObject(response);
                        JSONArray items = new JSONArray(resp.get("items").toString());
                        JSONObject item = new JSONObject(items.get(0).toString());
                        JSONObject details = new JSONObject(item.get("contentDetails").toString());
                        duration = details.get("duration").toString();

                        durationView.setText("Duration: " + timeConverter(duration));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
        }
    });
    }


    String id() {
        Bundle extras = getIntent().getExtras();
        if (extras!= null) {
            return extras.getString("id");
        } else {
            return "";
        }
    }


}


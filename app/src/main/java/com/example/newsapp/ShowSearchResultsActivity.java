package com.example.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import java.util.List;

public class ShowSearchResultsActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private List<Article> lstArticle;
    private RequestQueue requestQueue;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        Intent i = getIntent();
        final String q = i.getStringExtra("Query");
        Log.d("QUERY",q);


        Toolbar toolbar = findViewById(R.id.search_appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search Results for " + q);


        mSwipeRefreshLayout = findViewById(R.id.swiperefresh_items);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                parseJSON(q);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);
            }
        });
        requestQueue = Volley.newRequestQueue(this);
        parseJSON(q);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


    public void parseJSON(String q) {
        lstArticle = new ArrayList<>();
        String url = "https://newsapp-backend-99.appspot.com/search?paper=0&id="+q;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("articles");
                            for(int i=0; i<jsonArray.length();i++) {
                                JSONObject article = jsonArray.getJSONObject(i);
                                String id, title, image, section, date, url, desc;
                                id = article.getString("id");
                                title = article.getString("title");
                                image = article.getString("image");
                                date = article.getString("date");
                                section = article.getString("section");
                                url = article.getString("url");
                                desc = article.getString("description");
                                lstArticle.add(new Article(id, title, image, section, date, url,desc));
                            }

                            recyclerView = (RecyclerView) findViewById(R.id.search_recyclerview);
                            RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(ShowSearchResultsActivity.this, lstArticle);
                            recyclerView.setLayoutManager(new LinearLayoutManager(ShowSearchResultsActivity.this));
                            recyclerView.addItemDecoration(new DividerItemDecoration(ShowSearchResultsActivity.this, DividerItemDecoration.VERTICAL));
                            recyclerView.setAdapter(recyclerViewAdapter);

                            progressBar = (ProgressBar) findViewById(R.id.progressBar_search);
                            progressBar.setVisibility(View.GONE);
                            TextView loadingText = (TextView) findViewById(R.id.loading_text);
                            loadingText.setVisibility(View.GONE);

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
        requestQueue.add(request);
    }
}
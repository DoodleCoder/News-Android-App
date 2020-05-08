package com.example.newsapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class FragmentTechnology extends Fragment {

    private ProgressBar progressBar;
    View v;
    private RecyclerView recyclerView;
    private List<Article> lstArticle;
    private RequestQueue requestQueue;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerViewAdapter recyclerViewAdapter;

    public FragmentTechnology() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_technology, container, false);
        mSwipeRefreshLayout = v.findViewById(R.id.swiperefresh_items);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                parseJSON();
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
        lstArticle = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        parseJSON();
        return v;
    }

    public void parseJSON() {
        String url = "https://hw8-node-backend.wl.r.appspot.com/technology/1";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for(int i=0; i<jsonArray.length();i++) {
                                JSONObject article = jsonArray.getJSONObject(i);
                                String id, title, image, section, date, url;
                                id = article.getString("id");
                                title = article.getString("title");
                                image = article.getString("image");
                                date = article.getString("date");
                                section = article.getString("section");
                                url = article.getString("url");

                                lstArticle.add(new Article(id, title, image, section, date, url,""));

                            }

                            recyclerView = (RecyclerView) v.findViewById(R.id.tech_recyclerview);
                            recyclerViewAdapter = new RecyclerViewAdapter(getContext(), lstArticle);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                            recyclerView.setAdapter(recyclerViewAdapter);

                            progressBar = (ProgressBar) v.findViewById(R.id.progressBar_technology);
                            progressBar.setVisibility(View.GONE);
                            TextView loadingText = (TextView) v.findViewById(R.id.loading_text);
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

    @Override
    public void onResume() {
        super.onResume();
        Log.d("FUNCTION", "RESUME");
        try {
            recyclerViewAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

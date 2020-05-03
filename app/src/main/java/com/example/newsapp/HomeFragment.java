package com.example.newsapp;

import android.content.SharedPreferences;
import android.icu.util.LocaleData;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.format.DateUtils;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;


public class HomeFragment extends Fragment {

    View v;
    private RecyclerView recyclerView;
    private List<Article> lstArticle;
    private RequestQueue requestQueue;
    private ProgressBar progressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public HomeFragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home, container, false);
        mSwipeRefreshLayout = v.findViewById(R.id.swiperefresh_items);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getContext(), "REFRESH", Toast.LENGTH_SHORT).show();
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
        requestQueue = Volley.newRequestQueue(getContext());
        parseJSON();
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void parseJSON() {
        Log.d("TAG","Making Request Home");
        String url = "https://hw8-node-backend.wl.r.appspot.com/app_home/1";
        LocalDateTime now = LocalDateTime.now();            //Local date time
        final ZoneId zoneId = ZoneId.of( "America/Los_Angeles" );        //Zone information
        final ZonedDateTime laNow = now.atZone( zoneId );
        final LocalDateTime localNow = laNow.toLocalDateTime();

        lstArticle.clear();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for(int i=0; i<jsonArray.length(); i++) {
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

                            recyclerView = (RecyclerView) v.findViewById(R.id.home_recyclerview);
                            RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getContext(), lstArticle);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setAdapter(recyclerViewAdapter);

                            progressBar = (ProgressBar) v.findViewById(R.id.progressBar_home);
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

}

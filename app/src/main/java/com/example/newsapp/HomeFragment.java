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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


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

    public void parseJSON() {
        Log.d("TAG","Making Request Home");
        String url = "https://content.guardianapis.com/search?order-by=newest&show-fields=starRating,headline,thumbnail,short-url&api-key=05e44db8-acd9-42de-b08c-5b2b44a4eb7f";
//        LocalDateTime ldt = LocalDateTime.now();            //Local date time
//        final ZoneId zoneId = ZoneId.of( "America/Los_Angeles" );        //Zone information
//        final ZonedDateTime latime = ldt.atZone( zoneId );
//        final String latime = ldt.toString();
//        final String l_h = latime.substring(11,13);
//        final String l_m = latime.substring(14,16);
//        final String l_s = latime.substring(17,19);
//        Log.d("NOW",l_h + l_m + l_s);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject res = response.getJSONObject("response");
                            JSONArray jsonArray = res.getJSONArray("results");
                            for(int i=0; i<10;i++) {
                                JSONObject article = jsonArray.getJSONObject(i);
                                String id, title, image, section, d, date, url;
                                id = article.getString("id");
                                title = article.getString("webTitle");
                                JSONObject fields = article.getJSONObject("fields");
                                image = fields.getString("thumbnail");
                                date = article.getString("webPublicationDate");
                                section = article.getString("sectionName");
                                url = "";

//                                LocalDateTime article_time = LocalDateTime.parse(d, DateTimeFormatter.ISO_DATE_TIME);
//                                ZonedDateTime article_time_la = article_time.atZone(zoneId);
//
//                                date = article_time_la.toString();
//
//                                String h = date.substring(11,13), m = date.substring(14,16), s = latime.substring(17,19);
//                                Log.d("TIME ID: " + (i+1), h + m + s);
//
//                                Integer hour = Integer.parseInt(l_h) - Integer.parseInt(h);
//                                Integer minute = Integer.parseInt(l_m) - Integer.parseInt(m);
//                                Integer second = Integer.parseInt(l_s) - Integer.parseInt(s);
//
//                                Log.d("ID " + (i+1) + "Hour", ""+hour);
//                                Log.d("ID " + (i+1) + "Minute", ""+second);
//                                Log.d("ID " + (i+1) + "Second", ""+minute);

//                                String hour = "", minute = "", sec = "";
//
//                                LocalDateTime article_time = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
//                                LocalDateTime tempDateTime = LocalDateTime.from( latime );
//
//                                long hours = tempDateTime.until( article_time, ChronoUnit.HOURS );
//                                tempDateTime = tempDateTime.plusHours( hours );
//
//                                long minutes = tempDateTime.until( article_time, ChronoUnit.MINUTES );
//                                tempDateTime = tempDateTime.plusMinutes( minutes );
//
//                                long seconds = tempDateTime.until( article_time, ChronoUnit.SECONDS );
//
//                                String d = hours + " " + minutes + " " + seconds;

                                lstArticle.add(new Article(id, title, image, section, date.substring(6,20), url,""));

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

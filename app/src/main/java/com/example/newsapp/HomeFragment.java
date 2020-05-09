package com.example.newsapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.util.LocaleData;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class HomeFragment extends Fragment {

    View v;
    private RecyclerView recyclerView;
    private List<Article> lstArticle;
    private RequestQueue requestQueue;
    private ProgressBar progressBar;
    private TextView loadingText;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView home_recyclerview;
    private CardView weather_card;
    private RecyclerViewAdapter recyclerViewAdapter;
    private boolean loadCards, loadWeather;
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
                parseJSON();
                getCurrentLocation();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);
            }
        });

        home_recyclerview = (RecyclerView) v.findViewById(R.id.home_recyclerview);
        weather_card = (CardView) v.findViewById(R.id.weather_card);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar_home);
        loadingText = (TextView) v.findViewById(R.id.loading_text);


        home_recyclerview.setVisibility(View.INVISIBLE);
        weather_card.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        loadingText.setVisibility(View.VISIBLE);

        loadCards = false;
        loadWeather = false;

        lstArticle = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getContext());
        getCurrentLocation();
        parseJSON();
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void parseJSON() {
        Log.d("TAG", "Making Request Home");
        String url = "https://hw8-node-backend.wl.r.appspot.com/app_home/1";

        lstArticle.clear();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject article = jsonArray.getJSONObject(i);
                                String id, title, image, section, date, url;
                                id = article.getString("id");
                                title = article.getString("title");
                                image = article.getString("image");
                                date = article.getString("date");
                                section = article.getString("section");
                                url = article.getString("url");

                                lstArticle.add(new Article(id, title, image, section, date, url, ""));
                            }

                            recyclerView = (RecyclerView) v.findViewById(R.id.home_recyclerview);
                            recyclerViewAdapter = new RecyclerViewAdapter(getContext(), lstArticle);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                            recyclerView.setAdapter(recyclerViewAdapter);

                            loadCards = true;

                            if(loadWeather && loadCards) {
                                home_recyclerview.setVisibility(View.VISIBLE);
                                weather_card.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                                loadingText.setVisibility(View.INVISIBLE);
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

    private void getCurrentLocation() {
        Log.d("CALL","getCurrentLocation()");
        @SuppressLint("RestrictedApi") final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(getActivity()).requestLocationUpdates(locationRequest, new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(getActivity()).removeLocationUpdates(this);
                if(locationRequest != null && locationResult.getLocations().size() > 0) {
                    int latestLocationIndex = locationResult.getLocations().size() - 1;
                    double lat = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                    double lon = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                    Log.d("LATITUDE", lat+"");
                    Log.d("LONGITUDE", lon+"");
                    String city = "", state = "", country = "";

                    try {
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                        city = addresses.get(0).getLocality();
                        state = addresses.get(0).getAdminArea();

                        Log.d("CITY", city);
                        Log.d("STATE", state);

                        TextView cityName = (TextView) v.findViewById(R.id.city);
                        cityName.setText(city);
                        TextView stateName = (TextView) v.findViewById(R.id.state);
                        stateName.setText(state);

                        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=81c0990250dc7be0a363e70e12e21cc9";

                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("RESPONSE_WEATHER", String.valueOf(response));
                                try {
                                    JSONObject main = response.getJSONObject("main");
                                    String temp_raw = main.getString("temp");
                                    int temp = (int) Math.rint(Double.parseDouble(temp_raw));
                                    Log.d("TEMP", "" + temp);

                                    JSONArray jsonArray = response.getJSONArray("weather");
                                    JSONObject weather_obj = jsonArray.getJSONObject(0);
                                    String summary = weather_obj.getString("main");
                                    Log.d("WEATHER", summary + "");

                                    TextView tempName = (TextView) v.findViewById(R.id.temp);
                                    tempName.setText(temp+"°C");
                                    TextView weatherName = (TextView) v.findViewById(R.id.weather);
                                    weatherName.setText(summary);

                                    loadWeather = true;

                                    LinearLayout image = v.findViewById(R.id.weather_image);

                                    switch(summary) {
                                        case "Clouds" :
                                            image.setBackgroundResource(R.drawable.cloudy_weather);
                                            break;

                                        case "Clear":
                                            image.setBackgroundResource(R.drawable.clear_weather);
                                            break;

                                        case "Snow":
                                            image.setBackgroundResource(R.drawable.snowy_weather);
                                            break;

                                        case "Drizzle":
                                        case "Rain":
                                            image.setBackgroundResource(R.drawable.rainy_weather);
                                            break;

                                        case "Thunderstorm":
                                            image.setBackgroundResource(R.drawable.thunder_weather);
                                            break;

                                        default :
                                            image.setBackgroundResource(R.drawable.sunny_weather);
                                    }

                                    loadWeather = true;

                                    if(loadWeather && loadCards) {
                                        home_recyclerview.setVisibility(View.VISIBLE);
                                        weather_card.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        loadingText.setVisibility(View.INVISIBLE);
                                    }

                                } catch (Exception e) {
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
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, Looper.getMainLooper());

    }

}

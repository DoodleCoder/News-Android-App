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
import androidx.fragment.app.Fragment;
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


public class HomeFragment extends Fragment {

    View v;
    private RecyclerView recyclerView;
    private List<Article> lstArticle;
    private RequestQueue requestQueue;
    private ProgressBar progressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private static final int PERMISSION_ID = 44;
    private FusedLocationProviderClient mFusedLocationClient;
    private double lat, lon;
    private RecyclerView home_recyclerview;
    private CardView weather_card;

    public HomeFragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home, container, false);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        mSwipeRefreshLayout = v.findViewById(R.id.swiperefresh_items);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getContext(), "REFRESH", Toast.LENGTH_SHORT).show();
                parseJSON();
                getLastLocation();
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
        home_recyclerview.setVisibility(View.INVISIBLE);

        weather_card = (CardView) v.findViewById(R.id.weather_card);
        weather_card.setVisibility(View.INVISIBLE);

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar_home);
        progressBar.setVisibility(View.VISIBLE);
        TextView loadingText = (TextView) v.findViewById(R.id.loading_text);
        loadingText.setVisibility(View.VISIBLE);


        lstArticle = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getContext());
        getLastLocation();
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
                            RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getContext(), lstArticle);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setAdapter(recyclerViewAdapter);

                            home_recyclerview = (RecyclerView) v.findViewById(R.id.home_recyclerview);
                            home_recyclerview.setVisibility(View.VISIBLE);

                            weather_card = (CardView) v.findViewById(R.id.weather_card);
                            weather_card.setVisibility(View.VISIBLE);

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


    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    Log.d("LATITUDE1", location.getLatitude() + "");
                                    Log.d("LONGITUDE1", location.getLongitude() + "");
                                    lat = location.getLatitude();
                                    lon = location.getLongitude();

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

                                                    LinearLayout image = v.findViewById(R.id.weather_image);

                                                    switch(summary) {
                                                        case "Clouds" : {
                                                            image.setBackgroundResource(R.drawable.cloudy_weather);
                                                            break;
                                                        }
                                                        case "Clear": {
                                                            image.setBackgroundResource(R.drawable.clear_weather);
                                                            break;
                                                        }
                                                        case "Snow": {
                                                            image.setBackgroundResource(R.drawable.snowy_weather);
                                                            break;
                                                        }
                                                        case "Rain/Drizzle": {
                                                            image.setBackgroundResource(R.drawable.rainy_weather);
                                                            break;
                                                        }
                                                        case "Thunderstorm": {
                                                            image.setBackgroundResource(R.drawable.thunder_weather);
                                                            break;
                                                        }
                                                        default : {
                                                            image.setBackgroundResource(R.drawable.sunny_weather);
                                                        }
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
                        }
                );
            } else {
                Toast.makeText(getContext(), "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        @SuppressLint("RestrictedApi") LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

}

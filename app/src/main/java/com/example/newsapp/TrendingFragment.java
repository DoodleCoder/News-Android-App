package com.example.newsapp;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TrendingFragment extends Fragment {

    private List<Entry> data;
    private String url = "https://hw8-node-backend.wl.r.appspot.com/trends?query=";
    private RequestQueue requestQueue;
    private String query;
    private View v;
    private LineChart chart;
    private EditText editText;

    public TrendingFragment() {
    }

    @SuppressLint("LongLogTag")
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_trending, container, false);

        chart = (LineChart) v.findViewById(R.id.chart);

        jsonParse("Coronavirus");

        editText = (EditText) v.findViewById(R.id.query);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEND){
                        String text = v.getText().toString();
                        jsonParse(text);
                        return true;
                    }
                    else if(event!=null) {
                        if(KeyEvent.KEYCODE_ENTER == event.getKeyCode() && event.getAction() == KeyEvent.ACTION_DOWN) {
                            String text = v.getText().toString();
                            jsonParse(text);
                            return true;
                        }
                    }
                    else if(event.getAction() == KeyEvent.ACTION_DOWN && (KeyEvent.KEYCODE_DPAD_CENTER == actionId || KeyEvent.KEYCODE_ENTER == actionId)) {
                        String text = v.getText().toString();
                        jsonParse(text);
                        return true;
                    }
                    else
                        return false;
                } catch (Exception e) {}
                return false;
            }
        });

        return v;
    }

    public void jsonParse(final String query) {
        Log.d("JSONPARSE",query);
//        data.clear();
        data = new ArrayList<Entry>();
        requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url + query, null, new Response.Listener<JSONObject>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(JSONObject response) {
                try {
//                    chart.invalidate();
                    JSONArray jsonArray = response.getJSONArray("data");
                    for(int i=0; i<jsonArray.length(); i++) {
                        JSONObject dataPoint = (JSONObject) jsonArray.get(i);
                        data.add(new Entry(i+1, dataPoint.getInt("value")));
                    }
                    Log.d("WHOLE DATA IN JSONPARSE ",data.toString());
                    LineDataSet dataSet = new LineDataSet(data,"Trending Chart for " + query);
                    dataSet.setColor(Color.parseColor("#BB86FC"));
                    LineData lineData = new LineData(dataSet);

                    Legend legend = chart.getLegend();
                    legend.setTextSize(15f);
                    legend.setTextColor(Color.BLACK);

                    dataSet.setCircleColor(Color.parseColor("#BB86FC"));
                    dataSet.setCircleHoleColor(Color.parseColor("#BB86FC"));
                    dataSet.setValueTextColor(Color.parseColor("#BB86FC"));

                    chart.setDrawGridBackground(false);
                    chart.getAxisLeft().setDrawGridLines(false);
                    chart.getAxisRight().setDrawGridLines(false);

                    chart.getXAxis().setDrawGridLines(false);

                    chart.setData(lineData);
                    chart.invalidate();
//                    lineData.notifyDataChanged();
//                    chart.notifyDataSetChanged();


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
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onPause() {
        super.onPause();
        editText.setText("");
    }

    @Override
    public void onStop() {
        super.onStop();
        editText.setText("");
    }
}

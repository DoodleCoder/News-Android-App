package com.example.newsapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class BookmarksFragment extends Fragment {

    View v;
    private RecyclerView recyclerView;
    private List<Article> lstArticle;
    private ProgressBar progressBar;
    SharedPreferences mPref;
    SharedPreferences.Editor editor;


    public BookmarksFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        lstArticle = new ArrayList<>();
        mPref = getActivity().getApplicationContext().getSharedPreferences("MyPrefs", 0);
        parseJSON();
        return v;
    }

    public void parseJSON() {
        Map<String, ?> allEntries = mPref.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Gson gson = new Gson();
            Article a = gson.fromJson(entry.getValue().toString(), Article.class);
            lstArticle.add(a);
        }
        if(lstArticle.size() > 0) {
            recyclerView = (RecyclerView) v.findViewById(R.id.bookmarks_recyclerview);
            RecyclerViewAdapterBookmarks recyclerViewAdapter = new RecyclerViewAdapterBookmarks(getContext(), lstArticle);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerViewAdapter.notifyDataSetChanged();

            v.findViewById(R.id.empty_text).setVisibility(View.GONE);
        }
        else {
            v.findViewById(R.id.empty_text).setVisibility(View.VISIBLE);
        }
    }
}
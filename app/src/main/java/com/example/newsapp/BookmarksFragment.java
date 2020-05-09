package com.example.newsapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
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
    RecyclerViewAdapterBookmarks recyclerViewAdapter;
    TextView tv_empty;

    public BookmarksFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        tv_empty = v.findViewById(R.id.empty_text);
        mPref = getActivity().getApplicationContext().getSharedPreferences("MyPrefs", 0);
        parseJSON();
        return v;
    }

    public void parseJSON() {
        lstArticle = new ArrayList<>();
        Map<String, ?> allEntries = mPref.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Gson gson = new Gson();
            Article a = gson.fromJson(entry.getValue().toString(), Article.class);
            lstArticle.add(a);
        }
        recyclerView = (RecyclerView) v.findViewById(R.id.bookmarks_recyclerview);
        recyclerViewAdapter = new RecyclerViewAdapterBookmarks(v.getContext(), lstArticle, tv_empty);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(recyclerViewAdapter);

        if(lstArticle.size() > 0) {
            v.findViewById(R.id.empty_text).setVisibility(View.GONE);
            v.findViewById(R.id.bookmarks_recyclerview).setVisibility(View.VISIBLE);
        }
        else {
            v.findViewById(R.id.empty_text).setVisibility(View.VISIBLE);
            v.findViewById(R.id.bookmarks_recyclerview).setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("FUNCTION", "RESUME");
        parseJSON();
//        if (lstArticle.size() == 0) {
//            v.findViewById(R.id.empty_text).setVisibility(View.VISIBLE);
//        } else {
//            v.findViewById(R.id.empty_text).setVisibility(View.INVISIBLE);
//        }
    }

}
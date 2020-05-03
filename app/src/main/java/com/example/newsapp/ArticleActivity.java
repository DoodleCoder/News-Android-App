package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class ArticleActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    private String id;
    private Article a;
    private ProgressBar progressBar;
    SharedPreferences mPrefs;
    SharedPreferences.Editor editor;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.appbar_bookmark)
        {
            if(mPrefs.getString(a.getId(),"").length() != 0) {
                item.setIcon(ContextCompat.getDrawable(this, R.drawable.baseline_bookmark_border_black_24dp));
                editor.remove(a.getId());
                editor.commit();
                Toast.makeText(this,"\"" + a.getTitle() + "\" was removed from bookmarks",Toast.LENGTH_LONG).show();
            }
            else {
                item.setIcon(ContextCompat.getDrawable(this, R.drawable.baseline_bookmark_black_24dp));
                Gson gson = new Gson();
                String json = gson.toJson(a);
                editor.putString(a.getId(),json);
                editor.commit();
                Toast.makeText(this,"\"" + a.getTitle() + "\" was added to bookmarks",Toast.LENGTH_LONG).show();
            }
        }
        else if(id == R.id.appbar_twitter)
        {
            String text = "Check out this link: ";
            String url = "http://www.twitter.com/intent/tweet?url=" + a.getUrl() + "&text=" + text + "&hashtags=CSCI571NewsSearch";
            Intent j = new Intent(Intent.ACTION_VIEW);
            j.setData(Uri.parse(url));
            startActivity(j);
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar_menu_article, menu);
        MenuItem item = menu.getItem(0);
        if(mPrefs.getString(id,"").length() == 0) {
            item.setIcon(R.drawable.baseline_bookmark_border_black_24dp);
        }
        else {
            item.setIcon(R.drawable.baseline_bookmark_black_24dp);
        }
        item.setIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.bookmarkRed)));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article);

        CardView article_card = (CardView) findViewById(R.id.article_card);
        article_card.setVisibility(View.INVISIBLE);

        progressBar = (ProgressBar) findViewById(R.id.progressBar_article_page);
        progressBar.setVisibility(View.VISIBLE);

        TextView loadingText = (TextView) findViewById(R.id.loading_text_article_page);
        loadingText.setVisibility(View.VISIBLE);


        mPrefs = getSharedPreferences("MyPrefs",0);
        editor = mPrefs.edit();

        Toolbar toolbar = findViewById(R.id.app_bar_article);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        Intent i = getIntent();
        id = i.getStringExtra("ID");
        parseJSON(id);

        progressBar.setVisibility(View.INVISIBLE);
        loadingText.setVisibility(View.INVISIBLE);
    }

    public void parseJSON(String id) {
//        Log.d("IN","PARSEJSON FN" + id);
        mQueue = Volley.newRequestQueue(this);
        String url = "https://hw8-node-backend.wl.r.appspot.com/article/1?id=" + id;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");
//                    Log.d("DATA",data.getString("id"));
                    a = new Article(
                            data.getString("id"),
                            data.getString("title"),
                            data.getString("image"),
                            data.getString("section"),
                            data.getString("date"),
                            data.getString("url"),
                            data.getString("part1") + data.getString("part2")
                    );

                    TextView tv_title = (TextView) findViewById(R.id.article_page_title);
                    TextView tv_date = (TextView) findViewById(R.id.article_page_date);
                    TextView tv_desc = (TextView) findViewById(R.id.article_page_desc);
                    TextView tv_section = (TextView) findViewById(R.id.article_page_section);
                    ImageView iv = (ImageView) findViewById(R.id.article_page_image);
                    Button button = (Button) findViewById(R.id.article_page_button);
                    getSupportActionBar().setTitle(a.getTitle().substring(0,35) + "...");

                    tv_title.setText(a.getTitle());
                    tv_date.setText(a.getDate());
                    tv_desc.setText(a.getDesc());
                    tv_section.setText(a.getSection());
                    Picasso.with(getApplicationContext()).load(a.getImage()).fit().centerCrop().into(iv);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent open_page = new Intent(Intent.ACTION_VIEW);
                            open_page.setData(Uri.parse(a.getUrl()));
                            startActivity(open_page);
                        }
                    });
                    CardView article_card = (CardView) findViewById(R.id.article_card);
                    article_card.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(request);
    }

}

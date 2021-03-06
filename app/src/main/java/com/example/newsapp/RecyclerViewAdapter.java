package com.example.newsapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.TimeZone;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    Context mContext;
    List<Article> mData;
    Dialog myDialog;
    SharedPreferences mPrefs;
    SharedPreferences.Editor editor;

    public RecyclerViewAdapter(Context mContext, List<Article> mData) {
        this.mContext = mContext;
        this.mData = mData;
        this.mPrefs = mContext.getSharedPreferences("MyPrefs", 0);
        editor = mPrefs.edit();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.article,parent,false);
        final MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final Article a = mData.get(position);

        holder.title.setText(a.getTitle());

        String date = a.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            LocalDateTime published = LocalDateTime.ofInstant(Instant.parse(date), ZoneId.of("America/Los_Angeles"));
            LocalDateTime current = LocalDateTime.now();
            long minutes = ChronoUnit.MINUTES.between(published, current);
            long hours = ChronoUnit.HOURS.between(published, current);
            long seconds = ChronoUnit.SECONDS.between(published, current);

            if(seconds < 0) {
                seconds += 40;
                if(seconds > 60)
                    minutes += 1;
            }

            long days = ChronoUnit.DAYS.between(published, current);

            String write_date = "";
            if (days > 0)
                write_date = days + "d ago";
            else if(hours > 0)
                write_date = hours + "h ago";
            else if(minutes > 0)
                write_date = minutes + "m ago";
            else
//                if(seconds > 0)
                write_date = seconds + "s ago";
//            else {
//                long time = sdf.parse(date).getTime();
//                long now = System.currentTimeMillis();
//                String ago = (String) DateUtils.getRelativeTimeSpanString(time, now, DateUtils.SECOND_IN_MILLIS);
//                ago.replace(" second","s");
//                ago.replace(" seconds","s");
//                Log.d("DIFF1", (String) ago);
//                write_date = ago;
//            }
//            Log.d("DIFF(D,H,M,S)","("+days+","+hours+","+minutes+","+seconds+")");
//            Log.d("DIFF2",write_date);
//            String dateFinal = "", date = (String) ago;
////            int pos = 1;
////            if(date.charAt(pos) == ' ')
////                pos = 1;
////            else
////                pos = 2;
////
////            dateFinal += (date.substring(0,pos));
////
////            if(date.charAt(pos) == 'h') {
////                dateFinal += "h ago";
////            }
////            else if(date.charAt(pos) == 'm') {
////                dateFinal += "m ago";
////            }
////            else {
////                dateFinal += "s ago";
////            }
//            date.replace(" second","s");
//            date.replace(" seconds","s");
//            date.replace(" minute","m");
//            date.replace(" minutes","m");
//            date.replace(" hour","h");
//            date.replace(" hours","h");

            holder.date.setText(write_date);

        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.section.setText(a.getSection());
        String Imageurl = a.getImage();
        Picasso.with(mContext).load(Imageurl).fit().centerCrop().into(holder.image);

        if(mPrefs.getString(a.getId(),"").length() == 0) {
            holder.bookmark.setImageResource(R.drawable.baseline_bookmark_border_black_24dp);
        }
        else {
            holder.bookmark.setImageResource(R.drawable.baseline_bookmark_black_24dp);
        }

        holder.item_article.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), ArticleActivity.class);
                    i.putExtra("ID",a.getId());
                    mContext.startActivity(i);
            }
        });

        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPrefs.getString(a.getId(),"").length() != 0) {
                    holder.bookmark.setImageResource(R.drawable.baseline_bookmark_border_black_24dp);
                    editor.remove(a.getId());
                    editor.commit();
                    Toast.makeText(mContext,"\"" + a.getTitle() + "\" was removed from bookmarks",Toast.LENGTH_LONG).show();
                }
                else {
                    holder.bookmark.setImageResource(R.drawable.baseline_bookmark_black_24dp);
                    Gson gson = new Gson();
                    String json = gson.toJson(a);
                    editor.putString(a.getId(),json);
                    editor.commit();
                    Toast.makeText(mContext,"\"" + a.getTitle() + "\" was added to bookmarks",Toast.LENGTH_LONG).show();
                }
            }
        });

        myDialog = new Dialog(mContext);
        myDialog.setContentView(R.layout.article_dialog);
        // Open dialog
        holder.item_article.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TextView dialog_title_tv = (TextView) myDialog.findViewById(R.id.dialog_title);
                ImageView dialog_image_iv = (ImageView) myDialog.findViewById(R.id.dialog_image);
                ImageView dialog_image_twitter = (ImageView) myDialog.findViewById(R.id.dialog_twitter);
                final ImageView dialog_image_bookmark = (ImageView) myDialog.findViewById(R.id.dialog_bookmark);
                final String id = mData.get(holder.getAdapterPosition()).getId();

                ImageViewCompat.setImageTintList(dialog_image_bookmark, ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.bookmarkRed)));

                dialog_title_tv.setText(mData.get(holder.getAdapterPosition()).getTitle());
                Picasso.with(mContext).load(mData.get(holder.getAdapterPosition()).getImage()).fit().centerInside().into(dialog_image_iv);

                if(mPrefs.getString(id,"").length() != 0) {
                    dialog_image_bookmark.setImageResource(R.drawable.baseline_bookmark_black_24dp);
                }
                else {
                    dialog_image_bookmark.setImageResource(R.drawable.baseline_bookmark_border_black_24dp);
                }

                dialog_image_bookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (mPrefs.getString(id, "").length() != 0) {
                                //remove
                                editor.remove(id);
                                editor.commit();
                                dialog_image_bookmark.setImageResource(R.drawable.baseline_bookmark_border_black_24dp);
                                holder.bookmark.setImageResource(R.drawable.baseline_bookmark_border_black_24dp);
                                Toast.makeText(mContext, "\"" + mData.get(holder.getAdapterPosition()).getTitle() + "\" was removed from bookmarks", Toast.LENGTH_LONG).show();
                            } else {
                                //add
                                Gson gson = new Gson();
                                String json = gson.toJson(mData.get(holder.getAdapterPosition()));
                                editor.putString(id, json);
                                editor.commit();
                                dialog_image_bookmark.setImageResource(R.drawable.baseline_bookmark_black_24dp);
                                holder.bookmark.setImageResource(R.drawable.baseline_bookmark_black_24dp);
                                Toast.makeText(mContext, "\"" + mData.get(holder.getAdapterPosition()).getTitle() + "\" was added to bookmarks", Toast.LENGTH_LONG).show();
                            }
                        }catch (Exception e) {
                            Toast.makeText(mContext, "Could not find the requested article",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
                dialog_image_twitter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = "Check out this link: ";
                        String url = "http://www.twitter.com/intent/tweet?url=" + mData.get(holder.getAdapterPosition()).getUrl() + "&text=" + text + "&hashtags=CSCI571NewsSearch";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        mContext.startActivity(i);
                    }
                });
                myDialog.show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView title, section, date;
        private ImageView image;
        private ImageView bookmark;
        private CardView item_article;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            item_article = (CardView) itemView.findViewById(R.id.article_item);
            title = (TextView) itemView.findViewById(R.id.article_title);
            section = (TextView) itemView.findViewById(R.id.article_section);
            date = (TextView) itemView.findViewById(R.id.article_date);
            image = (ImageView) itemView.findViewById(R.id.article_image);
            bookmark = (ImageView) itemView.findViewById(R.id.article_bookmark);
        }
    }
}

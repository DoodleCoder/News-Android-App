package com.example.newsapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;



public class RecyclerViewAdapterBookmarks extends RecyclerView.Adapter<RecyclerViewAdapterBookmarks.MyViewHolder> {

    Context mContext;
    List<Article> mData;
    Dialog myDialog;
    SharedPreferences mPrefs;
    SharedPreferences.Editor editor;


    public RecyclerViewAdapterBookmarks(Context mContext, List<Article> mData) {
        this.mContext = mContext;
        this.mData = mData;
        this.mPrefs = mContext.getSharedPreferences("MyPrefs", 0);
        editor = mPrefs.edit();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.bookmark, parent, false);
        final MyViewHolder myViewHolder = new MyViewHolder(v);

        // Open detail page
        myViewHolder.item_article.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ArticleActivity.class);
                i.putExtra("ID", mData.get(myViewHolder.getAdapterPosition()).getId());
                mContext.startActivity(i);
            }
        });


        myDialog = new Dialog(mContext);
        myDialog.setContentView(R.layout.article_dialog);
        // Open dialog
        myViewHolder.item_article.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TextView dialog_title_tv = (TextView) myDialog.findViewById(R.id.dialog_title);
                ImageView dialog_image_iv = (ImageView) myDialog.findViewById(R.id.dialog_image);
                ImageView dialog_image_twitter = (ImageView) myDialog.findViewById(R.id.dialog_twitter);
                final ImageView dialog_image_bookmark = (ImageView) myDialog.findViewById(R.id.dialog_bookmark);
                final String id = mData.get(myViewHolder.getAdapterPosition()).getId();

                dialog_title_tv.setText(mData.get(myViewHolder.getAdapterPosition()).getTitle());
                Picasso.with(mContext).load(mData.get(myViewHolder.getAdapterPosition()).getImage()).fit().centerCrop().into(dialog_image_iv);

                dialog_image_bookmark.setImageResource(R.drawable.baseline_bookmark_black_24dp);
                ImageViewCompat.setImageTintList(dialog_image_bookmark, ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.bookmarkRed)));

                dialog_image_bookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            //remove
                            Toast.makeText(mContext,"\"" + mData.get(myViewHolder.getAdapterPosition()).getTitle() + "\" was removed from bookmarks",Toast.LENGTH_LONG).show();
                            editor.remove(id);
                            editor.commit();
                            mData.remove(myViewHolder.getAdapterPosition());
                            myDialog.dismiss();
                            notifyDataSetChanged();
                    }
                });
                dialog_image_twitter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = "Check out this link: ";
                        String url = "http://www.twitter.com/intent/tweet?url=" + mData.get(myViewHolder.getAdapterPosition()).getUrl() + "&text=" + text + "&hashtags=CSCI571NewsSearch";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        mContext.startActivity(i);
                    }
                });
                myDialog.show();
                return true;
            }
        });


        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final Article a = mData.get(position);
        holder.title.setText(a.getTitle());
        String date = a.getDate();
        String m = "";
        switch(date.substring(5,7)) {
            case "01":
                m = "Jan";
                break;
            case "02":
                m = "Feb";
                break;
            case "03":
                m = "Mar";
                break;
            case "04":
                m = "Apr";
                break;
            case "05":
                m = "May";
                break;
            case "06":
                m = "Jun";
                break;
            case "07":
                m = "July";
                break;
            case "08":
                m = "Aug";
                break;
            case "09":
                m = "Sep";
                break;
            case "10":
                m = "Oct";
                break;
            case "11":
                m = "Nov";
                break;
            case "12":
                m = "Dec";
                break;
            default:
                m = "DEF";
        }
        holder.date.setText(date.substring(8,10) + " " + m);
        holder.section.setText(a.getSection());
        holder.bookmark.setImageResource(R.drawable.baseline_bookmark_black_24dp);

        String Imageurl = a.getImage();
        Picasso.with(mContext).load(Imageurl).fit().centerCrop().into(holder.image);

        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"\"" + a.getTitle() + "\" was removed from bookmarks",Toast.LENGTH_LONG).show();
                editor.remove(a.getId());
                editor.commit();
                mData.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView title, section, date, empty;
        private ImageView image;
        private ImageView bookmark;
        private CardView item_article;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            item_article = (CardView) itemView.findViewById(R.id.bookmark_card);
            title = (TextView) itemView.findViewById(R.id.bookmark_card_title);
            section = (TextView) itemView.findViewById(R.id.bookmark_card_section);
            date = (TextView) itemView.findViewById(R.id.bookmark_card_date);
            empty = (TextView) itemView.findViewById(R.id.empty_text);
            image = (ImageView) itemView.findViewById(R.id.bookmark_card_image);
            bookmark = (ImageView) itemView.findViewById(R.id.bookmark_card_bookmark);
        }
    }
}

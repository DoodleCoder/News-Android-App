package com.example.newsapp;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    Context mContext;
    List<Article> mData;
    Dialog myDialog;

//    SharedPreferences pref = mContext.getSharedPreferences("MyPref", 0); // 0 - for private mode
//    SharedPreferences.Editor editor = pref.edit();

    public RecyclerViewAdapter(Context mContext, List<Article> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.article,parent,false);
        final MyViewHolder myViewHolder = new MyViewHolder(v);

        //Try SharedPref
//        try{
//
//            Log.d("TRY",pref.getString("id"));
//
//        }catch(Exception e)
//        {}

        //Bookmark
        myViewHolder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Bookmarked item: " + String.valueOf(myViewHolder.getAdapterPosition()), Toast.LENGTH_LONG).show();
            }
        });

        // Open detail page
        myViewHolder.item_article.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Test click will create intent" + String.valueOf(myViewHolder.getAdapterPosition()), Toast.LENGTH_LONG).show();
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
                ImageView dialog_image_bookmark = (ImageView) myDialog.findViewById(R.id.dialog_bookmark);

                dialog_title_tv.setText(mData.get(myViewHolder.getAdapterPosition()).getTitle());
                Picasso.with(mContext).load(mData.get(myViewHolder.getAdapterPosition()).getImage()).fit().centerCrop().into(dialog_image_iv);
                dialog_image_bookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,"Bookmark Toggle for Item: " + mData.get(myViewHolder.getAdapterPosition()).getTitle(), Toast.LENGTH_SHORT).show();
                    }
                });
                dialog_image_twitter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,"Tweeted Item: " + mData.get(myViewHolder.getAdapterPosition()).getTitle(), Toast.LENGTH_SHORT).show();
                    }
                });
                myDialog.show();
                return true;
            }
        });


        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.title.setText(mData.get(position).getTitle());
        holder.date.setText(mData.get(position).getDate());
        holder.section.setText(mData.get(position).getSection());
//        holder.image.setImageResource(mData.get(position).getImage());
        String Imageurl = mData.get(position).getImage();

        Picasso.with(mContext).load(Imageurl).fit().centerCrop().into(holder.image);
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

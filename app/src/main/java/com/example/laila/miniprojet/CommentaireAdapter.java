package com.example.laila.miniprojet;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentaireAdapter extends RecyclerView.Adapter<CommentaireAdapter.ExampleViewHolder> {
    private ArrayList<Comments> mExampleList;

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imgAuth);
            mTextView1 = itemView.findViewById(R.id.tag);
            mTextView2 = itemView.findViewById(R.id.textView4);
        }
    }

    public CommentaireAdapter(ArrayList<Comments> exampleList) {
        mExampleList = exampleList;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.commentaire, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        Comments currentItem = mExampleList.get(position);
        //holder.mImageView.setImageResource(R.drawable.ic_menu_share);
        Context context =holder.mImageView.getContext();
        Picasso.with(context).load(currentItem.getImage()).into(holder.mImageView);

        holder.mTextView1.setText(currentItem.auteur);
        holder.mTextView2.setText(currentItem.contenu);
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}
package com.udacity.ramshasaeed.redditapp.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.udacity.ramshasaeed.redditapp.R;
import com.udacity.ramshasaeed.redditapp.databinding.CommentItemBinding;
import com.udacity.ramshasaeed.redditapp.model.Comment;


import java.util.ArrayList;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {
    private static String LOG_TAG = CommentAdapter.class.getSimpleName();
    private ArrayList<Comment> list;
    private Context context;
    CommentHolder holder;

    public CommentAdapter(Context context, ArrayList<Comment> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, null);
        return new CommentHolder(v);
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {
        this.holder = holder;
        this.holder.bindViews(this.list.get(position));
    }

    public void clearAdapter() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class CommentHolder extends RecyclerView.ViewHolder{

        protected TextView author, body, postedOn, points;
        LinearLayout container;
        RelativeLayout levelIndicator;
        CommentItemBinding bi;


        public CommentHolder(View view) {
            super(view);
            bi = DataBindingUtil.bind(view);
        }


        public void bindViews(Comment comment) {
            bi.author.setText(comment.getAuthor());
            bi.points.setText(comment.getPoints() + " Points");
            bi.body.setText(comment.getHtmlText());
            bi.postedOn.setText(comment.getPostedOn());
            bi.cardLinear.setPadding(comment.getLevel() * 20, 0, 0, 0);

            switch (comment.getLevel()) {
                case 0:
                    bi.view.setBackgroundColor(Color.parseColor("#ff0000"));
                    break;
                case 1:
                    bi.view.setBackgroundColor(Color.parseColor("#257425"));
                    break;
                case 2:
                    bi.view.setBackgroundColor(Color.parseColor("#99ccff"));
                    break;
                case 3:
                    bi.view.setBackgroundColor(Color.parseColor("#ea6459"));
                    break;
                case 4:
                    bi.view.setBackgroundColor(Color.parseColor("#dd3c85"));
                    break;
                case 5:
                    bi.view.setBackgroundColor(Color.parseColor("#808080"));
                    break;

            }

        }


    }
}
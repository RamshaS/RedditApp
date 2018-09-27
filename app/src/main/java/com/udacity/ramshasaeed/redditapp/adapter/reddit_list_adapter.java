package com.udacity.ramshasaeed.redditapp.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.udacity.ramshasaeed.redditapp.R;
import com.udacity.ramshasaeed.redditapp.databinding.AdapterRedditListBinding;
import com.udacity.ramshasaeed.redditapp.model.Reddit;

import java.util.List;

public class reddit_list_adapter extends RecyclerView.Adapter<reddit_list_adapter.ListRowViewHolder>{
    private static String LOG_TAG = reddit_list_adapter.class.getSimpleName();

    private List<Reddit> list;
    private Context context;
    OnItemClickListener itemClickListener;
    ListRowViewHolder holder;

    private int focusedItem = 0;

    public reddit_list_adapter(Context context, List<Reddit> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ListRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_reddit_list,parent,false);

        return new ListRowViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListRowViewHolder holder, int position) {
        this.holder = holder;
        this.holder.bindViews(this.list.get(position));

    }

    @Override
    public int getItemCount() {
        return list.size() > 0 ? list.size() : 0;
    }

    public class ListRowViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        protected TextView title;
        protected TextView url;
        protected RelativeLayout recLayout;
        protected TextView author;
        protected TextView subreddit, score, comments;
       AdapterRedditListBinding bi;
        public ListRowViewHolder(View itemView) {
           super(itemView);
           bi = DataBindingUtil.bind(itemView);
           itemView.setClickable(true);
           itemView.setOnClickListener(this);
       }

       @Override
       public void onClick(View view) {
           if(itemClickListener != null){
               itemClickListener.onItemClick(view,getAdapterPosition());
           }
       }
        public void bindViews(Reddit reddit){
            holder.title.setText(Html.fromHtml(reddit.getTitle()));
            holder.subreddit.setText("r/"+Html.fromHtml(reddit.getSubreddit()));
            holder.comments.setText(Html.fromHtml(String.valueOf(reddit.getNumComments())));
            holder.score.setText(Html.fromHtml(String.valueOf(reddit.getScore())));

        }
   }
   public interface OnItemClickListener{
        public void onItemClick(View view, int position);
   }

    public List<Reddit> getListItems(){
        return list;
    }
    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    public void clearAdapter(){
        if(list != null){
            list.clear();
            notifyDataSetChanged();
        }
    }
}

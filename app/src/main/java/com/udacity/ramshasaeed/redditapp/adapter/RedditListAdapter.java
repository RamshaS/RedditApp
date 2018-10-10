package com.udacity.ramshasaeed.redditapp.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.udacity.ramshasaeed.redditapp.R;
import com.udacity.ramshasaeed.redditapp.databinding.AdapterRedditListBinding;
import com.udacity.ramshasaeed.redditapp.model.Reddit;

import java.util.List;

import static android.view.View.GONE;

public class RedditListAdapter extends RecyclerView.Adapter<RedditListAdapter.ListRowViewHolder>{


    private List<Reddit> list;
    private Context context;
    OnItemClickListener itemClickListener;
    ListRowViewHolder holder;

    private int focusedItem = 0;

    public RedditListAdapter(Context context, List<Reddit> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ListRowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_reddit_list,parent,false);

        return new ListRowViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListRowViewHolder holder, int position) {

        this.holder = holder;
        this.holder.itemView.setSelected(focusedItem == position);
        this.holder.bindViews(this.list.get(position));

    }

    @Override
    public int getItemCount() {
        return list.size() > 0 ? list.size() : 0;
    }

    public class ListRowViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
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
            bi.tvTitle.setText(Html.fromHtml(reddit.getTitle()));
            bi.tvContent.setText(Html.fromHtml(reddit.getSubreddit()));
            bi.tvComments.setText(String.valueOf(reddit.getNumComments()));
            bi.tvScore.setText(String.valueOf(reddit.getScore()));
            Glide.with(context)
                    .load(reddit.getImageUrl())
                    .apply(new RequestOptions().override(bi.ivItemImage.getWidth(),bi.ivItemImage.getHeight()).error(R.drawable.nointernet).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            bi.imgloadprogress.setVisibility(GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            bi.imgloadprogress.setVisibility(GONE);

                            return false;
                        }
                    })
                    .into(bi.ivItemImage);
        }
   }
   public interface OnItemClickListener{
        void onItemClick(View view, int position);
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

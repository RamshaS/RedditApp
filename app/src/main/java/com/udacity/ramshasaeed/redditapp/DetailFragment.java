package com.udacity.ramshasaeed.redditapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.udacity.ramshasaeed.redditapp.adapter.CommentAdapter;
import com.udacity.ramshasaeed.redditapp.analytics.AnalyticsApplication;
import com.udacity.ramshasaeed.redditapp.database.FavContract;
import com.udacity.ramshasaeed.redditapp.databinding.FragmentDetailBinding;
import com.udacity.ramshasaeed.redditapp.model.Comment;
import com.udacity.ramshasaeed.redditapp.util.CommentProcessor;
import com.udacity.ramshasaeed.redditapp.util.Constants;

import java.util.ArrayList;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static String LOG_TAG = DetailFragment.class.getSimpleName();
    Boolean isFavourite;
    String id;

   FragmentDetailBinding bi;

    Parcelable mListState;
    Bundle extras;
    private Tracker mTracker;

    RecyclerView.LayoutManager mLayoutManager;


    CommentProcessor processor;
    ArrayList<Comment> comments;

    CommentAdapter commentAdapter;

    public DetailFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public void onResume() {
        super.onResume();
        mTracker.setScreenName("Image~" + "Detail Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bi = DataBindingUtil.inflate(inflater,R.layout.fragment_detail,container,false);

        final View rootView = bi.getRoot();

        final Activity activity = this.getActivity();

        comments = new ArrayList<Comment>();

        extras = getActivity().getIntent().getExtras();

        if (extras == null) {
            extras = getArguments();
        }



        final String url = Constants.redditUrl + extras.getString("permalink") + Constants.jsonExt;
        Log.i(LOG_TAG, url);
        bi.fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFavourite) {

                    ContentValues values = new ContentValues();
                    values.put(FavContract.favourite.COLUMN_TITLE, extras.getString("title"));
                    values.put(FavContract.favourite.COLUMN_AUTHOR, extras.getString("author"));
                    values.put(FavContract.favourite.COLUMN_PERMALINK, extras.getString("permalink"));
                    values.put(FavContract.favourite.COLUMN_POINTS, extras.getInt("score"));
                    values.put(FavContract.favourite.COLUMN_COMMENTS, extras.getInt("num_comments"));
                    values.put(FavContract.favourite.COLUMN_IMAGE_URL, extras.getString("image_url"));
                    values.put(FavContract.favourite.COLUMN_URL, extras.getString("url"));
                    values.put(FavContract.favourite.COLUMN_THUMBNAIL, extras.getString("thumbnail"));
                    values.put(FavContract.favourite.COLUMN_POSTED_ON, extras.getLong("postedOn"));
                    values.put(FavContract.favourite.COLUMN_POST_ID, id);
                    values.put(FavContract.favourite.COLUMN_SUBREDDIT, extras.getString("subreddit"));
                    values.put(FavContract.favourite.COLUMN_FAVORITES, 1);
                    getContext().getContentResolver().insert(FavContract.favourite.CONTENT_URI, values);

                      Toast.makeText(getContext(), getString(R.string.title_favourites), Toast.LENGTH_SHORT).show();
                    bi.fav.setSelected(true);
                    isFavourite = true;

                } else {
                    //Toast.makeText(getContext(),"Post removed...",Toast.LENGTH_SHORT).show();

                    bi.fav.setSelected(false);
                    isFavourite = false;
                    //Delete from db
                    getContext().getContentResolver().delete(FavContract.favourite.CONTENT_URI,
                            FavContract.favourite.COLUMN_POST_ID + "=?", new String[]{String.valueOf(id)});
                }

                //Notify widget to update
                Context context = getContext();
                Intent dataUpdatedIntent = new Intent(getString(R.string.data_update_key))
                        .setPackage(context.getPackageName());
                context.sendBroadcast(dataUpdatedIntent);
            }
        });
        bi.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, extras.getString("url"));
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share link"));
            }
        });

        processor = new CommentProcessor(url);
        id = extras.getString(getString(R.string.reddit_id));
        class AddComments extends AsyncTask<String, Void, String> {

            protected String doInBackground(String... arg0) {
                //Your implementation
                comments.addAll(processor.fetchComments());
                return "done";
            }

            protected void onPostExecute(String result) {
                commentAdapter = new CommentAdapter(activity, comments);
                bi.rvComments.setAdapter(commentAdapter);
                mLayoutManager = new LinearLayoutManager(getContext());
                bi.rvComments.setLayoutManager(mLayoutManager);
                bi.rvComments.setNestedScrollingEnabled(false); // Disables scrolling
            }
        }
        if (savedInstanceState == null) {
            AddComments addComments = new AddComments();
            addComments.execute(url);
        } else {
            // AddComments addComments=new AddComments();
            //addComments.execute(url);
            comments = savedInstanceState.getParcelableArrayList(getString(R.string.listitems));
            mListState = savedInstanceState.getParcelable(getString(R.string.liststate_key));
            commentAdapter = new CommentAdapter(activity, comments);
            bi.rvComments.setAdapter(commentAdapter);
            mLayoutManager = new LinearLayoutManager(getContext());
            bi.rvComments.getLayoutManager().onRestoreInstanceState(mListState);
            bi.rvComments.setNestedScrollingEnabled(false);
        }
        bi.openbrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(extras.getString("url")));
                startActivity(i);
            }
        });


        MobileAds.initialize(getContext(), "ca-app-pub-5476505127668399~8084576917");
        AdRequest adRequest = new AdRequest.Builder().
                addTestDevice(AdRequest.DEVICE_ID_EMULATOR).
                build();
        bi.adView.loadAd(adRequest);

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();


        bi.fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFavourite) {

                    ContentValues values = new ContentValues();
                    values.put(FavContract.favourite.COLUMN_TITLE, extras.getString("title"));
                    values.put(FavContract.favourite.COLUMN_AUTHOR, extras.getString("author"));
                    values.put(FavContract.favourite.COLUMN_PERMALINK, extras.getString("permalink"));
                    values.put(FavContract.favourite.COLUMN_POINTS, extras.getInt("score"));
                    values.put(FavContract.favourite.COLUMN_COMMENTS, extras.getInt("num_comments"));
                    values.put(FavContract.favourite.COLUMN_IMAGE_URL, extras.getString("image_url"));
                    values.put(FavContract.favourite.COLUMN_URL, extras.getString("url"));
                    values.put(FavContract.favourite.COLUMN_THUMBNAIL, extras.getString("thumbnail"));
                    values.put(FavContract.favourite.COLUMN_POSTED_ON, extras.getLong("postedOn"));
                    values.put(FavContract.favourite.COLUMN_POST_ID, id);
                    values.put(FavContract.favourite.COLUMN_SUBREDDIT, extras.getString("subreddit"));
                    values.put(FavContract.favourite.COLUMN_FAVORITES, 1);
                    getContext().getContentResolver().insert(FavContract.favourite.CONTENT_URI, values);

                    //  Toast.makeText(getContext(), "Post added to favourites", Toast.LENGTH_SHORT).show();
                    bi.fav.setSelected(true);
                    isFavourite = true;

                } else {
                    //Toast.makeText(getContext(),"Post removed...",Toast.LENGTH_SHORT).show();

                    bi.fav.setSelected(false);
                    isFavourite = false;
                    //Delete from db
                    getContext().getContentResolver().delete(FavContract.favourite.CONTENT_URI,
                            FavContract.favourite.COLUMN_POST_ID + "=?", new String[]{String.valueOf(id)});
                }

                //Notify widget to update
                Context context = getContext();
                Intent dataUpdatedIntent = new Intent(getString(R.string.data_update_key))
                        .setPackage(context.getPackageName());
                context.sendBroadcast(dataUpdatedIntent);
            }
        });

        Glide.with(this).load(extras.getString("image_url")).into(bi.headerImage);


        bi.commentsNum.setText(String.valueOf((extras.getInt("num_comments"))));
        bi.score.setText(String.valueOf((extras.getInt("score"))) );
        bi.headerTitle.setText(String.valueOf((extras.getString("title"))));

        initLoader();
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(getString(R.string.listitems), this.comments);
        outState.putParcelable(getString(R.string.liststate_key),this.mListState);
        super.onSaveInstanceState(outState);
    }

    private void initLoader() {
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getContext(),FavContract.favourite.CONTENT_URI,null,FavContract.favourite.COLUMN_POST_ID+"=?", new String[]{id},null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        int fav = 0;
        isFavourite = false;
        Log.i("loader","finished");
        if(data != null && data.getCount() > 0) {
            if (data.moveToFirst()) {
                fav = data.getInt(10);
                Log.d(LOG_TAG, "  " + data.getString(3));
            }
        }

        if (fav == 1) {
            isFavourite = true;
            bi.fav.setSelected(true);
        } else {
            isFavourite = false;
            bi.fav.setSelected(false);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}

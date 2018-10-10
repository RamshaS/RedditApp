package com.udacity.ramshasaeed.redditapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.udacity.ramshasaeed.redditapp.adapter.RedditListAdapter;
import com.udacity.ramshasaeed.redditapp.analytics.AnalyticsApplication;
import com.udacity.ramshasaeed.redditapp.database.FavContract;
import com.udacity.ramshasaeed.redditapp.databinding.ActivityMainBinding;
import com.udacity.ramshasaeed.redditapp.model.Reddit;
import com.udacity.ramshasaeed.redditapp.services.RetrofitClient;
import com.udacity.ramshasaeed.redditapp.util.Constants;
import com.udacity.ramshasaeed.redditapp.util.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    ActivityMainBinding bi;
    SharedPreferences prefs;
    private String subReddit = "";
    private int counter = 0;
    public ArrayList<Reddit> list;
    Parcelable mListState;
    private RedditListAdapter adapter;
    private boolean mTwoPane;
    private String sortBy = "";
    private SearchView mSearchView;
    private Menu sortMenu;
    private Tracker mTracker;
    private static final String CURRENT_LIST_VIEW_POSITION = "currentpos";
    int current_pos = 0;

    private static String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    public void onResume() {
        super.onResume();

        mTracker.setScreenName("Image~List Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this,R.layout.activity_main);

        setSupportActionBar( bi.appBarMain.toolbar);
        list = new ArrayList<>();
        prefs = this.getSharedPreferences(getString(R.string.package_name), Context.MODE_PRIVATE);
        RetrofitClient.getInstance();


        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();



        setNavigationView();


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, bi.drawerLayout,  bi.appBarMain.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        bi.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            if (NetworkUtils.isOnline(MainActivity.this)) {
                updateList(getResources().getString(R.string.HomePage));
                getSupportActionBar().setTitle(R.string.HomePage);
            } else {
                Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            }

        }
    }
    public void toggleMenu(boolean showMenu) {
        if (sortMenu == null)
            return;
        Log.d(LOG_TAG, "toggleMenu()");

        for (int i = 0; i < sortMenu.size(); i++) {
            sortMenu.getItem(i).setVisible(showMenu);
        }
    }
    public void updateList(String subreddit) {

        this.subReddit = subreddit;
        int casenum;

        counter = 0;
        bi.appBarMain.toolbar.setTitle(subreddit);
        String subRedditSortBy = "";
        if (!"".equals(sortBy)) {
            subRedditSortBy = "/" + sortBy;
        }
        if(mSearchView!=null) {
            mSearchView.setQuery("", false);
            mSearchView.setIconified(true);
        }
        if (subreddit.equals(getResources().getString(R.string.HomePage))) {
            subreddit = Constants.jsonEnd;
            casenum = 1;
            toggleSort();
        } else {
            toggleMenu(true);
            casenum = 2;
            subreddit =   subreddit + subRedditSortBy + Constants.jsonEnd;
        }

        updateListFromUrl(casenum,subreddit);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Toast.makeText(context,"Restore is called",Toast.LENGTH_LONG).show();
        list = savedInstanceState.getParcelableArrayList(getString(R.string.listitems));
        mListState = savedInstanceState.getParcelable(getString(R.string.liststate_key));
        current_pos = savedInstanceState.getInt(CURRENT_LIST_VIEW_POSITION);
        if (list != null) {

            setRedditAdapter(MainActivity.this, this.list);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(getString(R.string.listitems), this.list);
        outState.putParcelable(getString(R.string.liststate_key),this.mListState);
        outState.putInt(CURRENT_LIST_VIEW_POSITION, bi.appBarMain.contentMain.rvRedditList.getScrollState());

    }
    private void setRedditAdapter(Context context, final ArrayList<Reddit> list) {
        adapter = new RedditListAdapter(context,list);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        bi.appBarMain.contentMain.rvRedditList.setLayoutManager(llm);
        bi.appBarMain.contentMain.rvRedditList.setAdapter(adapter);
        bi.appBarMain.contentMain.rvRedditList.scrollToPosition(current_pos);
        adapter.SetOnItemClickListener(adapterClick);
    }
    public void updateListFromUrl(int url_call_case, String searchKeyword) {

        adapter = new RedditListAdapter(MainActivity.this, list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        bi.appBarMain.contentMain.rvRedditList.setLayoutManager(mLayoutManager);
        bi.appBarMain.contentMain.rvRedditList.setAdapter(adapter);
        bi.appBarMain.contentMain.rvRedditList.scrollToPosition(current_pos);
        adapter.SetOnItemClickListener(adapterClick);
        adapter.notifyDataSetChanged();
        adapter.clearAdapter();

        Call<ResponseBody> call;

        switch (url_call_case){
            case 1:
                call = RetrofitClient.api.getAll(searchKeyword);
                break;
            case 2:
                call = RetrofitClient.api.getHome(Constants.subredditUrl+searchKeyword);

                break;
            case 3:
                call = RetrofitClient.api.getSearchqueryResult(searchKeyword);
                break;
                default:
                    call = RetrofitClient.api.getHome(Constants.subredditUrl+"home.json");
break;
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(MainActivity.this, getString(R.string.completion_status), Toast.LENGTH_SHORT).show();

                if (response.isSuccessful()) {
                    try {
                        //JSONObject data = response.getJSONObject("data");
                        String resp = response.body().string();
                        JSONObject data = new JSONObject(resp).getJSONObject("data");

                     //   after_id = data.getString("after");
                        JSONArray children = data.getJSONArray("children");

                        for (int i = 0; i < children.length(); i++) {

                            JSONObject post = children.getJSONObject(i).getJSONObject("data");
                            Reddit item = new Reddit();
                            item.setTitle(post.getString("title"));
                            item.setThumbnail(post.getString("thumbnail"));
                            item.setUrl(post.getString("url"));
                            item.setSubreddit(post.getString("subreddit"));
                            item.setAuthor(post.getString("author"));
                            item.setNumComments(post.getInt("num_comments"));
                            item.setScore(post.getInt("score"));
                            item.setOver18(post.getBoolean("over_18"));
                            item.setPermalink(post.getString("permalink"));
                            item.setPostedOn(post.getLong("created_utc"));
                            item.setId(post.getString("id"));
                            try {
                                Log.i(LOG_TAG, post.getJSONObject("preview").getJSONArray("images").getJSONObject(0).getJSONObject("source").getString("url"));
                                item.setImageUrl(post.getJSONObject("preview").getJSONArray("images").getJSONObject(0).getJSONObject("source").getString("url"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (list == null) {
                                list = new ArrayList<>();
                            }
                            list.add(item);
                        }
                       adapter.notifyDataSetChanged();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, getString(R.string.data_fetch), Toast.LENGTH_SHORT).show();
            }
        });
        if(mTwoPane){
            if(list!=null && list.size()!=0){
                startFragment(list.get(0));
            }
        }

    }
    private void initLoader() {
        Log.d(LOG_TAG, "initLoader()");
        getLoaderManager().initLoader(0, null, this);
    }
    private void setNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        bi.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                if (menuItem.getGroupId() == R.id.groupFav) {
                    initLoader();
                    bi.appBarMain.toolbar.setTitle(getString(R.string.title_favourites));
                    bi.drawerLayout.closeDrawers();
                    toggleMenu(false);
                    return true;
                } else updateList(menuItem.toString());
                //Closing drawer on item click
                bi.drawerLayout.closeDrawers();

                return true;
            }
        });
    }


    @Override
    public void onBackPressed() {

        if (bi.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            bi.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        this.sortMenu = menu;
        MenuItem search = menu.findItem(R.id.menuSearch);
        mSearchView= (SearchView) search.getActionView();
        setupSearchView();
        toggleSort();
        return true;
    }

    public void toggleSort() {
        if (sortMenu == null)
            return;
        Log.d(LOG_TAG, "toggleSort()");
        toggleSelectiveMenu(true, getString(R.string.sort));

    }

    public void toggleSelectiveMenu(boolean showMenu, String title) {
        if (sortMenu == null)
            return;
        Log.d(LOG_TAG, "toggleMenu()");

        for (int i = 0; i < sortMenu.size(); i++) {
            if (title.equals(sortMenu.getItem(i).getTitle())) {
                sortMenu.getItem(i).setVisible(showMenu);
                break;
            }
        }
    }
    private void setupSearchView() {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                // TODO Auto-generated method stub
                mSearchView.clearFocus();
                updateList(subReddit, query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                return true;
            }
        });
    }
    public void updateList(String subreddit, String searchQuery) {
        this.subReddit = subreddit;
        int casenum;
        bi.appBarMain.toolbar.setTitle(subreddit);
        String searchQuerySetup = Constants.searchJson + "?q=" + searchQuery;
        if (subreddit.equals(getResources().getString(R.string.HomePage))) {
            subreddit = searchQuery;
            casenum = 3;
            toggleSort();
        } else {
            toggleMenu(true);
            subreddit = subreddit + "/"+searchQuerySetup;
            casenum = 2;
        }
        updateListFromUrl(casenum,subreddit);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       /* if (subReddit.equals(getResources().getString(R.string.HomePage))
                || subReddit.equals(getResources().getString(R.string.title_favourites))) {
            Toast.makeText(this, "Sorting cannot be applied to Home and Favourites.", Toast.LENGTH_LONG).show();
            return true;
        }*/
        switch (item.getItemId()) {
            case R.id.menuSortHot:
                sortBy = "hot";
                break;

            case R.id.menuSortNew:
                sortBy = "new";
                break;
            case R.id.menuSortControversial:
                sortBy = "controversial";
                break;
            case R.id.menuSortTop:
                sortBy = "top";
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        updateList(this.subReddit);
        return true;
    }

    public void startFragment(Reddit item ){
        Log.d(LOG_TAG,"Starting the fragment.");
        getBundleForRedditItem(item);
    }
    public Bundle getBundleForRedditItem(Reddit item){
        Bundle arguments = new Bundle();
        arguments.putString("title", item.getTitle());
        arguments.putString("subreddit", item.getSubreddit());
        arguments.putString("image_url", item.getImageUrl());
        arguments.putString("url", item.getUrl());
        arguments.putInt("score", item.getScore());
        arguments.putString("thumbnail", item.getThumbnail());
        arguments.putLong("postedOn", item.getPostedOn());
        arguments.putInt("num_comments", item.getNumComments());
        arguments.putString("permalink", item.getPermalink());
        arguments.putString("id", item.getId());
        arguments.putString("author", item.getAuthor());

        return arguments;
    }
    public RedditListAdapter.OnItemClickListener adapterClick = new RedditListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {

            Reddit item = adapter.getListItems().get(position);
            item.getTitle();

            if (mTwoPane) {
                startFragment(item);

            } else {
                Intent openDetailActivity = new Intent(getBaseContext(), DetailActivity.class);

                openDetailActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle arguments = new Bundle();
                arguments.putString("title", item.getTitle());
                arguments.putString("subreddit", item.getSubreddit());
                arguments.putString("image_url", item.getImageUrl());
                arguments.putString("url", item.getUrl());
                arguments.putInt("score", item.getScore());
                arguments.putString("thumbnail", item.getThumbnail());
                arguments.putLong("postedOn", item.getPostedOn());
                arguments.putInt("num_comments", item.getNumComments());
                arguments.putString("permalink", item.getPermalink());
                arguments.putString("id", item.getId());
                arguments.putString("author", item.getAuthor());
                openDetailActivity.putExtras(arguments);
                getBaseContext().startActivity(openDetailActivity);
            }

        }


    };

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, FavContract.favourite.CONTENT_URI, null,
                FavContract.favourite.COLUMN_FAVORITES + "=?", new String[]{Integer.toString(1)}, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        getDataFromCursor(cursor);
        adapter.SetOnItemClickListener(adapterClick);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    private void getDataFromCursor(Cursor cursor) {

        list.clear();
        if (cursor != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Reddit item = new Reddit();
                item.setId(cursor.getString(1));
                item.setTitle(cursor.getString(2));
                item.setAuthor(cursor.getString(3));
                item.setThumbnail(cursor.getString(4));
                item.setPermalink(cursor.getString(5));
                item.setUrl(cursor.getString(6));
                item.setImageUrl(cursor.getString(7));
                item.setNumComments(cursor.getInt(8));
                item.setScore(cursor.getInt(9));
                item.setPostedOn(cursor.getLong(11));
                item.setOver18(false);
                item.setSubreddit(cursor.getString(12));

                list.add(item);


            }

            updateViewWithResults(list);
        }

    }

    public void updateViewWithResults(List<Reddit> result) {
        adapter = new RedditListAdapter(this, result);
        bi.appBarMain.contentMain.rvRedditList.setAdapter(adapter);
        bi.appBarMain.contentMain.rvRedditList.scrollToPosition(current_pos);

    }
}

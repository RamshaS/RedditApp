package com.udacity.ramshasaeed.redditapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DatabaseUtils;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.udacity.ramshasaeed.redditapp.adapter.reddit_list_adapter;
import com.udacity.ramshasaeed.redditapp.databinding.ActivityMainBinding;
import com.udacity.ramshasaeed.redditapp.databinding.ContentMainBinding;
import com.udacity.ramshasaeed.redditapp.model.Reddit;
import com.udacity.ramshasaeed.redditapp.services.RetrofitClient;
import com.udacity.ramshasaeed.redditapp.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ActivityMainBinding bi;
    SharedPreferences prefs;
    private String subReddit = "";
    private int counter = 0;
    private ArrayList<Reddit> list = new ArrayList<Reddit>();
    Parcelable mListState;
    private reddit_list_adapter adapter;
    private boolean mTwoPane;
    private String sortBy = "";
    private SearchView mSearchView;
    private Menu sortMenu;
    private static String LOG_TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this,R.layout.activity_main);

        setSupportActionBar( bi.appBarMain.toolbar);
        prefs = this.getSharedPreferences(getString(R.string.package_name), Context.MODE_PRIVATE);
        RetrofitClient.getInstance();

        setSubrreddits();
        bi.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, bi.drawerLayout,  bi.appBarMain.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        bi.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            updateList(getResources().getString(R.string.HomePage));
            getSupportActionBar().setTitle(R.string.HomePage);
        } else {
            list = savedInstanceState.getParcelableArrayList(getString(R.string.listitems));
            mListState = savedInstanceState.getParcelable(getString(R.string.liststate_key));
            adapter = new reddit_list_adapter(this,list);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            bi.appBarMain.contentMain.rvRedditList.setLayoutManager(llm);
            bi.appBarMain.contentMain.rvRedditList.setAdapter(adapter);
            adapter.SetOnItemClickListener(adapterClick);
        }

        bi.navView.setNavigationItemSelectedListener(this);
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
            toggleSort(false);
        } else {
            toggleMenu(true);
            casenum = 2;
            subreddit =   subreddit + subRedditSortBy + Constants.jsonEnd;
        }

        updateListFromUrl(casenum,subreddit);

    }

    public void updateListFromUrl(int url_call_case, String searchKeyword) {
       // Log.d(LOG_TAG, url);

        adapter = new reddit_list_adapter(this, list);
        bi.appBarMain.contentMain.rvRedditList.setAdapter(adapter);
        adapter.SetOnItemClickListener(adapterClick);
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
        adapter.clearAdapter();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(MainActivity.this, "Completed!", Toast.LENGTH_SHORT).show();

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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Update list by notifying the adapter of changes
                    adapter.notifyDataSetChanged();


                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Sorry Couldn't fetch data", Toast.LENGTH_SHORT).show();
            }
        });
        if(mTwoPane){
            if(list!=null && list.size()!=0){
                startFragment(list.get(0));
            }
        }

    }
    private void setSubrreddits() {
        MenuItem saveThis = bi.navView.getMenu().getItem(0);
        bi.navView.getMenu().removeGroup(500);
        if (prefs.getBoolean(getString(R.string.first_run), true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putString(getString(R.string.subreddit_pref_key), getString(R.string.initial_subs)).commit();
            prefs.edit().putBoolean(getString(R.string.first_run), false).commit();
        }


        String subString = prefs.getString(getString(R.string.subreddit_pref_key), "");
        List<String> mItems = Arrays.asList(subString.split(","));
        for (int i = 0; i < mItems.size(); i++) {
            MenuItem item = bi.navView.getMenu().add(500, Menu.NONE, Menu.NONE, mItems.get(i));
        }
        bi.navView.setItemIconTintList(null);
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
        setupSearchView(mSearchView);
        toggleSort(false);
        return true;
    }

    public void toggleSort(boolean showMenu) {
        if (sortMenu == null)
            return;
        Log.d(LOG_TAG, "toggleSort()");
        toggleSelectiveMenu(showMenu, getString(R.string.sort));
//        MenuItem item = (MenuItem) findViewById(R.id.menuSort);
//        for()
//        if(item!=null){
//            item.setVisible(showMenu);
//        }

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
    private void setupSearchView(final SearchView searchItem) {
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
            toggleSort(false);
        } else {
            toggleMenu(true);
            subreddit = subreddit + "/"+searchQuerySetup;
            casenum = 2;
        }
        updateListFromUrl(casenum,subreddit);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        /*// Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        bi.drawerLayout.closeDrawer(GravityCompat.START);
        return true;*/
        if (subReddit.equals(getResources().getString(R.string.HomePage))
                || subReddit.equals(getResources().getString(R.string.title_favourites))) {
            Toast.makeText(this, "Sorting cannot be applied to Home and Favourites.", Toast.LENGTH_LONG).show();
            return true;
        }
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
        Bundle arguments = getBundleForRedditItem(item);
/*
        DetailFragment fragment = new DetailFragment();

        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.reddititem_detail_container, fragment)
                .commit();
  */
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
    public reddit_list_adapter.OnItemClickListener adapterClick = new reddit_list_adapter.OnItemClickListener() {
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
}

package com.udacity.ramshasaeed.redditapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.DatabaseUtils;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.udacity.ramshasaeed.redditapp.databinding.ActivityMainBinding;
import com.udacity.ramshasaeed.redditapp.databinding.ContentMainBinding;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ActivityMainBinding bi;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this,R.layout.activity_main);

        setSupportActionBar( bi.appBarMain.toolbar);
        prefs = this.getSharedPreferences(getString(R.string.package_name), Context.MODE_PRIVATE);

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

        bi.navView.setNavigationItemSelectedListener(this);
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
        // Handle navigation view item clicks here.
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
        return true;
    }
}

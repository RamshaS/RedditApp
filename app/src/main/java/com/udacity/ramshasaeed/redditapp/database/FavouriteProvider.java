package com.udacity.ramshasaeed.redditapp.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;


public class FavouriteProvider extends ContentProvider {
    private static String LOG_TAG = FavouriteProvider.class.getSimpleName();
    static final String PROVIDER_NAME = "com.udacity.ramshasaeed.redditapp";
    static final String URL = "content://" + PROVIDER_NAME + "/favourite";
    private static final int TABLE = 1;
    private static final int TABLE_ID = 2;
    private static final UriMatcher mUriMatcher;
    private SQLiteOpenHelper mOpenHelper;
    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(FavContract.AUTHORITY, FavContract.favourite.TABLE_NAME, TABLE );
        mUriMatcher.addURI(FavContract.AUTHORITY, FavContract.favourite.TABLE_NAME + "/#", TABLE_ID);

    }
    public FavouriteProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int delete;
        switch (mUriMatcher.match(uri)){
            case TABLE_ID:{
                delete = db.delete(FavContract.favourite.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case TABLE:{
                delete = db.delete(FavContract.favourite.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new IllegalArgumentException("Unsupported Uri For Deletion " + uri);
        }
        if(getContext()!=null&&delete!=0)
            getContext().getContentResolver().notifyChange(uri, null);
        return delete;
    }

    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)){
            case TABLE:
                return FavContract.favourite.CONTENT_TYPE;
            case TABLE_ID:
                return FavContract.favourite.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported Uri" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri uri1=null;
        Log.d(LOG_TAG,"insert() "+ FavContract.favourite.TABLE_NAME+"  "+values.toString());
        switch (mUriMatcher.match(uri)){
            case TABLE:{
                long id = db.insert(FavContract.favourite.TABLE_NAME, null, values);
                if(id!=-1){
                    uri1 = FavContract.favourite.buildUri(id);
                }
                break;
            }
            default:{
                throw new IllegalArgumentException("Unsupported Uri For Insertion " + uri);
            }
        }
        if(getContext()!=null)
            getContext().getContentResolver().notifyChange(uri, null);
        return uri1;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new FavouriteDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cursor;
        switch (mUriMatcher.match(uri)){
            case TABLE:{
                cursor = db.query(FavContract.favourite.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            }
            default:
                throw new IllegalArgumentException("Unsupported Uri" + uri);
        }
        if(getContext()!=null)
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.d(LOG_TAG,"update() "+ FavContract.favourite.TABLE_NAME+"  "+values.toString());
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int update;
        switch (mUriMatcher.match(uri)){
            case TABLE_ID:{
                update = db.update(FavContract.favourite.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new IllegalArgumentException("Unsupported Uri For Updating " + uri);
        }
        if(getContext()!=null)
            getContext().getContentResolver().notifyChange(uri, null);
        return update;
    }
    }

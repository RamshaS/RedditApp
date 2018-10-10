package com.udacity.ramshasaeed.redditapp.util;

import android.text.format.DateFormat;
import android.util.Log;

import com.udacity.ramshasaeed.redditapp.model.Comment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class CommentProcessor {
    private static String LOG_TAG = CommentProcessor.class.getSimpleName();
   private String url;

    public CommentProcessor(String u){
        url=u;
    }

    private Comment loadComment(JSONObject data, int level){
        Comment comment=new Comment();
        try{
            comment.setHtmlText(data.getString("body"));
            comment.setAuthor(data.getString("author"));
            comment.setPoints((data.getInt("ups") - data.getInt("downs"))+ "");
            comment.setPostedOn(getDate(data.getLong("created_utc")));
            comment.setLevel(level);
        }catch(Exception e){
            Log.e(LOG_TAG,"Unable to parse comment. More: "+e);
        }
        return comment;
    }
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time*1000);
        return DateFormat.format("HH:mm  dd/MM/yy", cal).toString();


    }
    private void process(ArrayList<Comment> comments
            , JSONArray c, int level)
            throws Exception {
        for(int i=0;i<c.length();i++){
            if(c.getJSONObject(i).optString("kind")==null)
                continue;
            if(c.getJSONObject(i).optString("kind").equals("t1")==false)
                continue;
            JSONObject data=c.getJSONObject(i).getJSONObject("data");
            Comment comment=loadComment(data,level);
            if(comment.getAuthor()!=null) {
                comments.add(comment);
                addReplies(comments,data,level+1);
            }
        }
    }


    private void addReplies(ArrayList<Comment> comments,
                            JSONObject parent, int level){
        try{
            if(parent.get("replies").equals("")){
                // This means the comment has no replies
                return;
            }
            JSONArray r=parent.getJSONObject("replies")
                    .getJSONObject("data")
                    .getJSONArray("children");
            process(comments, r, level);
        }catch(Exception e){
            Log.e(LOG_TAG,"An error occured while adding reply to comment. More: "+e);
        }
    }


    public ArrayList<Comment> fetchComments(){
        ArrayList<Comment> comments=new ArrayList<Comment>();
        try{
            String raw= Connector.readContents(url);

            JSONArray r=new JSONArray(raw)
                    .getJSONObject(1)
                    .getJSONObject("data")
                    .getJSONArray("children");
            process(comments, r, 0);
        }catch(Exception e){
            Log.e(LOG_TAG,"Could not fetch comments. More: "+e);
        }
        return comments;
    }

}
package com.udacity.ramshasaeed.redditapp.util;

import android.os.StrictMode;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Connector {
    public static HttpURLConnection getConnection(String url){
        try{
            HttpURLConnection hcon = (HttpURLConnection)new URL(url).openConnection();
            hcon.setReadTimeout(30000);
            hcon.setRequestProperty("User-Agent", "Firefox 50");
            return hcon;
        }catch(Exception e){
            Log.d("CONNECTION FAILED", e.toString());
            return null;
        }
    }

    public static String readContents(String url){
        return readContents(url,true);
    }

    public static String readContents(String url, boolean useCache){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        String rurl=randomizeURL(url);
        HttpURLConnection hcon=getConnection(rurl);
        if(hcon==null) return null;
        try{
            StringBuffer sb=new StringBuffer(8192);
            String tmp="";
            BufferedReader br=new BufferedReader(new InputStreamReader(hcon.getInputStream()));
            while((tmp=br.readLine())!=null)
                sb.append(tmp).append("\n");
            br.close();

            return sb.toString();
        }catch(Exception e){
            Log.d("READ FAILED", e.toString());
            return null;
        }
    }

    private static String randomizeURL(String u){
        if(u.indexOf("?")!=-1){
            u+="&random_alien_n="+ Math.random();
        }else{
            u+="?random_alien_n="+ Math.random();
        }
        return u;
    }


}
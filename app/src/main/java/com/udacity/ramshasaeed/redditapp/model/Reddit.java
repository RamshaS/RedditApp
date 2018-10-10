package com.udacity.ramshasaeed.redditapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Reddit implements Parcelable
{

    private String id;
    private long postedOn;
    private String title;
    private String thumbnail;
    private String url;
    private String subreddit;
    private String author;
    private String permalink;
    private String imageUrl;
    private int score,numComments;
    private Boolean over18;

       public Reddit() {
    }
    protected Reddit(Parcel in) {
        this.title = in.readString();
        this.thumbnail = in.readString();
        this.url = in.readString();
        this.subreddit = in.readString();
        this.author = in.readString();
        this.permalink = in.readString();
        this.id = in.readString();
        this.postedOn = in.readLong();
        this.imageUrl = in.readString();
        this.score = in.readInt();
        this.numComments = in.readInt();
        this.over18 = (Boolean) in.readValue(Boolean.class.getClassLoader());
       }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.title);
        parcel.writeString(this.thumbnail);
        parcel.writeString(this.url);
        parcel.writeString(this.subreddit);
        parcel.writeString(this.author);
        parcel.writeString(this.permalink);
        parcel.writeString(this.id);
        parcel.writeLong(this.postedOn);
        parcel.writeString(this.imageUrl);
        parcel.writeInt(this.score);
        parcel.writeInt(this.numComments);
        parcel.writeValue(this.over18);

    }
    public static final Parcelable.Creator<Reddit> CREATOR = new Parcelable.Creator<Reddit>() {
        @Override
        public Reddit createFromParcel(Parcel source) {
            return new Reddit(source);
        }

        @Override
        public Reddit[] newArray(int size) {
            return new Reddit[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(long postedOn) {
        this.postedOn = postedOn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getNumComments() {
        return numComments;
    }

    public void setNumComments(int numComments) {
        this.numComments = numComments;
    }


    public void setOver18(Boolean over18) {
        this.over18 = over18;
    }
}

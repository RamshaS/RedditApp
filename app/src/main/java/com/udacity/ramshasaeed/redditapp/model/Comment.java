package com.udacity.ramshasaeed.redditapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable {
    String htmlText, author, points, postedOn;
    int level;

    public Comment() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.htmlText);
        parcel.writeString(this.author);
        parcel.writeString(this.points);
        parcel.writeInt(this.level);
    }

    protected Comment(Parcel in){
        this.htmlText = in.readString();
        this.author = in.readString();
        this.points = in.readString();
        this.postedOn = in.readString();
        this.level = in.readInt();
    }

    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };


    public String getHtmlText() {
        return htmlText;
    }

    public void setHtmlText(String htmlText) {
        this.htmlText = htmlText;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(String postedOn) {
        this.postedOn = postedOn;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}

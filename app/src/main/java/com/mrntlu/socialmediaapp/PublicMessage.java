package com.mrntlu.socialmediaapp;

import android.net.Uri;

import java.util.Date;

public class PublicMessage {

    private String imageUrl;
    private String message;
    private String author;
    private Date date;

    public PublicMessage(String message, String author,Date date, String imageUrl) {
        this.message = message;
        this.author = author;
        this.date=date;
        this.imageUrl = imageUrl;
    }

    public PublicMessage() {
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public Date getDate() {
        return date;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

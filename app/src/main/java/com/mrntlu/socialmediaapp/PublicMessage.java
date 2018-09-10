package com.mrntlu.socialmediaapp;

public class PublicMessage {

    private String imageUrl;
    private String message;
    private String author;

    public PublicMessage(String message, String author, String imageUrl) {
        this.message = message;
        this.author = author;
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

    public String getImageUrl() {
        return imageUrl;
    }
}

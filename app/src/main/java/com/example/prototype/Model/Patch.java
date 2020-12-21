package com.example.prototype.Model;

public class Patch {
    public String id;
    public String city;
    public String title;
    public String description;
    public String imageURL;
    public String sender;

    public Patch(String id, String city, String title, String description, String imageURL, String sender) {
        this.id = id;
        this.city = city;
        this.title = title;
        this.description = description;
        this.imageURL = imageURL;
        this.sender = sender;
    }

    public Patch(){
    }

    public String getId() {
        return id;
    }

    public Patch(String id) {
        this.id = id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}

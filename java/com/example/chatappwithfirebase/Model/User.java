package com.example.chatappwithfirebase.Model;

public class User {
    private String Id;
    private String username;
    private String ImageUrl;
    private String status;
    private String search;
    public User(){

    }
    public User(String Id, String username, String imageUrl,String status,String search) {
        this.Id = Id;
        this.username = username;
        this.ImageUrl = imageUrl;
        this.status = status;
        this.search = search;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}

package com.example.newsapp;

public class Article {
    private String title, image, url, section, id, date;

    public Article() {
    }

    public Article(String id, String title, String image, String section,  String date, String url) {
        this.title = title;
        this.image = image;
        this.url = url;
        this.section = section;
        this.id = id;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getUrl() {
        return url;
    }

    public String getSection() {
        return section;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

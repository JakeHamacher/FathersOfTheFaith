package com.example.model;

public class Document {
    private int id;
    private String title;
    private String content;
    private String author;
    private String century;

    // Constructor
    public Document(int id, String title, String content, String author, String century) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.century = century;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCentury() {
        return century;
    }

    public void setCentury(String century) {
        this.century = century;
    }

    // Add search query for display purposes
    private String searchQuery;
    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }
}

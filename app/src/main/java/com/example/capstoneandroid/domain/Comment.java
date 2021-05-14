package com.example.capstoneandroid.domain;

public class Comment{
    public Long id;
    public String createdTime;
    public String content;
    public int likes;

    public Comment(Long id, String createdTime, String content, int likes) {
        this.id = id;
        this.createdTime = createdTime;
        this.content = content;
        this.likes = likes;
    }
}

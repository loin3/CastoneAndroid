package com.example.capstoneandroid.domain;

import java.io.Serializable;
import java.util.Date;

public class Post implements Serializable {
    private static final long serialVersionUID = 1L;

    public Long id;
    public String title;
    public String content;
    public int likes;
    public Long imageId;
    public String createdTime;

    public Post(Long id, String title, String content, int likes, Long imageId, String createdTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.likes = likes;
        this.imageId = imageId;
        this.createdTime = createdTime;
    }
}

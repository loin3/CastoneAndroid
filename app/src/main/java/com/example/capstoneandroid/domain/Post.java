package com.example.capstoneandroid.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post implements Serializable {
    private static final long serialVersionUID = 1L;

    public Long id = Long.valueOf(-1);
    public String title;
    public String content;
    public int likes = 0;
    public Long imageId;
    public String createdTime;
    public List<Comment> comments = new ArrayList<>();

    public Post() {
    }

    public Post(Long id, String title, String content, int likes, Long imageId, String createdTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.likes = likes;
        this.imageId = imageId;
        this.createdTime = createdTime;
    }


}

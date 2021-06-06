package com.example.capstoneandroid.domain;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Comment implements Serializable {
    private static final long serialVersionUID = 2L;
    public Long id;
    public String createdTime;
    public String content;
    public int likes;

    public Comment() {
    }

    public Comment(Long id, String createdTime, String content, int likes) {
        this.id = id;
        this.createdTime = createdTime;
        this.content = content;
        this.likes = likes;
    }

    public JSONObject ofJsonObject(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}

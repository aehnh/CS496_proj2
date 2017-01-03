package com.example.anders.cs496_proj2;

import android.graphics.Bitmap;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Anders on 1/3/2017.
 */

public class Post {
    int id;
    String title;
    String question;
    JSONArray comments;

    public Post(int id, String title, String question) {
        this.id = id;
        this.title = title;
        this.question = question;
        comments = null;
    }

    public Post(int id, String title, String question, JSONArray comments) {
        this.id = id;
        this.title = title;
        this.question = question;
        this.comments = comments;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getQuestion() { return question; }

    public void setQuestion(String question) { this.question = question; }

    public JSONArray getComments() { return comments; }

    public void setComments(JSONArray comments) { this.comments = comments; }
}
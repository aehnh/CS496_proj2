package com.example.anders.cs496_proj2;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Anders on 1/3/2017.
 */

public class Post implements Serializable {
    String title;
    String question;
    ArrayList<Comment> comments;

    public Post(String title, String question) {
        this.title = title;
        this.question = question;
        comments = null;
    }

    public Post(String title, String question, ArrayList<Comment> comments) {
        this.title = title;
        this.question = question;
        this.comments = comments;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getQuestion() { return question; }

    public void setQuestion(String question) { this.question = question; }

    public ArrayList<Comment> getComments() { return comments; }

    public void setComments(ArrayList<Comment> comemnts) { this.comments = comments; }
}
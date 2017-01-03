package com.example.anders.cs496_proj2;

import java.util.ArrayList;

/**
 * Created by Anders on 1/3/2017.
 */

public class Post {
    String question;
    String description;
    ArrayList<String> comments;

    public Post(String question, String description, ArrayList<String> comments) {
        this.question = question;
        this.description = description;
        this.comments = comments;
    }

    public String getQuestion() {
        return question;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void addComment(String comment) {
        comments.add(comment);
    }
}

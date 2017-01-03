package com.example.anders.cs496_proj2;

/**
 * Created by q on 2017-01-03.
 */

public class Comment {
    String comment_title;
    String comment_content;

    public Comment(String comment_title, String comment_content) {
        this.comment_title = comment_title;
        this.comment_content = comment_content;
    }

    public String getCommentTitle() { return comment_title; }

    public void setCommentTitle(String comment_title) { this.comment_title = comment_title; }

    public String getCommentContent() { return comment_content; }

    public void setCommentContent(String comment_content) { this.comment_content = comment_content; }
}

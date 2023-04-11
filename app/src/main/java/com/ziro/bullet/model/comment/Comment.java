package com.ziro.bullet.model.comment;

import java.util.ArrayList;
import com.google.gson.annotations.SerializedName;

public class Comment{

    @SerializedName("more_comment")
    private int moreComment;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("comment")
    private String comment;

    @SerializedName("id")
    private String id;

    @SerializedName("user")
    private User user;

    @SerializedName("replies")
    private ArrayList<Comment> comments = new ArrayList<Comment>();

    @SerializedName("type")
    private String type;

    private String page;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMoreComment(int moreComment){
        this.moreComment = moreComment;
    }

    public int getMoreComment(){
        return moreComment;
    }

    public void setCreatedAt(String createdAt){
        this.createdAt = createdAt;
    }

    public String getCreatedAt(){
        return createdAt;
    }

    public void setComment(String comment){
        this.comment = comment;
    }

    public String getComment(){
        return comment;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }

    public void setUser(User user){
        this.user = user;
    }

    public User getUser(){
        return user;
    }

    public ArrayList<Comment> getChild() {
        return comments;
    }

    public void setChild(ArrayList<Comment> comments) {
        this.comments = comments;
    }
}
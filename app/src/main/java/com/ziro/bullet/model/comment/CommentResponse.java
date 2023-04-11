
package com.ziro.bullet.model.comment;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.BaseModel;

import java.util.ArrayList;


@SuppressWarnings("unused")
public class CommentResponse extends BaseModel {

    @SerializedName("parent")
    private Comment parent;

    @SerializedName("comments")
    private ArrayList<Comment> mComment;

    public ArrayList<Comment> getmComment() {
        return mComment;
    }

    public void setmComment(ArrayList<Comment> mComment) {
        this.mComment = mComment;
    }

    public Comment getParent() {
        return parent;
    }

    public void setParent(Comment parent) {
        this.parent = parent;
    }
}

package com.ziro.bullet.interfaces;

import com.ziro.bullet.model.comment.Comment;
import com.ziro.bullet.model.comment.CommentResponse;

public interface CommentInterface {
    void loaderShow(boolean flag);

    void error(String error);

    void error404(String error);

    void success(CommentResponse commentResponse, boolean refresh);

    void successPagination(CommentResponse commentResponse);

    void success(Comment comment);
}

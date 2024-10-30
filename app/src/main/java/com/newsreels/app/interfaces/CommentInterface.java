package com.newsreels.app.interfaces;

import com.newsreels.app.model.comment.Comment;
import com.newsreels.app.model.comment.CommentResponse;

public interface CommentInterface {
    void loaderShow(boolean flag);

    void error(String error);

    void error404(String error);

    void success(CommentResponse commentResponse, boolean refresh);

    void successPagination(CommentResponse commentResponse);

    void success(Comment comment);
}

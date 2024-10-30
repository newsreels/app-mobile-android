package com.newsreels.app.interfaces;

public interface OnScheduleCallback {
    void onPost(int position);

    void onEdit(int position);

    void onDelete(int position);
}

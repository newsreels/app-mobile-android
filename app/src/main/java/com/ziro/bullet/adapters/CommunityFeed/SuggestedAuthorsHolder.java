package com.ziro.bullet.adapters.CommunityFeed;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.model.articles.Author;

import java.util.ArrayList;

public class SuggestedAuthorsHolder extends RecyclerView.ViewHolder {

    private Context context;
    private LinearLayoutManager layoutManager;
    private TextView title;
    private RecyclerView listView;

    public SuggestedAuthorsHolder(View view, Context context) {
        super(view);
        this.context = context;
        title = view.findViewById(R.id.title);
        listView = view.findViewById(R.id.items);
    }

    public void bind(String mTitle, ArrayList<Author> data) {
        if (!TextUtils.isEmpty(mTitle)) {
            title.setText(mTitle);
        }
        AuthorItemsAdapter adapter = new AuthorItemsAdapter(context, data);
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        listView.setLayoutManager(layoutManager);
        listView.setAdapter(adapter);
    }
}

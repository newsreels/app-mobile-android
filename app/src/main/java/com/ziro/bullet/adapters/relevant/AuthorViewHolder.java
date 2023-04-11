package com.ziro.bullet.adapters.relevant;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;

import com.ziro.bullet.interfaces.ViewItemClickListener;
import com.ziro.bullet.model.articles.Author;

import java.util.ArrayList;

public class AuthorViewHolder extends RecyclerView.ViewHolder {

    private final RecyclerView listView;
//    private final TextView seeAll;
    public LinearLayoutManager layoutManager;

    public AuthorViewHolder(@NonNull View itemView) {
        super(itemView);
        listView = itemView.findViewById(R.id.list_view);
//        seeAll = itemView.findViewById(R.id.see_all);

    }

    public void bind(Context context, ArrayList<Author> authorSearchArrayList,
                     ViewItemClickListener viewItemClickListenerAuthor,
                     RecyclerView.OnScrollListener onScrollListener,
                     int lastSeenFirstPosition, float offset, ViewItemClickListener viewItemClickListener) {

        AuthorItemsAdapter adapter = new AuthorItemsAdapter(authorSearchArrayList, context, viewItemClickListenerAuthor);
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        layoutManager.scrollToPositionWithOffset(lastSeenFirstPosition, (int) offset);

        listView.setLayoutManager(layoutManager);
        listView.setAdapter(adapter);

        listView.addOnScrollListener(onScrollListener);
//        seeAll.setVisibility(authorSearchArrayList.size() > 1 ? View.VISIBLE : View.GONE);
//        seeAll.setOnClickListener(v -> viewItemClickListener.itemClickedData(v, ViewItemClickListener.TYPE_RELEVANT_AUTHOR_SEE_ALL, null));
    }
}

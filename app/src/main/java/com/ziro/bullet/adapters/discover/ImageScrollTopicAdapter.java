package com.ziro.bullet.adapters.discover;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.model.NewDiscoverPage.Icon;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ImageScrollTopicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final Callback callback;
    private final ArrayList<Icon> iconArrayList;

    public ImageScrollTopicAdapter(ArrayList<Icon> iconArrayList, Callback callback) {
        this.iconArrayList = iconArrayList;
        this.callback = callback;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_scroll_topic_adapter_item, parent, false);
        return new ImageViewHolder(view, callback);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {

        ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
        if(iconArrayList != null) {
            int itemPos = position % iconArrayList.size();
            imageViewHolder.bind(iconArrayList.get(itemPos));
        }
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView positionText;
        Callback callback;

        public ImageViewHolder(@NonNull @NotNull View itemView, Callback callback) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            positionText = itemView.findViewById(R.id.position_text);
            this.callback = callback;
        }

        public void bind(Icon icon){
            positionText.setText(icon.getName());
            Picasso.get()
                    .load(icon.getIcon())
                    .into(image);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    if (articlesItem.getSource() != null) {
////                        Intent intent = new Intent(activity, ChannelDetailsActivity.class);
////                        intent.putExtra("type", TYPE.SOURCE);
////                        intent.putExtra("id", articlesItem.getSource().getId());
////                        intent.putExtra("name", articlesItem.getSource().getName());
////                        intent.putExtra("favorite", articlesItem.getSource().isFavorite());
////                        activity.startActivity(intent);
////                    } else if (articlesItem.getAuthor() != null && articlesItem.getAuthor().size() > 0) {
////                        if (articlesItem.getAuthor().get(0) != null) {
////                            Utils.openAuthor(activity, articlesItem.getAuthor().get(0).getId());
////                        }
////                    }
//                }
//            });
        }
    }
}

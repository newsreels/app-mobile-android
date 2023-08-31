package com.ziro.bullet.adapters.discover;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.ChannelDetailsActivity;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.ShareInfo;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.LikeInterface;
import com.ziro.bullet.interfaces.ShareInfoInterface;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.presenter.LikePresenter;
import com.ziro.bullet.presenter.ShareBottomSheetPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;
import jp.wasabeef.picasso.transformations.BlurTransformation;
public class ListSmallCardViewHolder extends RecyclerView.ViewHolder {

    private final CardView card;
    private final TextView headline;
    private final TextView source_name;
    private final TextView time;
    private final ImageView imageBack;
    private final ImageView image;
    private final RelativeLayout divider;
    private final RelativeLayout line;
    private final RelativeLayout imageSection;
    private final RelativeLayout dotImg;
    private Activity activity;
    private PrefConfig mPrefConfig;
    private AdapterCallback adapterCallback;
    private ShareBottomSheetPresenter shareBottomSheetPresenter;
    private RelativeLayout commentMain;
    private RelativeLayout click;
    public TextView comment_count;
    private ImageView comment_icon;
    private RelativeLayout likeMain;
    public TextView like_count;
    public ImageView like_icon;
    private ImageView sourceImage;
    private TextView authorName;
    private ImageView separatorDot;
    private LikePresenter presenter;
    private CommentClick commentClick;
    private ShowOptionsLoaderCallback showOptionsLoaderCallback;

    public ListSmallCardViewHolder(CommentClick commentClick, @NonNull View itemView, Activity activity, AdapterCallback adapterCallback,
                                   ShareBottomSheetPresenter shareBottomSheetPresenter, ShowOptionsLoaderCallback showOptionsLoaderCallback) {
        super(itemView);
        this.commentClick = commentClick;
        this.activity = activity;
        this.adapterCallback = adapterCallback;
        this.shareBottomSheetPresenter = shareBottomSheetPresenter;
        this.presenter = new LikePresenter(activity);
        this.showOptionsLoaderCallback = showOptionsLoaderCallback;

        mPrefConfig = new PrefConfig(activity);
        imageSection = itemView.findViewById(R.id.imageSection);
        click = itemView.findViewById(R.id.click);
        dotImg = itemView.findViewById(R.id.dotImg);
        source_name = itemView.findViewById(R.id.source_name);
        line = itemView.findViewById(R.id.line);
        card = itemView.findViewById(R.id.card);
        headline = itemView.findViewById(R.id.headline);
        imageBack = itemView.findViewById(R.id.imageBack);
        image = itemView.findViewById(R.id.image);
        time = itemView.findViewById(R.id.time);
        divider = itemView.findViewById(R.id.divider);
        comment_icon = itemView.findViewById(R.id.comment_icon);
        comment_count = itemView.findViewById(R.id.comment_count);
        commentMain = itemView.findViewById(R.id.commentMain);
        like_icon = itemView.findViewById(R.id.like_icon);
        like_count = itemView.findViewById(R.id.like_count);
        likeMain = itemView.findViewById(R.id.likeMain);
        sourceImage = itemView.findViewById(R.id.source_image);
        authorName = itemView.findViewById(R.id.author_name);
        separatorDot = itemView.findViewById(R.id.separator_dot);
        // signal = itemView.findViewById(R.id.signal);
    }

    public void bind(int position, Article articlesItem) {
        if (articlesItem != null) {

            if (mPrefConfig.isReaderMode()) {
                getImageSection().setVisibility(View.GONE);
            } else {
                getImageSection().setVisibility(View.VISIBLE);
            }


            Glide.with(sourceImage)
                    .load(articlesItem.getSourceImageToDisplay())
                    .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                    .into(sourceImage);
            if (!articlesItem.getAuthorNameToDisplay().equals("")) {
                authorName.setVisibility(View.VISIBLE);
                separatorDot.setVisibility(View.VISIBLE);
                authorName.setText(articlesItem.getAuthorNameToDisplay());
            } else {
                authorName.setVisibility(View.GONE);
                separatorDot.setVisibility(View.GONE);
            }
            source_name.setText(articlesItem.getSourceNameToDisplay());


            if (!TextUtils.isEmpty(articlesItem.getImage())) {
                Picasso.get()
                        .load(articlesItem.getImage())
                        .transform(new BlurTransformation(activity, 25, 1))
                        .into(getImageBack());

                Picasso.get()
                        .load(articlesItem.getImage())
                        .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .into(getImage());
            }

            if (articlesItem.getBullets() != null && articlesItem.getBullets().size() > 0) {
                if (articlesItem.getBullets().get(0) != null && !TextUtils.isEmpty(articlesItem.getBullets().get(0).getData())) {
                    getHeadline().setText(articlesItem.getBullets().get(0).getData());
                    getHeadline().setTextDirection(Utils.getLanguageDirectionForView(articlesItem.getLanguageCode()));
                }
            }

            if (!TextUtils.isEmpty(articlesItem.getPublishTime())) {
                String time = Utils.getTimeAgo(Utils.getDate(articlesItem.getPublishTime()), card.getContext());
                if (!TextUtils.isEmpty(time)) {
                    getTime().setText(time);
                }
            }
//            if (articlesItem.getSource() != null) {
//                if (!TextUtils.isEmpty(articlesItem.getSource().getName())) {
//                    getSource_name().setText(articlesItem.getSource().getName());
//                }
//            } else if (articlesItem.getAuthor() != null && articlesItem.getAuthor().size() > 0) {
//                if (!TextUtils.isEmpty(articlesItem.getAuthor().get(0).getName())) {
//                    getSource_name().setText(articlesItem.getAuthor().get(0).getName());
//                }
//            }

            getCard().setOnClickListener(v -> {
                if(commentClick == null) return;
                commentClick.onDetailClick(position, articlesItem);
            });

            getDotImg().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    showOptionsLoaderCallback.showLoader(true);
                    shareBottomSheetPresenter.share_msg(articlesItem.getId(), new ShareInfoInterface() {
                        @Override
                        public void response(ShareInfo shareInfo) {
                            showOptionsLoaderCallback.showLoader(false);
                            adapterCallback.showShareBottomSheet(shareInfo, articlesItem, dialog -> {
                                Constants.sharePgNotVisible = true;

                            });
                        }

                        @Override
                        public void error(String error) {
                            showOptionsLoaderCallback.showLoader(false);
                        }
                    });
                }
            });

            commentMain.setOnClickListener(v -> {
                if (commentClick != null) commentClick.commentClick(position, articlesItem.getId());
            });

            getClick().setOnClickListener(v -> {
                if (articlesItem.getSource() != null) {
                    Intent intent = new Intent(activity, ChannelDetailsActivity.class);
                    intent.putExtra("type", TYPE.SOURCE);
                    intent.putExtra("id", articlesItem.getSource().getId());
                    intent.putExtra("name", articlesItem.getSource().getName());
                    intent.putExtra("favorite", articlesItem.getSource().isFavorite());
                    activity.startActivity(intent);
                } else if (articlesItem.getAuthor() != null && articlesItem.getAuthor().size() > 0) {
                    if (articlesItem.getAuthor().get(0) != null) {
                        Utils.openAuthor(activity, articlesItem.getAuthor().get(0));
                    }
                }
            });

//            signal.setOnClickListener(v -> {
//                if (articlesItem.getSource() != null) {
//                    Intent intent = new Intent(activity, ChannelPostActivity.class);
//                    intent.putExtra("type", TYPE.SOURCE);
//                    intent.putExtra("id", articlesItem.getSource().getId());
//                    intent.putExtra("name", articlesItem.getSource().getName());
//                    intent.putExtra("favorite", articlesItem.getSource().isFavorite());
//                    activity.startActivity(intent);
//                } else if (articlesItem.getAuthor() != null && articlesItem.getAuthor().size() > 0) {
//                    if (articlesItem.getAuthor().get(0) != null) {
//                        Utils.openAuthor(activity, articlesItem.getAuthor().get(0).getId());
//                    }
//                }
//            });


            if (articlesItem.getInfo() != null) {

                comment_count.setText("" + articlesItem.getInfo().getComment_count());
                like_count.setText("" + articlesItem.getInfo().getLike_count());

                if (articlesItem.getInfo().isLiked()) {
                    like_count.setTextColor(ContextCompat.getColor(activity, R.color.like_heart_filled));
                    like_icon.setImageResource(R.drawable.ic_like_heart_filled);
                } else {
                    like_count.setTextColor(ContextCompat.getColor(activity, R.color.like_disable_text_color));
                    like_icon.setImageResource(R.drawable.ic_like_heart);
                }

                likeMain.setOnClickListener(v -> {
                    likeMain.setEnabled(false);
                    Log.e("likearticlesItem", "like : " + articlesItem.getInfo().isLiked());
                    presenter.like(articlesItem.getId(), new LikeInterface() {
                        @Override
                        public void success(boolean like) {
                            if (activity == null) return;
                            likeMain.setEnabled(true);
                            Log.e("likearticlesItem", "new like: " + like);
                            articlesItem.getInfo().setLiked(like);
                            Log.e("likearticlesItem", "object like : " + articlesItem.getInfo().isLiked());
                            int counter = articlesItem.getInfo().getLike_count();
                            if (like) {
                                counter++;
                            } else {
                                if (counter > 0) {
                                    counter--;
                                } else {
                                    counter = 0;
                                }
                            }
                            articlesItem.getInfo().setLike_count(counter);
                            like_count.setText("" + counter);

                            if (articlesItem.getInfo().isLiked()) {
                                like_count.setTextColor(ContextCompat.getColor(activity, R.color.like_heart_filled));
                                like_icon.setImageResource(R.drawable.ic_like_heart_filled);
                            } else {
                                like_count.setTextColor(ContextCompat.getColor(activity, R.color.like_disable_text_color));
                                like_icon.setImageResource(R.drawable.ic_like_heart);
                            }
                        }

                        @Override
                        public void failure() {
                            likeMain.setEnabled(true);
                        }
                    }, !articlesItem.getInfo().isLiked());
                });
            }

        }

    }

    public RelativeLayout getLikeMain() {
        return likeMain;
    }

    public RelativeLayout getCommentMain() {
        return commentMain;
    }

    public RelativeLayout getImageSection() {
        return imageSection;
    }

    public RelativeLayout getDotImg() {
        return dotImg;
    }

    public RelativeLayout getDivider() {
        return divider;
    }

    public RelativeLayout getLine() {
        return line;
    }

    public CardView getCard() {
        return card;
    }

    public TextView getHeadline() {
        return headline;
    }

    public TextView getSource_name() {
        return source_name;
    }

    public TextView getTime() {
        return time;
    }

    public ImageView getImageBack() {
        return imageBack;
    }

    public ImageView getImage() {
        return image;
    }

    public RelativeLayout getClick() {
        return click;
    }
}

package com.ziro.bullet.adapters.discover;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.BulletDetailActivity;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.NewDiscoverPage.DiscoverItem;
import com.ziro.bullet.utills.Constants;

import jp.wasabeef.picasso.transformations.BlurTransformation;

public class YoutubeCardViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "youtubePlayer";

    //    private final TextView text1;
    private final TextView text2;
    private final ImageView speaker;
    private final ImageView imageBack;
    private final ImageView image;
    private final ImageView play_image;
    //    private final TextView playDuration;
    private final RelativeLayout thumbnail;
    private final LinearLayout card;

    private DiscoverItem bindDiscoverItem;
//    private final YouTubePlayerView youTubePlayerView;
//    private YouTubePlayer youTubePlayer;

    private boolean shouldLoad = true;

    public CardView videoCard;
    public boolean isHeightSet = false;
    private Activity mContext;
    private PrefConfig mPrefConfig;
    private LinearLayout transformationLayout;

    private PlayerConstants.PlayerState currentPlayerState;
    private long youtubeDuration;

    public YoutubeCardViewHolder(@NonNull View itemView, Activity context) {
        super(itemView);

        this.mContext = context;
        mPrefConfig = new PrefConfig(context);
        thumbnail = itemView.findViewById(R.id.thumbnail);
        imageBack = itemView.findViewById(R.id.imageBack);
        image = itemView.findViewById(R.id.image);
        play_image = itemView.findViewById(R.id.play_image);
        videoCard = itemView.findViewById(R.id.videoCard);
//        text1 = itemView.findViewById(R.id.text1);
        text2 = itemView.findViewById(R.id.text2);
//        youTubePlayerView = itemView.findViewById(R.id.video_player);
        speaker = itemView.findViewById(R.id.speaker);
//        playDuration = itemView.findViewById(R.id.play_duration);
        card = itemView.findViewById(R.id.card);
        transformationLayout = itemView.findViewById(R.id.transformationLayout);

//        if (context instanceof AppCompatActivity)
//            ((AppCompatActivity) context).getLifecycle().addObserver(youTubePlayerView);
//
//        youTubePlayerView.addYouTubePlayerListener(new YouTubePlayerListener() {
//            @Override
//            public void onVideoLoadedFraction(@NotNull YouTubePlayer youTubePlayer, float v) {
//                Log.d(TAG, "onVideoLoadedFraction: v = " + v);
//                Log.d(TAG, "onVideoLoadedFraction: shouldLoad = " + shouldLoad);
//                //causing issues in feed auto pause
////                if (!shouldLoad) {
////                    bulletPause();
////                }
//            }
//
//            @Override
//            public void onVideoId(@NotNull YouTubePlayer youTubePlayer, @NotNull String s) {
//            }
//
//            @Override
//            public void onVideoDuration(@NotNull YouTubePlayer youTubePlayer, float v) {
//                Log.d(TAG, "onVideoDuration() called with: youTubePlayer = [" + youTubePlayer + "], v = [" + v + "]");
//            }
//
//            @Override
//            public void onStateChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerState playerState) {
//
//                currentPlayerState = playerState;
//
//                if (playerState == PlayerConstants.PlayerState.PLAYING) {
//                    if (Constants.muted) {
//                        youTubePlayer.mute();
//                    } else {
//                        youTubePlayer.unMute();
//                    }
//                    showFlagAndAudioBtn(false);
//                } else {
//                    showFlagAndAudioBtn(true);
//                }
//
//                if (playerState == PlayerConstants.PlayerState.UNSTARTED) {
//                    thumbnail.setVisibility(View.VISIBLE);
//                } else {
//                    thumbnail.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onPlaybackRateChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlaybackRate playbackRate) {
//
//            }
//
//            @Override
//            public void onPlaybackQualityChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlaybackQuality playbackQuality) {
//
//            }
//
//            @Override
//            public void onError(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerError playerError) {
//                Log.d(TAG, "onError() called with: youTubePlayer = [" + youTubePlayer + "], playerError = [" + playerError + "]");
//            }
//
//            @Override
//            public void onCurrentSecond(@NotNull YouTubePlayer youTubePlayer, float v) {
//
//            }
//
//            @Override
//            public void onApiChange(@NotNull YouTubePlayer youTubePlayer) {
//            }
//
//            @Override
//            public void onReady(@NotNull YouTubePlayer player) {
//                if (youTubePlayer == null) {
//
//                    player.cueVideo(bindDiscoverItem.getData().getVideo().getLink(), 0);
//
//                    Log.d(TAG, "onReady: link = " + bindDiscoverItem.getData().getVideo().getLink());
//
//
////                    if (mPrefConfig.isBulletsAutoPlay()) {
////                        if (bindArticle != null) {
////                            if (bindArticle.isSelected() && shouldLoad) {
////                                player.loadVideo(bindArticle.getLink(), 0);
////                            } else
////                                player.cueVideo(bindArticle.getLink(), 0);
////                        }
////                    } else {
////                        player.cueVideo(bindArticle.getLink(), 0);
////                    }
//                    youTubePlayer = player;
//                }
////                else {
////                    player.cueVideo(bindDiscoverItem.getData().getVideo().getLink(), 0);
////                }
//
//
//            }
//        });
    }

    public void bind(int position, DiscoverItem item) {
//        if (mContext != null) {
////            transformationLayout.setBackground(mContext.getResources().getDrawable(R.drawable.discover_card_shape));
////            text1.setTextColor(mContext.getResources().getColor(R.color.discover_card_grey_text));
////            text2.setTextColor(mContext.getResources().getColor(R.color.discover_card_title));
//        }

        if (item != null && item.getData() != null) {
            bindDiscoverItem = item;
//            if (!TextUtils.isEmpty(item.getSubtitle())) {
//                text1.setText(item.getSubtitle());
//            }
            if (!TextUtils.isEmpty(item.getTitle())) {
                text2.setText(item.getTitle());
            }
            Article video = item.getData().getVideo();
            if (video != null) {
                //mediaUri = Uri.parse(video.getLink());
                if (!TextUtils.isEmpty(video.getImage())) {

                    Picasso.get()
                            .load(video.getImage())
                            .into(image);

                    Picasso.get()
                            .load(video.getImage())
                            .transform(new BlurTransformation(mContext, 25, 3))
                            .into(imageBack);
                }

//                if (video.getMediaMeta()!=null) {
//                    playDuration.setText(video.getMediaMeta().getDurationString());
//                    playDuration.setVisibility(View.VISIBLE);
//                } else {
//                    playDuration.setVisibility(View.GONE);
//                }
            }

//            updateMuteButtons();

//            speaker.setOnClickListener(v -> {
//                try {
//                    if (!Constants.muted) {
//                        if (youTubePlayer != null) {
//                            Constants.muted = true;
//                            Constants.ReelMute = true;
//                            youTubePlayer.mute();
//                        }
//                    } else {
//                        if (youTubePlayer != null) {
//                            Constants.muted = false;
//                            Constants.ReelMute = false;
//                            youTubePlayer.unMute();
//                        }
//                    }
//                    updateMuteButtons();
//                } catch (Exception ignore) {
//                }
//            });

            thumbnail.setOnClickListener(v -> {
//                playVideo(false);
                if (item != null && mContext != null) {


                    Intent intent = new Intent(mContext, BulletDetailActivity.class);
                    intent.putExtra("article", new Gson().toJson(item.getData().getVideo()));
                    //intent.putExtra("position", position);
                    mContext.startActivity(intent);
                }
            });
        }

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item != null && mContext != null) {


                    Intent intent = new Intent(mContext, BulletDetailActivity.class);
                    intent.putExtra("article", new Gson().toJson(item.getData().getVideo()));
                    //intent.putExtra("position", position);
                    mContext.startActivity(intent);
                }
            }
        });
    }

    public void updateMuteButtons() {
        if (!Constants.muted) {
            speaker.setImageResource(R.drawable.ic_speaker_unmute);
        } else {
            speaker.setImageResource(R.drawable.ic_speaker_mute);
        }
    }

    public void playVideo(boolean isAuto) {
        //thumbnail.setVisibility(View.GONE);
//        if (youTubePlayer != null) {
//            if (currentPlayerState != PlayerConstants.PlayerState.PAUSED) {
//                if (bindDiscoverItem.getData() != null && bindDiscoverItem.getData().getVideo() != null)
//                    youTubePlayer.loadVideo(bindDiscoverItem.getData().getVideo().getLink(), 0);
//                if (Constants.muted) {
//                    youTubePlayer.mute();
//                } else {
//                    youTubePlayer.unMute();
//                }
//            }
//            if (mPrefConfig.isBulletsAutoPlay() || !isAuto) {
//                youTubePlayer.play();
//            } else {
//                youTubePlayer.pause();
//            }
//            //isPlaying = true;
//        } else {
//            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
//
//                @Override
//                public void onVideoLoadedFraction(@NotNull YouTubePlayer youTubePlayer, float loadedFraction) {
//                    super.onVideoLoadedFraction(youTubePlayer, loadedFraction);
//                    youtubeDuration = (long) loadedFraction;
//                }
//
//                @Override
//                public void onReady(@NotNull YouTubePlayer initializedYouTubePlayer) {
//                    super.onReady(initializedYouTubePlayer);
//                    youTubePlayer = initializedYouTubePlayer;
//                    if (bindDiscoverItem.getData() != null && bindDiscoverItem.getData().getVideo() != null)
//                        youTubePlayer.loadVideo(bindDiscoverItem.getData().getVideo().getLink(), 0);
//                    if (Constants.muted) {
//                        youTubePlayer.mute();
//                    } else {
//                        youTubePlayer.unMute();
//                    }
//                    if (mPrefConfig.isBulletsAutoPlay()) {
//                        youTubePlayer.play();
//                    } else {
//                        youTubePlayer.pause();
//                    }
//                }
//            });
//        }
    }

    public void pause() {
//        if (youTubePlayer != null)
//            youTubePlayer.pause();
//        youTubePlayer = null;
    }

    public void release() {
//        if (youTubePlayer != null)
//            youTubePlayer.pause();
//        youTubePlayer = null;
    }

    protected void showFlagAndAudioBtn(boolean show) {
        speaker.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}

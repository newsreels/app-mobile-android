package com.ziro.bullet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danikula.videocache.HttpProxyCacheServer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.ziro.bullet.BulletApp;
import com.ziro.bullet.R;
import com.ziro.bullet.data.models.Video.VideoItem;

import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideoViewHolder> {

    private List<VideoItem> mVideoItems;
    private Context context;
    private VideoViewHolder holder;
    private String url;

    public VideosAdapter(List<VideoItem> videoItems, Context context) {
        mVideoItems = videoItems;
        this.context = context;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_videos_container, parent, false), context);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        if (mVideoItems != null) {
            this.holder = holder;
            this.url = mVideoItems.get(position).videoURL;
            holder.setData(context, mVideoItems.get(position));
        }
    }


    @Override
    public int getItemCount() {
        return mVideoItems.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {

        private PlayerView playerView;
        private SimpleExoPlayer player;

        public VideoViewHolder(View itemView, Context context) {
            super(itemView);
            playerView = itemView.findViewById(R.id.video_view);

            if (player == null) {
//                DefaultTrackSelector trackSelector = new DefaultTrackSelector(context);
//                trackSelector.setParameters(
//                        trackSelector.buildUponParameters().setMaxVideoSizeSd());
//                player = new SimpleExoPlayer.Builder(context)
//                        .setTrackSelector(trackSelector)
//                        .build();
            }
        }

        public void setData(Context context, VideoItem object) {
            HttpProxyCacheServer proxy = BulletApp.getProxy(context);
            String proxyUrl = proxy.getProxyUrl(object.videoURL);
//
//            MediaItem mediaItem = new MediaItem.Builder()
//                    .setUri(proxyUrl)
//                    .setMimeType(MimeTypes.APPLICATION_MP4)
//                    .build();
//
//            player.setMediaItem(mediaItem);
//            player.prepare();
//            player.setPlayWhenReady(object.isSelected);
//            player.setRepeatMode(Player.REPEAT_MODE_ONE);
//            playerView.setUseController(false);
//            playerView.hideController();
//            playerView.setShowBuffering(true);
//            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
//            playerView.setPlayer(player);
        }
    }

}
package com.ziro.bullet.activities;

import static com.ziro.bullet.activities.AddPostPlaceActivity.LOCATION_RESULT_KEY;
import static com.ziro.bullet.activities.LanguageActivity.LANGUAGE_RESULT_KEY;
import static com.ziro.bullet.data.POST_TYPE.REELS;
import static com.ziro.bullet.data.POST_TYPE.VIDEO_ARTICLE;
import static com.ziro.bullet.utills.Constants.ACTION_UPDATE_EVENT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.gson.Gson;
import com.nex3z.flowlayout.FlowLayout;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.ziro.bullet.BulletApp;
import com.ziro.bullet.CacheData.DbHandler;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.Channel.PostToChannelActivity;
import com.ziro.bullet.adapters.AddBulletsAdapter;
import com.ziro.bullet.background.UploadInfo;
import com.ziro.bullet.background.VideoInfo;
import com.ziro.bullet.background.VideoProcessorService;
import com.ziro.bullet.background.VideoStatus;
import com.ziro.bullet.data.MODE;
import com.ziro.bullet.data.POST_TYPE;
import com.ziro.bullet.data.PostArticleParams;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.location.Location;
import com.ziro.bullet.data.models.postarticle.CurrentLocations;
import com.ziro.bullet.data.models.postarticle.CurrentTags;
import com.ziro.bullet.data.models.postarticle.TagItem;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.fragments.LoadingProgressDialog;
import com.ziro.bullet.fragments.SchedulePickerDialog;
import com.ziro.bullet.interfaces.AdapterItemCallback;
import com.ziro.bullet.interfaces.PostArticleCallback;
import com.ziro.bullet.mediapicker.GalleryPicker;
import com.ziro.bullet.mediapicker.config.PictureConfig;
import com.ziro.bullet.mediapicker.dialog.PictureLoadingDialog;
import com.ziro.bullet.mediapicker.engine.GlideCacheEngine;
import com.ziro.bullet.mediapicker.engine.GlideEngine;
import com.ziro.bullet.mediapicker.entity.LocalMedia;
import com.ziro.bullet.mediapicker.permissions.PermissionChecker;
import com.ziro.bullet.mediapicker.thread.PictureThreadUtils;
import com.ziro.bullet.mediapicker.utils.PictureSelector;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.model.SelectedChannel;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.Bullet;
import com.ziro.bullet.model.articles.MediaMeta;
import com.ziro.bullet.model.language.LanguagesItem;
import com.ziro.bullet.model.searchhistory.History;
import com.ziro.bullet.presenter.PostArticlePresenter;
import com.ziro.bullet.utills.BlurTransformation;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostArticleActivity extends BaseActivity implements PostArticleCallback, AdapterItemCallback {

    private final int PERMISSION_REQUEST_IMAGE = 212;
    private final int PERMISSION_REQUEST_VIDEO = 121;
    private final int headlineCharLengthVideo = 150;
    private RelativeLayout back;
    private ConstraintLayout next_btn;
    private ImageView image;
    private ImageView imageBack;
    private CardView card;
    private TextView heading;
    private TextView from_label;
    private TextView name;
    private TextView headerTxt;
    private CircleImageView profile_image;
    private EditText headline;
    private EditText reference;
    private RecyclerView rvBullets;
    private RelativeLayout add_bullet;
    private RelativeLayout progress;
    private RelativeLayout root;
    private LinearLayout bulletSection;
    private LinearLayout replace_image;
    private LinearLayout llSchedule;
    private TextView tvSchedule;
    private ImageView ivScheduleArrow;
    private TextView counter;
    private ImageView playButton;
    private TextView next_btn_text;
    private PlayerView video_player;
    private YouTubePlayerView youtube_view;
    private String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private PostArticlePresenter presenter;
    private File imageFile = null;
    private String date_pub = "";
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private AddBulletsAdapter addBulletsAdapter;
    private PrefConfig prefConfig;
    private SimpleExoPlayer simpleExoPlayer;
    private ArrayList<Bullet> paramsBullets = new ArrayList<>();
    private POST_TYPE postType = POST_TYPE.UNKNOWN;
    private MODE mode;
    private File file;
    private Uri uri;
    private PostArticleParams postArticleParams = new PostArticleParams();
    private Article article = null;
    private boolean isVideoChanged = false;
    private List<LocalMedia> selectList = null;
    private LocalMedia localMedia = null;
    private VideoInfo videoInfo = null;
    private LoadingProgressDialog mLoadingDialog;
    private boolean isValidateApiSuccess = false;
    private boolean isUploadMediaApiSuccess = false;
    private ArrayList<TagItem> tagItemArrayList = new ArrayList<>();
    private ArrayList<Location> locationItemArrayList = new ArrayList<>();
    private FlowLayout flow_layout, places_layout, language_layout;
    private RelativeLayout tag, place, language, sourceLink;
    private View line1, line2;
    private RelativeLayout post_to_profile;
    private String schedule = "";
    private String imageUrl = null;
    private PictureLoadingDialog mIndefiniteLoadingDialog;
    private DbHandler dbHandler;
    private LanguagesItem languagesItem;
    private LinearLayout ll_placeholder;


    @Override
    protected void onResume() {
        super.onResume();
        if (bullets.size() < 5) {
            add_bullet.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Utils.checkAppModeColor(this, false);
        setContentView(R.layout.activity_post_article);
        bindView();
        setListener();
        init();
        setData();
    }

    private boolean checkBullets() {
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            if (bullet != null) {
                if (TextUtils.isEmpty(bullet.getData())) {
                    return false;
                }
            }
        }
        return true;
    }

    private void checkParams() {
        if (postType != null) {
            switch (postType) {
                case ARTICLE:
                    isNextEnable(((file != null || article != null) && !TextUtils.isEmpty(headline.getText().toString().trim()) && checkBullets()));
                    break;
                case VIDEO_ARTICLE:
                    isNextEnable(((uri != null || article != null) && !TextUtils.isEmpty(headline.getText().toString().trim()) && checkBullets()));
                    break;
                case YOUTUBE:
                    isNextEnable((!TextUtils.isEmpty(headline.getText().toString().trim())));
                    break;
                case REELS:
                    isNextEnable((uri != null && !TextUtils.isEmpty(headline.getText().toString().trim())));
                    break;
            }
        }
    }

    private void isNextEnable(boolean isEnable) {
        Log.d("isNextEnable", "isNextEnable() called with: isEnable = [" + isEnable + "]");
//        next_btn.setEnabled(isEnable);
        if (isEnable) {
            next_btn.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_post_article_button));
//            Utils.setDrawableColor(next_btn.getBackground(), getResources().getColor(R.color.primaryRed));
            next_btn_text.setTextColor(getResources().getColor(R.color.white));
        } else {
            next_btn.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_post_article_button_disable));
            next_btn_text.setTextColor(getResources().getColor(R.color.disable_next_btn_text));
        }
    }

    public void article() {
        video_player.setVisibility(View.GONE);
        youtube_view.setVisibility(View.GONE);
        bulletSection.setVisibility(View.VISIBLE);
        sourceLink.setVisibility(View.VISIBLE);
        replace_image.setVisibility(View.VISIBLE);
        image.setVisibility(View.VISIBLE);
        imageBack.setVisibility(View.VISIBLE);
        headerTxt.setText(getString(R.string.post_article_));
        heading.setText(getString(R.string.headline));
        headline.setHint(getString(R.string.create_a_headline));
        from_label.setHint(getString(R.string.upload_media));
    }

    public void videoArticle(boolean isYoutube) {
        if (isYoutube) {
            video_player.setVisibility(View.GONE);
            youtube_view.setVisibility(View.VISIBLE);
            replace_image.setVisibility(View.GONE);
        } else {
            replace_image.setVisibility(View.VISIBLE);
            video_player.setVisibility(View.VISIBLE);
            youtube_view.setVisibility(View.GONE);
        }
        image.setVisibility(View.GONE);
        bulletSection.setVisibility(View.GONE);
        imageBack.setVisibility(View.GONE);
        headerTxt.setText(getString(R.string.post_article_));
        heading.setText(getString(R.string.headline));
        headline.setHint(getString(R.string.create_a_headline));
        from_label.setHint(getString(R.string.upload_media));
    }

    public void localVideo() {
        playButton.setVisibility(View.VISIBLE);
        if (postType == REELS) {
            sourceLink.setVisibility(View.VISIBLE);
        }
        video_player.setVisibility(View.GONE);
        youtube_view.setVisibility(View.GONE);
        replace_image.setVisibility(View.GONE);
        image.setVisibility(View.VISIBLE);
        bulletSection.setVisibility(View.GONE);
        imageBack.setVisibility(View.VISIBLE);
        headerTxt.setText(getString(R.string.post_article_));
        heading.setText(getString(R.string.headline));
        headline.setHint(getString(R.string.create_a_headline));
        from_label.setHint(getString(R.string.upload_media));
    }

    public void reels() {
        video_player.setVisibility(View.VISIBLE);
        replace_image.setVisibility(View.VISIBLE);
        sourceLink.setVisibility(View.VISIBLE);
        youtube_view.setVisibility(View.GONE);
        bulletSection.setVisibility(View.GONE);
        image.setVisibility(View.GONE);
        imageBack.setVisibility(View.GONE);
        headerTxt.setText(getString(R.string.post_newsreels));
        heading.setText(getString(R.string.caption));
        headline.setHint(getString(R.string.create_a_caption));
        from_label.setHint(getString(R.string.upload_news_reel));
    }

    private void setData() {
        if (prefConfig != null && prefConfig.isUserObject() != null) {
            if (!TextUtils.isEmpty(prefConfig.isUserObject().getProfile_image())) {
                Glide.with(this)
                        .load(prefConfig.isUserObject().getProfile_image())
                        .into(profile_image);
            }
            if (!TextUtils.isEmpty(prefConfig.isUserObject().getFirst_name()) && !TextUtils.isEmpty(prefConfig.isUserObject().getLast_name())) {
                name.setText(prefConfig.isUserObject().getFirst_name() + " " + prefConfig.isUserObject().getLast_name());
            } else if (!TextUtils.isEmpty(prefConfig.isUserObject().getFirst_name())) {
                name.setText(prefConfig.isUserObject().getFirst_name());
            }
        }

        if (getIntent() != null) {
            postType = (POST_TYPE) getIntent().getSerializableExtra("POST_TYPE");
            mode = (MODE) getIntent().getSerializableExtra("MODE");
            if (!TextUtils.isEmpty(getIntent().getStringExtra("uri"))) {
                uri = Uri.parse(getIntent().getStringExtra("uri"));
            }

            if (getIntent().hasExtra("SCHEDULE")) {
                schedule = getIntent().getStringExtra("SCHEDULE");
                tvSchedule.setTextColor(ContextCompat.getColor(this, R.color.title_bar_title));
                tvSchedule.setText(schedule);
                tvSchedule.setText(Utils.getCustomDate(schedule, "MMM dd, hh:mm aaa"));
                ivScheduleArrow.setVisibility(View.GONE);
            }
            file = (File) getIntent().getSerializableExtra("file");
            if (getIntent().hasExtra("localMedia")) {
                localMedia = new Gson().fromJson(getIntent().getStringExtra("localMedia"), LocalMedia.class);
            }

            if (getIntent().hasExtra("video_info")) {
                videoInfo = getIntent().getExtras().getParcelable("video_info");
            }

            if (getIntent().hasExtra("article")) {
                article = new Gson().fromJson(getIntent().getStringExtra("article"), Article.class);
                if (article != null && !TextUtils.isEmpty(article.getStatus()) && article.getStatus().equalsIgnoreCase(Constants.ARTICLE_PUBLISHED) && mode == MODE.EDIT) {
                    llSchedule.setVisibility(View.GONE);
                }
                if (article != null && article.getSource() != null) {
                    prefConfig.setChannel(
                            new SelectedChannel(
                                    article.getSource().getId(),
                                    article.getSource().getIcon(),
                                    article.getSource().getName())
                    );
                }
            }

            if (postType != null) {
                switch (postType) {
                    case ARTICLE:
                        article();
                        break;
                    case VIDEO_ARTICLE:
                        headline.setFilters(new InputFilter[]{new InputFilter.LengthFilter(headlineCharLengthVideo)});
                        counter.setText("0/" + headlineCharLengthVideo);
                        if (videoInfo != null)
                            localVideo();
                        else
                            videoArticle(false);
                        break;
                    case YOUTUBE:
                        headline.setFilters(new InputFilter[]{new InputFilter.LengthFilter(headlineCharLengthVideo)});
                        counter.setText("0/" + headlineCharLengthVideo);
                        videoArticle(true);
                        break;
                    case REELS:
                        headline.setFilters(new InputFilter[]{new InputFilter.LengthFilter(headlineCharLengthVideo)});
                        counter.setText("0/" + headlineCharLengthVideo);
                        if (videoInfo != null)
                            localVideo();
                        else
                            reels();
                        break;
                    default:
                }

                switch (mode) {
                    case ADD:
                        isNextEnable(false);
                        next_btn_text.setText(getString(R.string.next));
                        switch (postType) {
                            case ARTICLE:
                                headerTxt.setText(getString(R.string.post_article_));
                                bullets.add(new Bullet(getString(R.string.add_bullet_)));
                                bullets.add(new Bullet(getString(R.string.add_bullet_)));
                                if (file != null) {
                                    setImagesFromFile(file);
                                } else {
                                    image.setImageResource(R.drawable.img_place_holder);
                                }
                                break;
                            case REELS:
                                headerTxt.setText(getString(R.string.post_newsreels));
//                                if (uri != null) {
//                                    initializePlayer(uri.toString());
//                                }
                                if (!TextUtils.isEmpty(videoInfo.getPath())) {
                                    Glide.with(this)
                                            .load(videoInfo.getPath())
                                            .apply(RequestOptions.bitmapTransform(new com.ziro.bullet.utills.BlurTransformation(50, 10)))
                                            .override(Constants.targetWidth, Constants.targetHeight)
                                            .into(imageBack);

                                    Glide.with(this)
                                            .load(videoInfo.getPath())
                                            .error(Utils.getPlaceholderForTheme(prefConfig.getAppTheme()))
                                            .placeholder(Utils.getPlaceholderForTheme(prefConfig.getAppTheme()))
                                            .into(image);
                                }
                                break;
                            case VIDEO_ARTICLE:
                                headerTxt.setText(getString(R.string.post_article_));
//                                if (uri != null) {
//                                    initializePlayer(uri.toString());
//                                }

                                if (!TextUtils.isEmpty(videoInfo.getPath())) {
                                    Glide.with(this)
                                            .load(videoInfo.getPath())
                                            .apply(RequestOptions.bitmapTransform(new com.ziro.bullet.utills.BlurTransformation(50, 10)))
                                            .override(Constants.targetWidth, Constants.targetHeight)
                                            .into(imageBack);

                                    Glide.with(this)
                                            .load(videoInfo.getPath())
                                            .error(Utils.getPlaceholderForTheme(prefConfig.getAppTheme()))
                                            .placeholder(Utils.getPlaceholderForTheme(prefConfig.getAppTheme()))
                                            .into(image);
                                }
                                break;
                            case YOUTUBE:
                                headerTxt.setText(getString(R.string.post_article_));
                                if (article != null) {
                                    headline.setText(article.getTitle());
                                    if (article.getBullets().get(0).getDuration() == 0) {
                                        youtube_view.getPlayerUiController().enableLiveVideoUi(true);
                                    } else {
                                        youtube_view.getPlayerUiController().enableLiveVideoUi(false);
                                    }
                                    initYouTube(article.getLink());
                                }
                                break;
                        }

                        break;
                    case EDIT:
                        isNextEnable(true);
                        isValidateApiSuccess = true;
                        isUploadMediaApiSuccess = true;
                        if (article != null) {
                            if (!TextUtils.isEmpty(article.getStatus()) && article.getStatus().equalsIgnoreCase(Constants.ARTICLE_DRAFT)) {
                                next_btn_text.setText(getString(R.string.next));
                            } else {
                                next_btn_text.setText(getString(R.string.save));
                            }
                            if (!TextUtils.isEmpty(article.getLanguageCode())) {
                                language_layout.removeAllViews();
                                language_layout.addView(createTagItem(article.getLanguageCode(), article.getId()));
                            }
                            if (!TextUtils.isEmpty(article.getPublishTime())) {
                                String validDate = Utils.getCustomDateIfValid(article.getPublishTime(), "MMM dd, hh:mm aaa");
                                if (!validDate.equals("")) {
                                    schedule = article.getPublishTime();
                                    tvSchedule.setText(Utils.getCustomDate(article.getPublishTime(), "MMM dd, hh:mm aaa"));
                                }
                            }
                            List<Bullet> bullet = article.getBullets();
                            if (bullet != null && bullet.size() > 0) {

                                tag.setVisibility(View.VISIBLE);
                                line1.setVisibility(View.VISIBLE);
                                place.setVisibility(View.VISIBLE);
                                line2.setVisibility(View.VISIBLE);
                                language.setVisibility(View.VISIBLE);

                                presenter.getTagList(article.getId());
                                presenter.getLocationList(article.getId());

                                switch (postType) {
                                    case ARTICLE:

                                        headerTxt.setText(getString(R.string.edit_article));
                                        if (!TextUtils.isEmpty(bullet.get(0).getImage())) {
                                            Glide.with(BulletApp.getInstance())
                                                    .load(article.getBullets().get(0).getImage())
                                                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                                    .dontAnimate()
                                                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(50, 10)))
                                                    .override(Constants.targetWidth, Constants.targetHeight)
                                                    .into(imageBack);

                                            Glide.with(BulletApp.getInstance())
                                                    .load(bullet.get(0).getImage())
                                                    .dontAnimate()
                                                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                                    .listener(new RequestListener<Drawable>() {
                                                        @Override
                                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                            image.setImageResource(R.drawable.img_place_holder);
                                                            return false;
                                                        }

                                                        @Override
                                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                            return false;
                                                        }

                                                    })
                                                    .error(R.drawable.img_place_holder)
                                                    .placeholder(R.drawable.img_place_holder)
                                                    .override(Constants.targetWidth, Constants.targetHeight)
                                                    .into(image);

                                        } else {
                                            image.setImageResource(R.drawable.img_place_holder);
                                        }
                                        break;
                                    case YOUTUBE:
                                        headerTxt.setText(getString(R.string.edit_article));
                                        if (article.getBullets().get(0).getDuration() == 0) {
                                            youtube_view.getPlayerUiController().enableLiveVideoUi(true);
                                        } else {
                                            youtube_view.getPlayerUiController().enableLiveVideoUi(false);
                                        }
                                        initYouTube(article.getLink());
                                        break;
                                    case VIDEO_ARTICLE:
                                        headerTxt.setText(getString(R.string.edit_article));
                                        initializePlayer(article.getLink());
                                        break;
                                    case REELS:
                                        headerTxt.setText(getString(R.string.edit_newsreels));
                                        initializePlayer(article.getLink());
                                        break;
                                    default:
                                }

                                for (int i = 0; i < bullet.size(); i++) {
                                    if (i == 0) {
                                        //HEADLINE
                                        headline.setText(bullet.get(i).getmData());
                                    } else {
                                        //BULLETS
                                        bullets.add(new Bullet(bullet.get(i).getmData(), bullet.get(i).getImage(), getString(R.string.bullet)));
                                    }
                                }
                            } else {
                                image.setImageResource(R.drawable.img_place_holder);
                            }

                            if (!TextUtils.isEmpty(article.getLink())) {
                                reference.setText(article.getLink());
                            }
                        }
                        break;
                    case DELETE:
                        next_btn_text.setText(getString(R.string.delete));
                        switch (postType) {
                            case ARTICLE:
                            case YOUTUBE:
                            case VIDEO_ARTICLE:
                                headerTxt.setText(getString(R.string.delete_article));
                                break;
                            case REELS:
                                headerTxt.setText(getString(R.string.delete_newsreels));
                                break;
                            default:
                        }
                        break;
                    default:
                }
            }
        }
        addBulletsAdapter = new AddBulletsAdapter(this, this, bullets);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvBullets.setLayoutManager(manager);
        rvBullets.setAdapter(addBulletsAdapter);
        setChannel();
    }

    private void setImagesFromFile(File file) {
        if (file == null) return;
        Glide.with(this)
                .load(file)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(50, 10)))
                .override(Constants.targetWidth, Constants.targetHeight)
                .into(imageBack);

        Glide.with(this)
                .load(file)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        image.setImageResource(R.drawable.img_place_holder);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }

                })
                .error(R.drawable.img_place_holder)
                .placeholder(R.drawable.img_place_holder)
                .override(Constants.targetWidth, Constants.targetHeight)
                .into(image);
    }

    private void init() {
        prefConfig = new PrefConfig(this);
        presenter = new PostArticlePresenter(this, this);
        dbHandler = new DbHandler(this);
    }

    private void setListener() {
        back.setOnClickListener(v -> finish());
        next_btn.setOnClickListener(v -> {

            switch (postType) {
                case ARTICLE:
                    paramsBullets.clear();
                    if (TextUtils.isEmpty(headline.getText())) {
                        Utils.showSnacky(headline, getString(R.string.enter_headline));
                        return;
                    }
//                    if (!TextUtils.isEmpty(reference.getText())) {
//                        if (!Patterns.WEB_URL.matcher(reference.getText().toString()).matches()) {
//                            error(getString(R.string.enter_valid_source));
//                            return;
//                        }
//                    }
                    // Validation for bullets array
                    for (int i = 0; i < bullets.size(); i++) {
                        Bullet bullet = bullets.get(i);
                        if (bullet != null) {
                            if (TextUtils.isEmpty(bullet.getData())) {
                                if (i <= 1) {
                                    if (i == 0) {
                                        Utils.showSnacky(reference, "Enter Bullet 1");
                                    } else if (i == 1) {
                                        Utils.showSnacky(reference, "Enter Bullet 2");
                                    }
                                    return;
                                } else {
                                    bullets.remove(bullet);
                                }
                            } else {
                                paramsBullets.add(bullet);
                            }
                        }
                    }
                    addBulletsAdapter.notifyDataSetChanged();
                    break;
                case VIDEO_ARTICLE:
                case YOUTUBE:
                    if (TextUtils.isEmpty(headline.getText())) {
                        Utils.showSnacky(headline, getString(R.string.enter_headline));
                        return;
                    }
                    break;
                case REELS:
                    if (TextUtils.isEmpty(headline.getText())) {
                        Utils.showSnacky(headline, getString(R.string.enter_caption));
                        return;
                    }
                default:
            }

            //in case of failure of any api call
            if (isValidateApiSuccess) {
                setParams();
            } else {
                //call validate api only
                if (localMedia != null && presenter != null && postType != null) {
                    float duration = localMedia.getDuration() / 1000;
                    long size = localMedia.getSize();
                    String type = localMedia.getMimeType();
                    if (postType == VIDEO_ARTICLE) {
                        presenter.validateMedia(size, duration, type, "video");
                    } else if (postType == REELS) {
                        presenter.validateMedia(size, duration, type, "reel");
                    } else if (postType == POST_TYPE.ARTICLE) {
                        presenter.validateMedia(size, 0, type, "image");
                    }
                } else {
                    setParams();
                }
            }
        });
        replace_image.setOnClickListener(v -> {
            //gallery
            if (mode == null) return;
            if (postType == null) return;
            switch (mode) {
                case ADD:
                case EDIT:
                    if (PermissionChecker.checkSelfPermission(PostArticleActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                            PermissionChecker.checkSelfPermission(PostArticleActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        switch (postType) {
                            case ARTICLE:
                                GalleryPicker.openGalleryOnlyPictures(PostArticleActivity.this, GlideEngine.createGlideEngine(), GlideCacheEngine.createCacheEngine(), new ArrayList<>(), PERMISSION_REQUEST_IMAGE);
                                break;
                            case REELS:
                                GalleryPicker.openGalleryOnlyVideo(PostArticleActivity.this, true, GlideEngine.createGlideEngine(), GlideCacheEngine.createCacheEngine(), new ArrayList<>(), PERMISSION_REQUEST_VIDEO);
                                break;
                            case VIDEO_ARTICLE:
                                GalleryPicker.openGalleryOnlyVideo(PostArticleActivity.this, false, GlideEngine.createGlideEngine(), GlideCacheEngine.createCacheEngine(), new ArrayList<>(), PERMISSION_REQUEST_VIDEO);
                                break;
                        }
                    } else {
                        PermissionChecker.requestPermissions(PostArticleActivity.this, new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
                    }
                    break;
            }
        });
//        video_player.setOnClickListener(v -> {
//            if (mode == null) return;
//            if (postType == null) return;
//            switch (mode) {
//                case EDIT:
//                    if (PermissionChecker.checkSelfPermission(PostArticleActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
//                            PermissionChecker.checkSelfPermission(PostArticleActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                        switch (postType) {
//                            case VIDEO_ARTICLE:
//                                GalleryPicker.openGalleryOnlyVideo(PostArticleActivity.this, GlideEngine.createGlideEngine(), GlideCacheEngine.createCacheEngine(), new ArrayList<>(), PERMISSION_REQUEST_VIDEO);
//                                break;
//                        }
//                    } else {
//                        PermissionChecker.requestPermissions(PostArticleActivity.this, new String[]{
//                                Manifest.permission.READ_EXTERNAL_STORAGE,
//                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
//                    }
//                    break;
//            }
//        });
        add_bullet.setOnClickListener(v -> {
            if (bullets.size() == 4) {
                add_bullet.setVisibility(View.GONE);
            }
            if (bullets.size() < 5) {
                bullets.add(new Bullet(getString(R.string.add_bullet_)));
                addBulletsAdapter.notifyDataSetChanged();
            } else {
                add_bullet.setVisibility(View.GONE);
            }
        });
        headline.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    if (postType == POST_TYPE.ARTICLE) {
                        counter.setText(s.length() + "/100");
                    } else {
                        counter.setText(s.length() + "/" + headlineCharLengthVideo);
                    }
                }
                checkParams();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tag.setOnClickListener(v -> {
            if (article != null) {
                Intent intent = new Intent(PostArticleActivity.this, AddTagActivity.class);
                intent.putExtra(AddTagActivity.ARTICLE_ID_KEY, article.getId());
                startActivityForResult(intent, 1);
            }
        });
        place.setOnClickListener(v -> {
            if (article != null) {
                Intent intent = new Intent(PostArticleActivity.this, AddPostPlaceActivity.class);
                intent.putExtra(AddTagActivity.ARTICLE_ID_KEY, article.getId());
                startActivityForResult(intent, 3);
            }
        });
        language.setOnClickListener(v -> {
            if (article != null) {
                Intent intent = new Intent(PostArticleActivity.this, LanguageActivity.class);
                intent.putExtra(LanguageActivity.ARTICLE_ID_KEY, article.getId());
                intent.putExtra("isPostLanguageSelection", true);
                intent.putExtra("languagesItemSelected", languagesItem);
                startActivityForResult(intent, 4);
            }
        });

        llSchedule.setOnClickListener(v -> {
            SchedulePickerDialog schedulePickerDialog = new SchedulePickerDialog(this);
            schedulePickerDialog.showDialog(new SchedulePickerDialog.ScheduleSelectionCallback() {
                @Override
                public void onScheduleSelected(Calendar selectedCalendar) {
                    schedule = Utils.convertLocalCalendarToUTC(selectedCalendar);
                    tvSchedule.setTextColor(ContextCompat.getColor(PostArticleActivity.this, R.color.title_bar_title));
                    tvSchedule.setText(Utils.convertCalendarToLocalString(selectedCalendar));
                    ivScheduleArrow.setVisibility(View.GONE);
                }

                @Override
                public void onCleared() {
                    schedule = "";
                    tvSchedule.setTextColor(ContextCompat.getColor(PostArticleActivity.this, R.color.post_article_text));
                    tvSchedule.setText(getString(R.string.scheduled_post));
                    ivScheduleArrow.setVisibility(View.VISIBLE);
                }
            }, true);
        });

        card.setOnClickListener(v -> {
            if (videoInfo != null) {
                Intent intent = new Intent(this, VideoPreviewActivity.class);
                intent.putExtra("video_info", videoInfo);
                startActivity(intent);
            }
        });
        post_to_profile.setOnClickListener(v -> {
            Intent intent = new Intent(PostArticleActivity.this, PostToChannelActivity.class);
            startActivityForResult(intent, 232);
        });
    }

    private void setParams() {
        postArticleParams = new PostArticleParams();
        //if (!TextUtils.isEmpty(schedule))
        postArticleParams.setSchedule(schedule);
        setChannel();
        Utils.hideKeyboard(this, root);
        switch (postType) {
            case ARTICLE:
                if (paramsBullets.size() > 0) {
                    postArticleParams.setBullets(paramsBullets);
                    postArticleParams.setLink(reference.getText().toString().trim());
                    postArticleParams.setTitle(headline.getText().toString().trim());
                    postArticleParams.setType("image");
                    if (article != null) //if coming back from preview page, on press save again image us empty
                        postArticleParams.setImage(article.getImage());
                    else if (!TextUtils.isEmpty(imageUrl)) {
                        postArticleParams.setImage(imageUrl);
                    }
                }
                switch (mode) {
                    case ADD:
                        if (isUploadMediaApiSuccess) {
                            if (article != null) //if coming back from preview page, on press save again image us empty
                                postArticleParams.setId(article.getId());
                            presenter.createArticle(postArticleParams, false);
                        } else {
                            if (file == null) {
                                Utils.showSnacky(headline, getString(R.string.media_required));
                                return;
                            }
                            //call upload media api only
                            presenter.uploadImageVideo(file, "images");
                        }
                        break;
                    case EDIT:
                        if (article != null) {
                            postArticleParams.setId(article.getId());
                            postArticleParams.setImage(article.getImage());
                        }
                        if (!TextUtils.isEmpty(imageUrl)) {
                            postArticleParams.setImage(imageUrl);
                        }
                        Log.e("params", "-params->" + new Gson().toJson(postArticleParams));

                        if (article != null && !TextUtils.isEmpty(article.getStatus()) && article.getStatus().equalsIgnoreCase(Constants.ARTICLE_DRAFT)) {
                            presenter.createArticle(postArticleParams, false);
                        } else {

                            if (isUploadMediaApiSuccess) {
                                presenter.createArticle(postArticleParams, false);
                            } else {
                                if (file == null) {
                                    Utils.showSnacky(headline, getString(R.string.media_required));
                                    return;
                                }
                                //call upload media api only
                                presenter.uploadImageVideo(file, "images");
                            }
                        }
                        break;
                    case DELETE:
                        if (article != null)
                            presenter.publishArticle(article.getId(), "UNPUBLISHED");
                        break;
                }
                break;
            case VIDEO_ARTICLE:
                postArticleParams.setTitle(headline.getText().toString().trim());
                postArticleParams.setType("video");
                switch (mode) {
                    case ADD:
                        if (isUploadMediaApiSuccess) {
                            if (article != null) { //if coming back from preview page, on press save again image us empty
                                postArticleParams.setId(article.getId());
                                postArticleParams.setVideo(article.getLink().trim());
                                presenter.createArticle(postArticleParams, false);
                            }
                        } else {
                            if (article != null) { //if coming back from preview page, on press save again image us empty
                                postArticleParams.setId(article.getId());
                                if (!TextUtils.isEmpty(article.getLink()))
                                    postArticleParams.setVideo(article.getLink().trim());
                            }
                            presenter.createArticle(postArticleParams, false);
//                            //call upload media api only
//                            if (uri == null) return;
//                            presenter.uploadImageVideo(new File(uri.toString()), "videos");
                        }
                        break;
                    case EDIT:
                        if (article != null) {
                            postArticleParams.setId(article.getId());
                            if (isVideoChanged) {
                                postArticleParams.setVideo(article.getLink());
                            }
                        }
                        if (isUploadMediaApiSuccess) {
                            presenter.createArticle(postArticleParams, false);
                        } else {
                            presenter.createArticle(postArticleParams, false);
                            //call upload media api only
//                            if (uri == null) return;
//                            presenter.uploadImageVideo(new File(uri.toString()), "videos");
                        }
                        break;
                    case DELETE:
                        if (article != null)
                            presenter.publishArticle(article.getId(), "UNPUBLISHED");
                        break;
                }
                break;
            case YOUTUBE:
                postArticleParams.setHeadline(headline.getText().toString().trim());
                if (article != null) {
                    postArticleParams.setYoutube_id(article.getLink());
                    postArticleParams.setId(article.getId());
                    postArticleParams.setType("youtube");
                }
                switch (mode) {
                    case EDIT:
                    case ADD:
                        if (article != null)
                            postArticleParams.setId(article.getId());
                        presenter.createArticle(postArticleParams, false);
                        break;
                    case DELETE:
                        if (article != null)
                            presenter.publishArticle(article.getId(), "UNPUBLISHED");
                        break;
                }
                break;
            case REELS:
                postArticleParams.setDescription(headline.getText().toString().trim());
                postArticleParams.setLink(reference.getText().toString().trim());
                postArticleParams.setType("reels");
                switch (mode) {
                    case EDIT:
                        if (article != null) {
                            if (isVideoChanged) {
                                postArticleParams.setMedia(article.getLink());
                            }
                            postArticleParams.setId(article.getId());
                        }
                        if (isUploadMediaApiSuccess) {
                            presenter.createArticle(postArticleParams, true);
                        } else {
                            presenter.createArticle(postArticleParams, true);
                            //call upload media api only
//                            if (uri == null) return;
//                            presenter.uploadImageVideo(new File(uri.toString()), "videos");
                        }
                        break;
                    case ADD:
                        postArticleParams.setStatus("DRAFT");
                        if (isUploadMediaApiSuccess) {
                            if (article != null) { //if coming back from preview page, on press save again image us empty
                                postArticleParams.setId(article.getId());
                                postArticleParams.setMedia(article.getLink());
                                sentArticleForReview(article);
                            }
                        } else {
                            if (article != null) { //if coming back from preview page, on press save again image us empty
                                postArticleParams.setId(article.getId());
                                if (!TextUtils.isEmpty(article.getLink()))
                                    postArticleParams.setMedia(article.getLink());
                            }
                            presenter.createArticle(postArticleParams, true);
                            //call upload media api only
//                            if (uri == null) return;
//                            presenter.uploadImageVideo(new File(uri.toString()), "videos");
                        }
                        break;
                    case DELETE:
                        if (article != null)
                            presenter.publishArticle(article.getId(), "UNPUBLISHED");
                        break;
                }
                break;
            default:
        }
    }

    private View createTagItem(String tagStr, String id) {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View v = vi.inflate(R.layout.tag_layout, null);

        TextView tagText = v.findViewById(R.id.tag_text);
        ImageView removeTag = v.findViewById(R.id.remove_tag);

        tagText.setText(tagStr);
        if (!id.equals("")) {
            v.setTag(id);
        }

        return v;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case 1:
                    ArrayList<TagItem> tagItemArrayList = (ArrayList<TagItem>) data.getSerializableExtra(AddTagActivity.TAG_RESULT_KEY);
                    if (tagItemArrayList != null) {
                        flow_layout.removeAllViews();
                        for (TagItem tagItem : tagItemArrayList) {
                            flow_layout.addView(createTagItem(tagItem.getName(), tagItem.getId()));
                        }
                    }
                    break;
                case 3:
                    ArrayList<Location> locationArrayListSelected = (ArrayList<Location>) data.getSerializableExtra(LOCATION_RESULT_KEY);
                    if (locationArrayListSelected != null && locationArrayListSelected.size() > 0) {
                        places_layout.removeAllViews();
                        for (Location tagItem : locationArrayListSelected) {
                            places_layout.addView(createTagItem(tagItem.getNameToShow(), tagItem.getId()));
                        }
                    }
                    break;
                case 4:
                    languagesItem = (LanguagesItem) data.getSerializableExtra(LANGUAGE_RESULT_KEY);
                    if (languagesItem != null && !TextUtils.isEmpty(languagesItem.getName())) {
                        language_layout.removeAllViews();
                        language_layout.addView(createTagItem(languagesItem.getName(), languagesItem.getId()));
                    }
                    break;
                case 2:
                    if (data.getClipData() != null) {
                        int count = data.getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            Uri imageUri = data.getClipData().getItemAt(i).getUri();

                            String selectedImagePath = getRealPathFromURI(imageUri);
                            File file = new File(selectedImagePath);
                            imageFile = file;
                            image.setImageURI(imageUri);

                            Glide.with(this)
                                    .load(imageUri)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3)))
                                    .into(imageBack);
                        }
                    } else if (data.getData() != null) {
                        String selectedImagePath = getRealPathFromURI(data.getData());
                        File file = new File(selectedImagePath);
                        imageFile = file;
                        image.setImageURI(data.getData());

                        Glide.with(this)
                                .load(data.getData())
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3)))
                                .into(imageBack);
                    }
                    break;
                case 101:
                    Bullet bulletObj = new Gson().fromJson(data.getStringExtra("bullet"), Bullet.class);
                    int position = Integer.parseInt(data.getStringExtra("position"));
                    String delete = data.getStringExtra("delete");
                    if (!TextUtils.isEmpty(delete) && delete.equalsIgnoreCase("0")) {
                        if (addBulletsAdapter != null && bullets != null && bullets.size() > 0 && position < bullets.size() && bulletObj != null) {
                            bullets.get(position).setImage(bulletObj.getImage());
                            bullets.get(position).setData(bulletObj.getData());
                            addBulletsAdapter.notifyDataSetChanged();
                        }
                    } else {
                        bullets.remove(position);
                        addBulletsAdapter.notifyDataSetChanged();
                    }
                    break;
                case 343:
                    finish();
                    break;
                case PERMISSION_REQUEST_IMAGE:
                    selectList = PictureSelector.obtainMultipleResult(data);

                    if (presenter != null && selectList != null && selectList.size() > 0 && selectList.get(0) != null) {
                        showIndefiniteProgressDialog();
                        LocalMedia localMedia = selectList.get(0);

                        File file;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            file = new File(localMedia.getAndroidQToPath());
                        } else {
                            file = new File(localMedia.getPath());
                        }


                        PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<List<File>>() {

                            @Override
                            public List<File> doInBackground() {
                                File modifiedFile;
                                try {
                                    modifiedFile = Utils.getFileFromBitmap(PostArticleActivity.this, "imageProfile", Utils.modifyOrientation(file.getAbsolutePath()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    modifiedFile = file;
                                }

                                return Collections.singletonList(modifiedFile);
                            }

                            @Override
                            public void onSuccess(List<File> files) {
                                dismissIndefiniteProgressDialog();
                                loadSelectedImage(files.get(0).getPath());
                            }
                        });
                    }

//                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
//                    if (presenter != null && selectList != null && selectList.size() > 0 && selectList.get(0) != null) {
//                        Log.e("jansdj", "+++++++++++++++++++++++++++++++++");
//                        Log.e("jansdj", "1 : " + selectList.get(0).getSize());
//                        Log.e("jansdj", "2 : " + selectList.get(0).getMimeType());
////                                presenter.validateMedia(selectList.get(0).getSize(), 0, selectList.get(0).getMimeType(), "image");
//                        localMedia = selectList.get(0);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                            loadSelectedImage(selectList.get(0).getAndroidQToPath());
//                        } else {
//                            loadSelectedImage(selectList.get(0).getPath());
//                        }
//                    }
                    break;
                case PERMISSION_REQUEST_VIDEO:
                    List<LocalMedia> selectList2 = PictureSelector.obtainMultipleResult(data);
                    if (presenter != null && selectList2 != null && selectList2.size() > 0 && selectList2.get(0) != null) {
                        Log.e("jansdj", "+++++++++++++++++++++++++++++++++");
                        Log.e("jansdj", " 1: " + selectList2.get(0).getSize());
                        Log.e("jansdj", " 2: " + selectList2.get(0).getDuration());
                        Log.e("jansdj", " 3: " + selectList2.get(0).getMimeType());
//                                if (postType == POST_TYPE.VIDEO_ARTICLE) {
//                                    presenter.validateMedia(selectList.get(0).getSize(), selectList.get(0).getDuration(), selectList.get(0).getMimeType(), "video");
//                                } else if (postType == POST_TYPE.REELS) {
//                                    presenter.validateMedia(selectList.get(0).getSize(), selectList.get(0).getDuration(), selectList.get(0).getMimeType(), "reels");
//                                }
                        localMedia = selectList2.get(0);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            loadSelectedVideo(selectList2.get(0).getAndroidQToPath());
                        } else {
                            loadSelectedVideo(selectList2.get(0).getPath());
                        }
                    }
                    break;
                case 232:
                    setChannel();
                    break;
            }
        }
        new Handler().postDelayed(() -> {
            Utils.hideKeyboard(PostArticleActivity.this, root);
            checkParams();
        }, 200);
    }

    private void setChannel() {
        if (prefConfig != null) {
            SelectedChannel selectedChannel = prefConfig.selectedChannel();
            if (selectedChannel != null) {
                if (!TextUtils.isEmpty(selectedChannel.getName())) {
                    name.setText(getResources().getString(R.string.post_to) + " " + selectedChannel.getName());
                }
                if (!TextUtils.isEmpty(selectedChannel.getImage())) {
                    Glide.with(this)
                            .load(selectedChannel.getImage())
                            .into(profile_image);
                }
                if (postArticleParams != null) {
                    postArticleParams.setSource(selectedChannel.getId());
                }
            }
        }
    }

    private void loadSelectedImage(String path) {
        checkParams();
        if (!TextUtils.isEmpty(path)) {
            isUploadMediaApiSuccess = false;
            isValidateApiSuccess = false;
            file = new File(path);
            setImagesFromFile(file);
//            if (presenter != null) {
//                presenter.uploadImageVideo(new File(path), "images");
//            }
        }
    }

    private void loadSelectedVideo(String path) {
        checkParams();
        if (!TextUtils.isEmpty(path)) {
            isUploadMediaApiSuccess = false;
            isValidateApiSuccess = false;
            isVideoChanged = true;
            uri = Uri.parse(path);
            if (uri != null) {
                initializePlayer(uri.toString());
            }
//            if (presenter != null) {
//                presenter.uploadImageVideo(new File(path), "videos");
//            }
        }
    }

    private void bindView() {
        post_to_profile = findViewById(R.id.post_to_profile);
        flow_layout = findViewById(R.id.flow_layout);
        places_layout = findViewById(R.id.places_layout);
        language_layout = findViewById(R.id.language_layout);
        tag = findViewById(R.id.tag);
        place = findViewById(R.id.places);
        language = findViewById(R.id.language);
        sourceLink = findViewById(R.id.sourceLink);
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        replace_image = findViewById(R.id.replace_image);
        llSchedule = findViewById(R.id.schedule);
        next_btn = findViewById(R.id.next_btn);
        bulletSection = findViewById(R.id.bulletSection);
        from_label = findViewById(R.id.from_label);
        youtube_view = findViewById(R.id.youtube_view);
        video_player = findViewById(R.id.video_player);
        headerTxt = findViewById(R.id.headerTxt);
        counter = findViewById(R.id.counter);
        next_btn_text = findViewById(R.id.next_btn_text);
        name = findViewById(R.id.name);
        profile_image = findViewById(R.id.profile_image);
        heading = findViewById(R.id.heading);
        add_bullet = findViewById(R.id.add_bullet);
        imageBack = findViewById(R.id.imageBack);
        rvBullets = findViewById(R.id.rvBullets);
        progress = findViewById(R.id.progressLayout);
        root = findViewById(R.id.root);
        image = findViewById(R.id.image);
        back = findViewById(R.id.back);
        card = findViewById(R.id.card);
        headline = findViewById(R.id.headline);
        reference = findViewById(R.id.reference);
        tvSchedule = findViewById(R.id.tvSchedule);
        ivScheduleArrow = findViewById(R.id.ivScheduleArrow);
        playButton = findViewById(R.id.icon_video_play);
        ll_placeholder = findViewById(R.id.ll_placeholder);
    }

    public boolean checkPermissions(String... permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /*METHOD TO GET REAL IMAGE PATH FROM URI*/
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            String path = cursor.getString(idx);
            cursor.close();
            return path;
        }
        return "";
    }

    @Override
    public void loaderShow(boolean flag) {
        showLoadingView(flag);
        if (flag) {
//            progress.setVisibility(View.VISIBLE);
        } else {
//            progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void error(String error) {
        showLoadingView(false);
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    private void loadTags() {
        if (tagItemArrayList != null && tagItemArrayList.size() > 0) {
            flow_layout.removeAllViews();
            for (TagItem tagItem : tagItemArrayList) {
                flow_layout.addView(createTagItem(tagItem.getName(), tagItem.getId()));
            }
        }
    }

    private void loadLocations() {
        if (locationItemArrayList != null && locationItemArrayList.size() > 0) {
            places_layout.removeAllViews();
            for (Location tagItem : locationItemArrayList) {
                places_layout.addView(createTagItem(tagItem.getNameToShow(), tagItem.getId()));
            }
        }
    }

    @Override
    public void success(Object response) {
        if (response instanceof CurrentTags) {
            CurrentTags tagsResponse = (CurrentTags) response;
            if (tagsResponse != null) {
                tagItemArrayList.clear();
                tagItemArrayList.addAll(tagsResponse.getTopics());
                loadTags();
            }
        } else if (response instanceof CurrentLocations) {
            CurrentLocations locations = (CurrentLocations) response;
            if (locations != null) {
                locationItemArrayList.clear();
                locationItemArrayList.addAll(locations.getLocations());
                loadLocations();
            }
        } else {
            showLoadingView(false);
            headline.setText("");
            reference.setText("");
            image.setImageURI(null);
            Utils.showSnacky(getWindow().getDecorView().getRootView(), getString(R.string.posted_successfully));
            finish();
        }
    }

    @Override
    public void successDelete() {
        Toast.makeText(this, "" + getString(R.string.article_deleted), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void createSuccess(Article article, String type) {
        this.article = article;
        switch (mode) {
            case EDIT:
                if (article != null && !TextUtils.isEmpty(article.getStatus()) && article.getStatus().equalsIgnoreCase(Constants.ARTICLE_DRAFT)) {
                    if (postArticleParams != null) {
                        postArticleParams.setId(article.getId());
                    }
                    sentArticleForReview(article);
                } else {
                    Utils.broadcastIntent(this, "update", ACTION_UPDATE_EVENT);
                    finish();
                }
                break;
            case ADD:
                if (article != null) {
                    //If user come back to this view then the Article ID already set to the params object,
                    //So it will update the draft article not creating new article!
                    if (postArticleParams != null) {
                        postArticleParams.setId(article.getId());
                    }


                    String id = article.getId();
                    String tempType = postArticleParams.getType() != null ? postArticleParams.getType() : "";
                    String tempSource = postArticleParams.getSource() != null ? postArticleParams.getSource() : "";
                    VideoInfo tempVideoInfo = videoInfo;

                    insertTaskAndUpload(id, tempType, tempSource, tempVideoInfo);

                    sentArticleForReview(article);
                }
                break;
        }
    }

    private void insertTaskAndUpload(String id, String tempType, String tempSource, VideoInfo tempVideoInfo) {
        if (
                !TextUtils.isEmpty(tempType) &&
                        (tempType.equals("reels") ||
                                tempType.equals("video"))
        ) {

            UploadInfo uploadInfo = new UploadInfo(
                    id,
                    tempType,
                    tempSource,
                    tempVideoInfo,
                    VideoStatus.PROCESSING
            );
            dbHandler.insertTask(uploadInfo);

            if (!Utils.isMyServiceRunning(PostArticleActivity.this, VideoProcessorService.class)) {
                Intent intent = new Intent(this, VideoProcessorService.class);
                intent.putExtra("uploadInfo", uploadInfo);
                startService(intent);
            }
        }
    }

    private void sentArticleForReview(Article article) {
        Intent intent = new Intent(PostArticleActivity.this, PostArticleReviewActivity.class);
        article.setSelected(true);
        Source source = new Source();
        if (prefConfig != null && prefConfig.isUserObject() != null) {
            if (!TextUtils.isEmpty(prefConfig.isUserObject().getProfile_image())) {
                source.setImage(prefConfig.isUserObject().getProfile_image());
                source.setIcon(prefConfig.isUserObject().getProfile_image());
            }
            source.setName(prefConfig.isUserObject().getFirst_name() + " " + prefConfig.isUserObject().getLast_name());
        }
        article.setSource(source);
        if (videoInfo != null) {
            if (TextUtils.isEmpty(article.getLink()))
                article.setLink(videoInfo.getPath());
            intent.putExtra("video_info", videoInfo);
            article.setVideoInfo(videoInfo);
        }
        intent.putExtra("article", new Gson().toJson(article));
        intent.putExtra("params", new Gson().toJson(postArticleParams));

        intent.putExtra("MODE", mode);
        intent.putExtra("POST_TYPE", postType);
        startActivityForResult(intent, 343);
    }

    @Override
    public void createSuccess(ReelsItem reels, String type) {
        switch (mode) {
            case EDIT:
                Utils.broadcastIntent(this, "update", ACTION_UPDATE_EVENT);
                finish();
                break;
            case ADD:
                if (postArticleParams != null) {
                    postArticleParams.setId(reels.getId());
                }

                String id = reels.getId();
                String tempType = postArticleParams.getType() != null ? postArticleParams.getType() : "";
                String tempSource = postArticleParams.getSource() != null ? postArticleParams.getSource() : "";
                VideoInfo tempVideoInfo = videoInfo;

                insertTaskAndUpload(id, tempType, tempSource, tempVideoInfo);

                sentArticleForReview(getArticleFromReels(reels));
                break;
        }
    }

    private Article getArticleFromReels(ReelsItem reels) {
        Article article = new Article();
        if (reels != null) {
            article.setId(reels.getId());
            article.setTitle(reels.getDescription());
            article.setLink(reels.getMedia());
            article.setPublishTime(reels.getPublishTime());
            article.setType("REEL");
            MediaMeta mediaMeta = new MediaMeta();
            if (reels.getMediaMeta() != null) {
                mediaMeta.setDuration(reels.getMediaMeta().getDuration());
                mediaMeta.setHeight(reels.getMediaMeta().getHeight());
                mediaMeta.setWidth(reels.getMediaMeta().getWidth());
            }
            article.setMediaMeta(mediaMeta);
            ArrayList<Bullet> bullets = new ArrayList<>();
            Bullet bullet = new Bullet();
            bullet.setImage(reels.getImage());
            bullet.setData(reels.getDescription());
            bullets.add(bullet);
            article.setBullets(bullets);
            article.setSelected(true);
        }
        return article;
    }

    @Override
    public void uploadSuccess(String url, String type) {
        isUploadMediaApiSuccess = true;
        imageUrl = url;
        boolean isReel = false;
        if (postArticleParams != null) {
            switch (postType) {
                case ARTICLE:
                    postArticleParams.setType("image");
                    postArticleParams.setImage(url);
                    isReel = false;
                    break;
                case VIDEO_ARTICLE:
                    postArticleParams.setType("video");
                    postArticleParams.setVideo(url);
                    isReel = false;
                    break;
                case YOUTUBE:
                    postArticleParams.setType("youtube");
                    postArticleParams.setVideo(url);
                    isReel = false;
                    break;
                case REELS:
                    postArticleParams.setType("reels");
                    postArticleParams.setStatus("DRAFT");
                    postArticleParams.setMedia(url);
                    isReel = true;
                    break;
                default:
            }
            switch (mode) {
                case EDIT:
                    if (!TextUtils.isEmpty(type) && article != null) {
                        if (type.equalsIgnoreCase("images")) {
                            article.setImage(url);
                        } else if (type.equalsIgnoreCase("videos")) {
                            isVideoChanged = true;
                            article.setLink(url);
                        }
                    }
                    showLoadingView(false);
//                    progress.setVisibility(View.GONE);
                    setParams();
                    break;
                case ADD:
                    presenter.createArticle(postArticleParams, isReel);
                    break;
            }
        }
    }

    @Override
    public void proceedToUpload() {
        isValidateApiSuccess = true;
        setParams();
    }

    @Override
    public void onProgressUpdate(int percentage) {
        if (mLoadingDialog != null)
            mLoadingDialog.updateProgress(percentage);
    }

    @Override
    public void onItemClick(int position, History item) {

    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(PostArticleActivity.this, AddBulletActivity.class);
        if (bullets != null && position < bullets.size()) {
            intent.putExtra("bullet", new Gson().toJson(bullets.get(position)));
        }
        intent.putExtra("mode", mode);
        intent.putExtra("position", "" + position);
        intent.putExtra("size", bullets.size());
        startActivityForResult(intent, 101);
    }

    @Override
    public void onItemCancelClick(int position, History item) {

    }

    private void initializePlayer(String videoLink) {
//        if (videoLink.endsWith(".m3u8")) {
//            //for HLS
//            TrackSelection.Factory adaptiveTrackSelection = new AdaptiveTrackSelection.Factory();
//            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this,
//                    new DefaultTrackSelector(adaptiveTrackSelection));
//
//            DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
//            com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
//                    Util.getUserAgent(this, "Exo2"), defaultBandwidthMeter);
//
////            String hls_url = "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8";
//            Uri uri = Uri.parse(videoLink);
//            Handler mainHandler = new Handler();
//            MediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
//
//            simpleExoPlayer.prepare(mediaSource);
//        } else {
//            TrackSelector trackSelectorDef = new DefaultTrackSelector();
//            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(PostArticleActivity.this, trackSelectorDef);
//            String userAgent = getString(R.string.app_name);
//            DefaultDataSourceFactory defDataSourceFactory = new DefaultDataSourceFactory(PostArticleActivity.this, userAgent);
//            Uri uriOfContentUrl = Uri.parse(videoLink);
//            MediaSource mediaSource = new ProgressiveMediaSource.Factory(defDataSourceFactory).createMediaSource(uriOfContentUrl);
//            simpleExoPlayer.prepare(mediaSource);
//        }
//        video_player.setPlayer(simpleExoPlayer);
    }

    private void initYouTube(String url) {
        getLifecycle().addObserver(youtube_view);
        youtube_view.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(url, 0);
                youTubePlayer.pause();
            }
        });
    }

    private void pausePlayer(SimpleExoPlayer player) {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
    }

    private void playPlayer(SimpleExoPlayer player) {
        if (player != null) {
            player.setPlayWhenReady(true);
        }
    }

    private void stopPlayer() {
        video_player.setPlayer(null);
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }

    private void seekTo(SimpleExoPlayer player, long positionInMS) {
        if (player != null) {
            player.seekTo(positionInMS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlayer();
    }

    private void showLoadingView(boolean isShow) {
        if (isFinishing()) {
            return;
        }
        if (isShow) {
            if (mLoadingDialog == null) {
                mLoadingDialog = new LoadingProgressDialog(this);
            }
//            if (mLoadingDialog.isShowing()) {
//                mLoadingDialog.dismiss();
//            }
//            mLoadingDialog.updateProgress(0);
            mLoadingDialog.show();
        } else {
            if (mLoadingDialog != null
                    && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        }
    }


    /**
     * loading dialog
     */
    protected void showIndefiniteProgressDialog() {
        try {
            if (mIndefiniteLoadingDialog == null) {
                mIndefiniteLoadingDialog = new PictureLoadingDialog(this);
            }
            if (mIndefiniteLoadingDialog.isShowing()) {
                mIndefiniteLoadingDialog.dismiss();
            }
            mIndefiniteLoadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * dismiss dialog
     */
    protected void dismissIndefiniteProgressDialog() {
        try {
            if (mIndefiniteLoadingDialog != null
                    && mIndefiniteLoadingDialog.isShowing()) {
                mIndefiniteLoadingDialog.dismiss();
            }
        } catch (Exception e) {
            mIndefiniteLoadingDialog = null;
            e.printStackTrace();
        }
    }


}
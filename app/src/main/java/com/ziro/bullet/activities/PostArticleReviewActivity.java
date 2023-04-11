package com.ziro.bullet.activities;

import static com.ziro.bullet.activities.AddPostPlaceActivity.LOCATION_RESULT_KEY;
import static com.ziro.bullet.activities.LanguageActivity.LANGUAGE_RESULT_KEY;
import static com.ziro.bullet.utills.Constants.ACTION_UPDATE_EVENT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.nex3z.flowlayout.FlowLayout;
import com.ziro.bullet.CacheData.DbHandler;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.feed.FeedAdapter;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
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
import com.ziro.bullet.interfaces.AdFailedListener;
import com.ziro.bullet.interfaces.AudioCallback;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.LanguageInterface;
import com.ziro.bullet.interfaces.PostArticleCallback;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.model.AudioObject;
import com.ziro.bullet.model.language.LanguageResponse;
import com.ziro.bullet.model.language.LanguagesItem;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.model.SelectedChannel;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.presenter.LanguagePresenter;
import com.ziro.bullet.presenter.PostArticlePresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.SpeedyLinearLayoutManager;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import im.ene.toro.PlayerSelector;
import im.ene.toro.widget.Container;

public class PostArticleReviewActivity extends BaseActivity implements PostArticleCallback {

    private ConstraintLayout post_article_btn;
    private RelativeLayout back;
    private RelativeLayout progress;
    private RelativeLayout tag, places, language;
    private TextView headerTxt;
    private TextView post_article_btn_text;
    private TextView nameTv;
    private RelativeLayout post_to_profile;
    private CircleImageView profile_image;
    private Container card;
    private PrefConfig prefConfig;
    private POST_TYPE postType;
    private MODE mode;
    private PostArticlePresenter presenter;
    private PostArticleParams params;
    private FlowLayout flow_layout, places_layout, language_layout;
    private VideoInfo videoInfo = null;
    private PlayerSelector selector = PlayerSelector.DEFAULT;
    private FeedAdapter mCardAdapter;
    private DbHandler dbHandler;
    private LanguagePresenter languagePresenter;
    private List<LanguagesItem> languagesArray = new ArrayList<>();
    private LanguagesItem languagesItem;
    private String articleLanguageCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setStatusBarColor(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_post_article_review);
        bindView();
        init();
        setData();
        setListener();
    }

    private void setListener() {
        back.setOnClickListener(v -> finish());
        post_article_btn.setOnClickListener(v -> {
            if (params != null && presenter != null) {
                post_article_btn.setEnabled(false);
                if (params.getType().equalsIgnoreCase("video") ||
                        params.getType().equalsIgnoreCase("reels")) {

                    dbHandler.setTaskReadyToPublish(
                            params.getId(),
                            VideoStatus.READY_TO_PUBLISH_YES
                    );

                    if (Utils.isMyServiceRunning(PostArticleReviewActivity.this, VideoProcessorService.class)) {
                        Log.d("review", "service running: ");
                        Intent intent = new Intent();
                        intent.putExtra("task", "done");
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Log.d("review", "service not running: ");
                        String source = TextUtils.isEmpty(params.getSource()) ? "" : params.getSource();

//                        UploadInfo uploadInfo = new UploadInfo(params.getId(), params.getType(), source, videoInfo, VideoStatus.PROCESSING);

                        Intent intent = new Intent(this, VideoProcessorService.class);
//                        intent.putExtra("upload_info", uploadInfo);
                        startService(intent);

                        Intent intentDummy = new Intent();
                        intentDummy.putExtra("task", "done");
                        setResult(RESULT_OK, intentDummy);
                        finish();
                    }
                } else {
                    if (postType.equals(POST_TYPE.YOUTUBE)) {
                        Map<String, String> params = new HashMap<>();
                        AnalyticsEvents.INSTANCE.logEvent(PostArticleReviewActivity.this,
                                params,
                                Events.UPLOAD_YOUTUBE_LINK_DONE);
                    }
                    presenter.publishArticle(params.getId(), "PUBLISHED");
                }
            }
        });
        tag.setOnClickListener(v -> {
            if (params != null) {
                Intent intent = new Intent(PostArticleReviewActivity.this, AddTagActivity.class);
                intent.putExtra(AddTagActivity.ARTICLE_ID_KEY, params.getId());
                startActivityForResult(intent, 1);
            }
        });
        places.setOnClickListener(v -> {
            if (params != null) {
                Intent intent = new Intent(PostArticleReviewActivity.this, AddPostPlaceActivity.class);
                intent.putExtra(AddTagActivity.ARTICLE_ID_KEY, params.getId());
                startActivityForResult(intent, 3);
            }
        });
        language.setOnClickListener(v -> {
            if (params != null) {
                Intent intent = new Intent(PostArticleReviewActivity.this, LanguageActivity.class);
                intent.putExtra(LanguageActivity.ARTICLE_ID_KEY, params.getId());
                intent.putExtra("isPostLanguageSelection", true);
                intent.putExtra("languagesItemSelected", languagesItem);
                startActivityForResult(intent, 4);
            }
        });
    }

    private void init() {
        presenter = new PostArticlePresenter(this, this);
        prefConfig = new PrefConfig(this);
        dbHandler = new DbHandler(this);
        languagePresenter = new LanguagePresenter(this, new LanguageInterface() {
            @Override
            public void languageResult(int size) {

            }

            @Override
            public void loaderShow(boolean flag) {

            }

            @Override
            public void error(String error) {

            }

            @Override
            public void success(Object response) {
                if (response instanceof LanguageResponse) {
                    LanguageResponse languageResponse = (LanguageResponse) response;
                    if (languageResponse != null) {
                        languagesArray.clear();
                        languagesArray.addAll(languageResponse.getLanguages());
                        if (articleLanguageCode != null) {
                            setLanguageItemFromCode(articleLanguageCode);
                        }
                        if (languagesItem != null && params != null) {
                            language_layout.removeAllViews();
                            language_layout.addView(createTagItem(languagesItem.getName(), params.getId()));
                        }
                    }
                }
            }

//            @Override
//            public void successLanguageSelection(String id, int position, String code) {
//
//            }
        });
    }

    private void setLanguageItemFromCode(String languageCode) {
        if (languagesArray != null && languagesArray.size() > 0) {
            for (int i = 0; i < languagesArray.size(); i++) {
                if (languagesArray.get(i).getCode().equals(languageCode)) {
                    languagesItem = languagesArray.get(i);
                }
            }
        }
    }

    private void bindView() {
        flow_layout = findViewById(R.id.flow_layout);
        places_layout = findViewById(R.id.places_layout);
        language_layout = findViewById(R.id.language_layout);
        tag = findViewById(R.id.tag);
        language = findViewById(R.id.language);
        places = findViewById(R.id.places);
        progress = findViewById(R.id.progress);
        post_to_profile = findViewById(R.id.post_to_profile);
        card = findViewById(R.id.card);
        post_article_btn_text = findViewById(R.id.post_article_btn_text);
        nameTv = findViewById(R.id.nameTv);
        post_article_btn = findViewById(R.id.post_article_btn);
        headerTxt = findViewById(R.id.headerTxt);
        profile_image = findViewById(R.id.profile_image);
        back = findViewById(R.id.back);
        card.setPlayerSelector(selector);
        flow_layout.setRtl(Utils.isRTL());
        language_layout.setRtl(Utils.isRTL());
        places_layout.setRtl(Utils.isRTL());
    }


    private void setData() {
        languagePresenter.getLanguages("");
        if (prefConfig != null && prefConfig.isUserObject() != null) {
            if (!TextUtils.isEmpty(prefConfig.isUserObject().getProfile_image())) {
                Glide.with(this)
                        .load(prefConfig.isUserObject().getProfile_image())
                        .into(profile_image);
            }
        }

        if (getIntent() != null) {
            postType = (POST_TYPE) getIntent().getSerializableExtra("POST_TYPE");
            mode = (MODE) getIntent().getSerializableExtra("MODE");

            switch (mode) {
                case ADD:
                    post_article_btn_text.setText(getString(R.string.post));
                    switch (postType) {
                        case REELS:
                            headerTxt.setText(getString(R.string.post_newsreels));
                            break;
                        default:
                            headerTxt.setText(getString(R.string.post_article_));
                    }
                    break;
                case EDIT:
                    headerTxt.setText(getString(R.string.edit_article));
                    post_article_btn_text.setText(getString(R.string.save));
                    break;
                case DELETE:
                    headerTxt.setText(getString(R.string.delete_article));
                    post_article_btn_text.setText(getString(R.string.delete));
                    break;
                default:
            }

            if (getIntent().hasExtra("params")) {
                params = new Gson().fromJson(getIntent().getStringExtra("params"), PostArticleParams.class);
//                if (params != null) {
//                    params.setSource(prefConfig.isUserObject().getId());
//                }
            }

            if (getIntent().hasExtra("video_info")) {
                videoInfo = getIntent().getExtras().getParcelable("video_info");
            }
            if (getIntent().hasExtra("article")) {
                Article article = new Gson().fromJson(getIntent().getStringExtra("article"), Article.class);
                if (article != null) {
                    if (presenter != null) {
                        presenter.getTagList(article.getId());
                        presenter.getLocationList(article.getId());
                    }
                    if (!TextUtils.isEmpty(article.getStatus()) && article.getStatus().equalsIgnoreCase(Constants.ARTICLE_DRAFT)) {
                        headerTxt.setText(getString(R.string.post_article_));
                        post_article_btn_text.setText(getString(R.string.post));
                    }

                    if (!TextUtils.isEmpty(article.getLanguageCode())) {
                        articleLanguageCode = article.getLanguageCode();
                        if (articleLanguageCode != null) {
                            setLanguageItemFromCode(articleLanguageCode);
                        }
                        language_layout.removeAllViews();
                        if (languagesItem != null) {
                            language_layout.addView(createTagItem(languagesItem.getName(), article.getId()));
                        } else {
                            language_layout.addView(createTagItem(article.getLanguageCode(), article.getId()));
                        }
                    }

                    ArrayList<Article> contentArrayList = new ArrayList<>();
                    switch (postType) {
                        case ARTICLE:
                            article.setType("EXTENDED");
                            contentArrayList.add(article);
                            break;
                        case VIDEO_ARTICLE:
                            article.setType("VIDEO");
                            contentArrayList.add(article);
                            break;
                        case YOUTUBE:
                            article.setType("YOUTUBE");
                            contentArrayList.add(article);
                            break;
                        case REELS:
                            article.setType("REEL");
                            contentArrayList.add(article);
                            break;
                        default:
                    }
                    mCardAdapter = new FeedAdapter(null, true, this, contentArrayList, null, false, new DetailsActivityInterface() {
                        @Override
                        public void playAudio(AudioCallback audioCallback, String fragTag, AudioObject audio) {

                        }

                        @Override
                        public void pause() {

                        }

                        @Override
                        public void resume() {

                        }
                    }, null, null, null, null, null,
                            new ShowOptionsLoaderCallback() {
                                @Override
                                public void showLoader(boolean show) {

                                }
                            },
                            new AdFailedListener() {
                                @Override
                                public void onAdFailed() {

                                }
                            }, getLifecycle()
                    );

                    LinearLayoutManager manager = new SpeedyLinearLayoutManager(this);
                    card.setLayoutManager(manager);
                    card.setAdapter(mCardAdapter);
                }
            }
        }
        setChannelForArticle();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data != null) {
                ArrayList<TagItem> tagItemArrayList = (ArrayList<TagItem>) data.getSerializableExtra(AddTagActivity.TAG_RESULT_KEY);
                if (tagItemArrayList != null && tagItemArrayList.size() > 0) {
                    flow_layout.removeAllViews();
                    for (TagItem tagItem : tagItemArrayList) {
                        flow_layout.addView(createTagItem(tagItem.getName(), tagItem.getId()));
                    }
                }
            }
        } else if (requestCode == 2) {
            setChannelForArticle();
        } else if (requestCode == 3) {
            if (data != null) {
                ArrayList<Location> locationArrayListSelected = (ArrayList<Location>) data.getSerializableExtra(LOCATION_RESULT_KEY);
                if (locationArrayListSelected != null && locationArrayListSelected.size() > 0) {
                    places_layout.removeAllViews();
                    for (Location tagItem : locationArrayListSelected) {
                        places_layout.addView(createTagItem(tagItem.getNameToShow(), tagItem.getId()));
                    }
                }
            }
        } else if (requestCode == 4) {
            if (data != null) {
                languagesItem = (LanguagesItem) data.getSerializableExtra(LANGUAGE_RESULT_KEY);
                if (languagesItem != null && !TextUtils.isEmpty(languagesItem.getName())) {
                    language_layout.removeAllViews();
                    language_layout.addView(createTagItem(languagesItem.getName(), languagesItem.getId()));
                }
            }
        }
    }

    private void setChannelForArticle() {
        if (prefConfig != null && params != null) {
            SelectedChannel selectedChannel = prefConfig.selectedChannel();
            if (selectedChannel != null) {
                if (!TextUtils.isEmpty(selectedChannel.getName())) {
                    nameTv.setText(getResources().getString(R.string.post_to) + " " + selectedChannel.getName());
                }
                if (!TextUtils.isEmpty(selectedChannel.getImage())) {
                    Glide.with(this)
                            .load(selectedChannel.getImage())
                            .into(profile_image);
                }
                params.setSource(selectedChannel.getId());
                if (mCardAdapter != null)
                    mCardAdapter.notifyDataSetChanged();
            }
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
    public void loaderShow(boolean flag) {
        if (flag) {
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void error(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void success(Object responseBody) {
        if (responseBody instanceof CurrentTags) {
            CurrentTags tagsResponse = (CurrentTags) responseBody;
            if (tagsResponse != null && tagsResponse.getTopics() != null && tagsResponse.getTopics().size() > 0) {
                flow_layout.removeAllViews();
                for (TagItem tagItem : tagsResponse.getTopics()) {
                    flow_layout.addView(createTagItem(tagItem.getName(), tagItem.getId()));
                }
            }
        } else if (responseBody instanceof CurrentLocations) {
            CurrentLocations locations = (CurrentLocations) responseBody;
            if (locations != null && locations.getLocations() != null && locations.getLocations().size() > 0) {
                places_layout.removeAllViews();
                for (Location tagItem : locations.getLocations()) {
                    places_layout.addView(createTagItem(tagItem.getNameToShow(), tagItem.getId()));
                }
            }
        } else {
            Utils.broadcastIntent(PostArticleReviewActivity.this, "update", ACTION_UPDATE_EVENT);
//            if (prefConfig != null) {
//                prefConfig.setChannel(null);
//            }
            Utils.showSnacky(getWindow().getDecorView().getRootView(), getString(R.string.posted_successfully), new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    Intent intent = new Intent();
                    intent.putExtra("review", "review");
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });


        }
    }

    @Override
    public void successDelete() {

    }

    @Override
    public void createSuccess(Article responseBody, String type) {

    }

    @Override
    public void createSuccess(ReelsItem responseBody, String type) {

    }

    @Override
    public void uploadSuccess(String url, String type) {

    }

    @Override
    public void proceedToUpload() {

    }

    @Override
    public void onProgressUpdate(int percentage) {

    }
}
package com.ziro.bullet.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nex3z.flowlayout.FlowLayout;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.postarticle.SelectTagAdapter;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.postarticle.CurrentTags;
import com.ziro.bullet.data.models.postarticle.TagItem;
import com.ziro.bullet.data.models.postarticle.TagsReplace;
import com.ziro.bullet.data.models.postarticle.TagsResponse;
import com.ziro.bullet.interfaces.PostArticleCallback;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.presenter.PostArticlePresenter;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;

public class AddTagActivity extends BaseActivity {

    public static final String TAG_RESULT_KEY = "resultTags";
    public static final String ARTICLE_ID_KEY = "articleIdKey";
    public static final String SEARCH_EDIT_TAG = "editTextTag";

    private RelativeLayout ivBack;
    private RecyclerView tagRecyclerView;
    private FlowLayout flowLayout;
    private LinearLayout loadingView;
    // private TextView saveTag;

    private ArrayList<TagItem> tagItemArrayList;
    private ArrayList<TagItem> tagItemArrayListSelected;
    private SelectTagAdapter selectTagAdapter;
    private ArrayList<String> tempTagIds;

    private PostArticlePresenter postArticlePresenter;
    private PrefConfig prefConfig;
    private String articleId;

    private String currentlyEditingText;

    private final PostArticleCallback postArticleCallback = new PostArticleCallback() {
        @Override
        public void loaderShow(boolean flag) {
            loadingView.setVisibility(flag ? View.VISIBLE : View.GONE);
        }

        @Override
        public void error(String error) {

        }

        @Override
        public void success(Object response) {
            if (response instanceof TagsResponse) {
                TagsResponse tagsResponse = (TagsResponse) response;
                tagItemArrayList.clear();
                tagItemArrayList.addAll(tagsResponse.getTopics());
                markSelected();
                selectTagAdapter.notifyDataSetChanged();
            } else if (response instanceof TagsReplace) {
                TagsReplace tagsReplace = (TagsReplace) response;
                TagItem tagItemNew = tagsReplace.getTagItem();
                if (!tagsReplace.getTempId().equals("")) {
                    int i = 0;
                    int pos = -1;
                    for (TagItem tagItem : tagItemArrayListSelected) {
                        if (tagItem.getId().equals(tagsReplace.getTempId())) {
                            pos = i;
                        }
                        i++;
                    }
                    if (pos >= 0) {
                        tagItemArrayListSelected.get(pos).setId(tagItemNew.getId());
                        tagItemArrayListSelected.get(pos).setIcon(tagItemNew.getIcon());
                        tagItemArrayListSelected.get(pos).setImage(tagItemNew.getImage());
                    }
                }
            } else if (response instanceof CurrentTags) {
                CurrentTags currentTags = (CurrentTags) response;
                tagItemArrayListSelected.clear();
                tagItemArrayListSelected.addAll(currentTags.getTopics());
                loadTags();
                markSelected();
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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);

        tempTagIds = new ArrayList<>();
        postArticlePresenter = new PostArticlePresenter(this, postArticleCallback);
        prefConfig = new PrefConfig(this);

        if (getIntent() != null) {
            articleId = getIntent().getStringExtra(ARTICLE_ID_KEY);
        } else {
            finish();
        }

        bindViews();
        setUpRecyclerView();
        setUpTags();

        postArticlePresenter.getTagList(articleId);
        postArticlePresenter.getSuggestedTags(articleId, "");

        listeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.checkAppModeColor(this, false);
    }

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra(TAG_RESULT_KEY, tagItemArrayListSelected);
        setResult(RESULT_OK, returnIntent);
        finish();

        // super.onBackPressed();
    }

    private void bindViews() {
        ivBack = findViewById(R.id.ivBack);
        tagRecyclerView = findViewById(R.id.tag_recycler_view);
        flowLayout = findViewById(R.id.flow_layout);
        loadingView = findViewById(R.id.loading_view);
        ImageView loader = findViewById(R.id.loader);
        // saveTag = findViewById(R.id.save_tag);

//        Glide.with(loader)
//                .load(Utils.getLoaderForTheme(prefConfig.getAppTheme()))
//                .into(loader);

        flowLayout.setRtl(Utils.isRTL());
    }

    private void setUpRecyclerView() {
        tagItemArrayList = new ArrayList<>();
        tagItemArrayListSelected = new ArrayList<>();

        selectTagAdapter = new SelectTagAdapter(this, tagItemArrayList, position -> {
            if (tagItemArrayList.size() > position && position >= 0) {
                tagRecyclerView.requestFocus();

                if (tagItemArrayList.get(position).isSelected() || checkTagLimit()) {
                    if (!tagItemArrayList.get(position).isSelected()) {
                        postArticlePresenter.addPostTags(articleId, tagItemArrayList.get(position).getName(), "");
                    } else {
                        postArticlePresenter.removePostTags(articleId, tagItemArrayList.get(position).getId());
                    }

                    updateSelectedList(tagItemArrayList.get(position), tagItemArrayList.get(position).isSelected());
                    tagItemArrayList.get(position).setSelected(!tagItemArrayList.get(position).isSelected());
                    selectTagAdapter.notifyItemChanged(position);

                    loadTags();
                }
//                else {
//                    //TODO: limit reached error
//                }
            }
        });

        tagRecyclerView.setHasFixedSize(true);
        tagRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tagRecyclerView.setAdapter(selectTagAdapter);
        tagRecyclerView.setItemAnimator(null);
    }

    private void updateSelectedList(TagItem tagItem, boolean remove) {
        int pos = -1;
        for (int i = 0; i < tagItemArrayListSelected.size(); i++) {
            // if (tagItemArrayListSelected.get(i).getId().equals(tagItem.getId())) {
            if (tagItemArrayListSelected.get(i).getName().equals(tagItem.getName())) {
                pos = i;
            }
        }

        if (pos >= 0 && remove) {
            tagItemArrayListSelected.remove(pos);
        } else if (pos == -1 && !remove) {
            tagItemArrayListSelected.add(tagItem);
        }
    }

    private void loadTags() {
        flowLayout.removeAllViews();

        // flowLayout.addView(makeTagIcon());
        for (TagItem tagItem : tagItemArrayListSelected) {
            // flowLayout.addView(makeTagTextView(tagItem.getName()));
            flowLayout.addView(createTagItem(tagItem.getName(), tagItem.getId()));
        }
        if (checkTagLimit()) {
            flowLayout.addView(makeTagEditText());

            if (!TextUtils.isEmpty(currentlyEditingText)) {
                EditText userNameText = (EditText) flowLayout.getChildAt(flowLayout.getChildCount() - 1);
                userNameText.setFocusable(true);
                userNameText.setFocusableInTouchMode(true);
                userNameText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(userNameText, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    private void setUpTags() {
        flowLayout.removeAllViews();

        //flowLayout.addView(makeTagIcon());
        flowLayout.addView(makeTagEditText());
    }

    private void listeners() {

        ivBack.setOnClickListener(v -> onBackPressed());
//        saveTag.setOnClickListener(v -> {
//            Intent returnIntent = new Intent();
//            returnIntent.putExtra(TAG_RESULT_KEY, tagItemArrayListSelected);
//            setResult(Activity.RESULT_OK, returnIntent);
//            finish();
//        });
    }

    private EditText makeTagEditText() {
        EditText editText = new EditText(this);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        editText.setLayoutParams(lp);

        editText.setHint(R.string.tag_hint);
        editText.setTag(SEARCH_EDIT_TAG);
        editText.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        editText.setTextDirection(View.TEXT_DIRECTION_LOCALE);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0 && s.toString().contains(",")) {
                    String newTag = s.toString().replace(",", "");
                    if (newTag.length() > 0) {
                        currentlyEditingText = null;
                        String tempId = addNewTag(newTag);
                        postArticlePresenter.addPostTags(articleId, newTag, tempId);
                        editText.setText(null);
                        postArticlePresenter.getSuggestedTags(articleId, "");
                        hideKeyboard(AddTagActivity.this);
                    } else {
                        editText.setText(null);
                        postArticlePresenter.getSuggestedTags(articleId, "");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    currentlyEditingText = s.toString();
                    postArticlePresenter.getSuggestedTags(articleId, s.toString());
                } else {
                    postArticlePresenter.getSuggestedTags(articleId, "");
                }
            }
        });

        if (!TextUtils.isEmpty(currentlyEditingText)) {
            editText.setText(currentlyEditingText);
        }

        return editText;
    }

    private View createTagItem(String tagStr, String id) {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View v = vi.inflate(R.layout.tag_layout, null);

        TextView tagText = v.findViewById(R.id.tag_text);
        ImageView removeTag = v.findViewById(R.id.remove_tag);
        removeTag.setVisibility(View.VISIBLE);
        tagText.setText(tagStr);
        if (!id.equals("")) {
            v.setTag(id);
        }
        tagText.setTextDirection(View.TEXT_DIRECTION_LOCALE);
        removeTag.setOnClickListener(view -> {
            if (!id.equals("")) {
                if (id.length() > 4) {
                    postArticlePresenter.removePostTags(articleId, id);
                }
                removeTag(id);
            }
            ((ViewGroup) v.getParent()).removeView(v);
            loadTags();
        });

        return v;
    }

    private void removeTag(String id) {
        int i = 0;
        int j = 0;
        int selectedPos = -1;
        int tagPos = -1;

        for (TagItem tagItem : tagItemArrayListSelected) {
            if (tagItem.getId().equals(id)) {
                selectedPos = i;
            }
            i++;
        }
        if (selectedPos >= 0) {
            tagItemArrayListSelected.remove(selectedPos);
        }

        for (TagItem tagItem : tagItemArrayList) {
            if (tagItem.getId().equals(id)) {
                tagPos = j;
            }
            j++;
        }
        if (tagPos >= 0) {
            tagItemArrayList.get(tagPos).setSelected(false);
        }
        selectTagAdapter.notifyDataSetChanged();

        listeners();
    }

    private boolean checkTagLimit() {
        return tagItemArrayListSelected.size() < 5;
    }

    private void markSelected() {
        for (int i = 0; i < tagItemArrayList.size(); i++) {
            for (int j = 0; j < tagItemArrayListSelected.size(); j++) {
                if (tagItemArrayListSelected.get(j).getName().equals(tagItemArrayList.get(i).getName())) {
                    tagItemArrayList.get(i).setSelected(true);
                }
            }
        }
        selectTagAdapter.notifyDataSetChanged();
    }

    private String addNewTag(String tag) {
        String tempId = generateTemTagId();

        TagItem tagItem = new TagItem();
        tagItem.setId(tempId);
        tagItem.setName(tag);
        tagItemArrayListSelected.add(tagItem);

        loadTags();

        return tempId;
    }

    private String generateTemTagId() {
        int min = 100;
        int max = 999;
        int randomInt = (int) Math.floor(Math.random() * (max - min + 1) + min);
        String tempTag = String.valueOf(randomInt);

        if (tempTagIds.contains(tempTag)) {
            return generateTemTagId();
        } else {
            tempTagIds.add(tempTag);
            return tempTag;
        }
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
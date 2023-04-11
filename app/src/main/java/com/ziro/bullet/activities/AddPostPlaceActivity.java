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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nex3z.flowlayout.FlowLayout;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.postarticle.SelectLocationAdapter;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.location.Location;
import com.ziro.bullet.data.models.postarticle.CurrentLocations;
import com.ziro.bullet.data.models.postarticle.LocationReplace;
import com.ziro.bullet.data.models.postarticle.LocationResponse;
import com.ziro.bullet.interfaces.PostArticleCallback;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.presenter.PostArticlePresenter;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;

public class AddPostPlaceActivity extends BaseActivity {

    public static final String LOCATION_RESULT_KEY = "resultLocations";
    public static final String ARTICLE_ID_KEY = "articleIdKey";
    public static final String SEARCH_EDIT_TAG = "editTextTag";

    private RelativeLayout ivBack;
    private RecyclerView locationRecyclerView;
    private FlowLayout flowLayout;
    private LinearLayout loadingView;
    // private TextView saveTag;

    private ArrayList<Location> locationArrayList;
    private ArrayList<Location> locationArrayListSelected;
    private SelectLocationAdapter selectLocationAdapter;
    private ArrayList<String> tempLocationIds;

    private PostArticlePresenter postArticlePresenter;
    private PrefConfig prefConfig;
    private String articleId;

    private String currentlyEditingText;

    private final PostArticleCallback postArticleCallback = new PostArticleCallback() {
        @Override
        public void loaderShow(boolean flag) {
            loadingView.setVisibility(flag?View.VISIBLE:View.GONE);
        }

        @Override
        public void error(String error) {

        }

        @Override
        public void success(Object response) {
            if (response instanceof LocationResponse) {
                LocationResponse locationResponse = (LocationResponse) response;
                locationArrayList.clear();
                locationArrayList.addAll(locationResponse.getLocations());
                markSelected();
                selectLocationAdapter.notifyDataSetChanged();
            } else if (response instanceof LocationReplace) {
                LocationReplace tagsReplace = (LocationReplace) response;
                Location tagItemNew = tagsReplace.getLocation();
                if(!tagsReplace.getTempId().equals("")) {
                    int i = 0;
                    int pos = -1;
                    for (Location tagItem : locationArrayListSelected) {
                        if (tagItem.getId().equals(tagsReplace.getTempId())) {
                            pos = i;
                        }
                        i++;
                    }
                    if (pos >= 0) {
                        locationArrayListSelected.get(pos).setId(tagItemNew.getId());
                        //tagItemArrayListSelected.get(pos).setImage(tagItemNew.getImage());
                        locationArrayListSelected.get(pos).setImage(tagItemNew.getImage());
                    }
                }
            } else if (response instanceof CurrentLocations) {
                CurrentLocations currentLocations = (CurrentLocations) response;
                locationArrayListSelected.clear();
                locationArrayListSelected.addAll(currentLocations.getLocations());
                loadLocations();
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
        setContentView(R.layout.activity_add_post_place);

        tempLocationIds = new ArrayList<>();
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

        postArticlePresenter.getLocationList(articleId);
        postArticlePresenter.getSuggestedLocation(articleId, "");

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
        returnIntent.putExtra(LOCATION_RESULT_KEY, locationArrayListSelected);
        setResult(RESULT_OK, returnIntent);
        finish();

        // super.onBackPressed();
    }

    private void bindViews() {
        ivBack = findViewById(R.id.ivBack);
        locationRecyclerView = findViewById(R.id.tag_recycler_view);
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
        locationArrayList = new ArrayList<>();
        locationArrayListSelected = new ArrayList<>();

        selectLocationAdapter = new SelectLocationAdapter(this, locationArrayList, position -> {
            if (locationArrayList.size() > position && position >= 0) {
                locationRecyclerView.requestFocus();

                if (locationArrayList.get(position).isSelected() || checkLocationLimit()) {
                    if(!locationArrayList.get(position).isSelected()){
                        postArticlePresenter.addPostLocation(articleId, locationArrayList.get(position).getId(), "");
                    }else{
                        postArticlePresenter.removePostLocation(articleId, locationArrayList.get(position).getId());
                    }

                    updateSelectedList(locationArrayList.get(position), locationArrayList.get(position).isSelected());
                    locationArrayList.get(position).setSelected(!locationArrayList.get(position).isSelected());
                    selectLocationAdapter.notifyItemChanged(position);

                    loadLocations();
                }
//                else {
//                    //TODO: limit reached error
//                }
            }
        });

        locationRecyclerView.setHasFixedSize(true);
        locationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        locationRecyclerView.setAdapter(selectLocationAdapter);
        locationRecyclerView.setItemAnimator(null);
    }

    private void updateSelectedList(Location tagItem, boolean remove) {
        int pos = -1;
        for (int i = 0; i < locationArrayListSelected.size(); i++) {
            // if (tagItemArrayListSelected.get(i).getId().equals(tagItem.getId())) {
            if (locationArrayListSelected.get(i).getNameToShow().equals(tagItem.getNameToShow())) {
                pos = i;
            }
        }

        if (pos >= 0 && remove) {
            locationArrayListSelected.remove(pos);
        } else if (pos == -1 && !remove) {
            locationArrayListSelected.add(tagItem);
        }
    }

    private void loadLocations() {
        flowLayout.removeAllViews();

        // flowLayout.addView(makeTagIcon());
        for (Location tagItem : locationArrayListSelected) {
            // flowLayout.addView(makeTagTextView(tagItem.getName()));
            flowLayout.addView(createTagItem(tagItem.getNameToShow(), tagItem.getId()));
        }
        if (checkLocationLimit()) {
            flowLayout.addView(makeTagEditText());

            if(!TextUtils.isEmpty(currentlyEditingText)) {
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
//                    String newTag = s.toString().replace(",", "");
//                    if (newTag.length() > 0) {
//                        currentlyEditingText = null;
//                        String tempId = addNewLocation(newTag);
//                        postArticlePresenter.addPostLocation(articleId, newTag, tempId);
//                        editText.setText(null);
//                        postArticlePresenter.getSuggestedLocation(articleId, "");
//                        hideKeyboard(AddPostPlaceActivity.this);
//                    } else {
//                        editText.setText(null);
//                        postArticlePresenter.getSuggestedLocation(articleId, "");
//                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    currentlyEditingText = s.toString();
                    postArticlePresenter.getSuggestedLocation(articleId, s.toString());
                }else{
                    postArticlePresenter.getSuggestedLocation(articleId, "");
                }
            }
        });

        if(!TextUtils.isEmpty(currentlyEditingText)){
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
                    postArticlePresenter.removePostLocation(articleId, id);
                }
                removeLocation(id);
            }
            ((ViewGroup) v.getParent()).removeView(v);
            loadLocations();
        });

        return v;
    }

    private void removeLocation(String id) {
        int i = 0;
        int j = 0;
        int selectedPos = -1;
        int tagPos = -1;

        for (Location tagItem : locationArrayListSelected) {
            if (tagItem.getId().equals(id)) {
                selectedPos = i;
            }
            i++;
        }
        if (selectedPos >= 0) {
            locationArrayListSelected.remove(selectedPos);
        }

        for (Location tagItem : locationArrayList) {
            if (tagItem.getId().equals(id)) {
                tagPos = j;
            }
            j++;
        }
        if (tagPos >= 0) {
            locationArrayList.get(tagPos).setSelected(false);
        }
        selectLocationAdapter.notifyDataSetChanged();

        listeners();
    }

    private boolean checkLocationLimit() {
        return locationArrayListSelected.size() < 5;
    }

    private void markSelected(){
        for (int i = 0; i < locationArrayList.size(); i++){
            for(int j = 0; j < locationArrayListSelected.size(); j++){
                if(locationArrayListSelected.get(j).getNameToShow().equals(locationArrayList.get(i).getNameToShow())){
                    locationArrayList.get(i).setSelected(true);
                }
            }
        }
        selectLocationAdapter.notifyDataSetChanged();
    }

    private String addNewLocation(String tag) {
        String tempId = generateTemLocationId();

        Location tagItem = new Location(tempId);
        //tagItem.setId(tempId);
        //tagItem.setName(tag);
        locationArrayListSelected.add(tagItem);

        loadLocations();

        return tempId;
    }

    private String generateTemLocationId() {
        int min = 100;
        int max = 999;
        int randomInt = (int) Math.floor(Math.random() * (max - min + 1) + min);
        String tempTag = String.valueOf(randomInt);

        if (tempLocationIds.contains(tempTag)) {
            return generateTemLocationId();
        } else {
            tempLocationIds.add(tempTag);
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
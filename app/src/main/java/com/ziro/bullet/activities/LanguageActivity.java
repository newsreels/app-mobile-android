package com.ziro.bullet.activities;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.ziro.bullet.utills.PaginationListener.PAGE_START;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.CacheData.DbHandler;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.LanguageAdapter;
import com.ziro.bullet.adapters.RegionAdapter;
import com.ziro.bullet.adapters.RegionDialogAdapter;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.BaseModel;
import com.ziro.bullet.fragments.RestartPopup;
import com.ziro.bullet.interfaces.LanguageInterface;
import com.ziro.bullet.interfaces.PostArticleCallback;
import com.ziro.bullet.interfaces.ReportConcernDialog;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.language.LanguageResponse;
import com.ziro.bullet.model.language.LanguagesItem;
import com.ziro.bullet.model.language.region.Region;
import com.ziro.bullet.model.language.region.RegionApiResponse;
import com.ziro.bullet.presenter.LanguagePresenter;
import com.ziro.bullet.presenter.PostArticlePresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class LanguageActivity extends BaseActivity implements LanguageInterface, RegionAdapter.FavoriteChangeListener, LanguageAdapter.LanguageClickCallback {

    public static final String LANGUAGE_RESULT_KEY = "resultLanguage";
    public static final String ARTICLE_ID_KEY = "articleIdKey";

    private RelativeLayout header;
    private RelativeLayout done;
    private TextView selectLanguageTitle;
    private RecyclerView language_list;
    private LanguagePresenter presenter;
    private LanguageAdapter languageAdapter;
    private RegionAdapter regionAdapter;
    private RegionDialogAdapter regionDialogAdapter;
    private ArrayList<LanguagesItem> languages = new ArrayList<>();
    private List<Region> regionList = new ArrayList<>();
    private boolean isLoading = false;
    private String nextPage = PAGE_START;
    private boolean isLastPage;
    private PrefConfig preference;
    private boolean isReg = false;
    private boolean isChanges = false;
    private String mCode = "";
    private RelativeLayout progress;
    private LinearLayout ll_no_results;
    private boolean isPostLanguageSelection;
    private ImageView ivBack;
    private TextView tvSelectedRegion;
    private AlertDialog selectRegionDialog;
    private RecyclerView rvRegions;
    private TextView tvContinue;

    private DbHandler cacheManager;
    private LanguagesItem languagesItemSelected;
    private PostArticlePresenter postArticlePresenter;
    private PostArticleCallback postArticleCallback = new PostArticleCallback() {
        @Override
        public void loaderShow(boolean flag) {

        }

        @Override
        public void error(String error) {

        }

        @Override
        public void success(Object responseBody) {

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
    private String articleId;
    private String operationType;
    private Region selectedRegion = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            String flow = getIntent().getStringExtra("flow");
            isPostLanguageSelection = getIntent().getBooleanExtra("isPostLanguageSelection", false);
            articleId = getIntent().getStringExtra(ARTICLE_ID_KEY);
            languagesItemSelected = (LanguagesItem) getIntent().getSerializableExtra("languagesItemSelected");
            operationType = getIntent().getStringExtra("type");
        }
        Utils.checkAppModeColor(this, false);
        setContentView(R.layout.activity_language);
        postArticlePresenter = new PostArticlePresenter(this, postArticleCallback);
        bindViews();
        init();
        initDialog();
        setListener();
        Utils.saveSystemThemeAsDefault(this, preference);
        setBundle();

        Utils.setStatusBarColor(this);
    }

    private void setBundle() {
        if (getIntent() != null) {
            String flow = getIntent().getStringExtra("flow");

            if (isPostLanguageSelection) {
            } else if (TextUtils.isEmpty(flow)) {
                //Register flow
                done.setVisibility(View.VISIBLE);
                isReg = true;
            } else {
                //setting flow
                isReg = false;
            }
        }

//        if (isPostLanguageSelection) {
//            selectLanguageTitle.setVisibility(View.VISIBLE);
//        } else {
//            selectLanguageTitle.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putBoolean("isChanges", isChanges);
        // etc.
    }

    @Override
    public void onRestoreInstanceState(@NotNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        isChanges = savedInstanceState.getBoolean("isChanges");
    }

    private void init() {
        showLoadingView(true);
        preference = new PrefConfig(this);
        presenter = new LanguagePresenter(this, this);
        regionAdapter = new RegionAdapter(this);
        regionDialogAdapter = new RegionDialogAdapter(this);
        cacheManager = new DbHandler(this);
        if (operationType != null) {
            switch (operationType) {
                case Constants.PRIMARY_LANGUAGE:
                    selectLanguageTitle.setText(this.getResources().getString(R.string.primary_language));
                    break;
                case Constants.SECONDARY_LANGUAGE:
                    selectLanguageTitle.setText(this.getResources().getString(R.string.secondary_language));
                    // tvSelectedRegion.setVisibility(View.GONE);
                    isPostLanguageSelection = true;
                    break;
                case Constants.REGION:
                    selectLanguageTitle.setText(this.getResources().getString(R.string.region));
                    // tvSelectedRegion.setVisibility(View.GONE);
                    tvContinue.setText(this.getResources().getString(R.string.next));
                    enableSaveButton(true);
                    break;
            }
        }


        if (operationType.equals(Constants.SECONDARY_LANGUAGE)) {
            presenter.getLanguagesWithoutRegion(preference.getSelectedRegion());
        } else {
            presenter.getRegions();
        }

        languageAdapter = new LanguageAdapter(languages, this, this, isPostLanguageSelection);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        language_list.setLayoutManager(manager);
//        language_list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        languageAdapter.setCallback(this);
        switch (operationType) {
            case Constants.PRIMARY_LANGUAGE:
            case Constants.SECONDARY_LANGUAGE:
                language_list.setAdapter(languageAdapter);
                break;
            case Constants.REGION:
                language_list.setAdapter(regionAdapter);
                language_list.setAdapter(regionAdapter);
                break;
        }
    }

    private void initDialog() {
        AlertDialog.Builder regionDialogBuilder = new AlertDialog.Builder(this);
        View regionDialogView =
                LayoutInflater.from(this).inflate(R.layout.region_dialog_view, null, false);
        regionDialogBuilder.setView(regionDialogView);
        regionDialogBuilder.setCancelable(true);
        selectRegionDialog = regionDialogBuilder.create();
        selectRegionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectRegionDialog.setCanceledOnTouchOutside(true);
        rvRegions = regionDialogView.findViewById(R.id.rv_regions);
        rvRegions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvRegions.setAdapter(regionDialogAdapter);
    }

    private void setListener() {
        ivBack.setOnClickListener(v -> finish());

        tvSelectedRegion.setOnClickListener(v -> selectRegionDialog.show());

        done.setOnClickListener(v -> {
            if (operationType.equals(Constants.REGION)) {
                if (languagesItemSelected == null) {
                    showLoadingView(true);
                    presenter.getLanguages(selectedRegion.getId());
                    return;
                }
                if ((!preference.getSelectedRegion().equals(selectedRegion.getId()) || !preference.isLanguagePushedToServer().equals(languagesItemSelected.getId()))) {
                    operationType = Constants.PRIMARY_LANGUAGE;
                } else {
                    finish();
                    return;
                }
            }
            if (isReg) {
                Map<String, String> params = new HashMap<>();
                params.put(Events.KEYS.LANGUAGE_ID, getLanguageCode(mCode));
                AnalyticsEvents.INSTANCE.logEvent(LanguageActivity.this,
                        params,
                        Events.REG_SELECT_LANG_APP);
//                LocaleManager.setLocale(LanguageActivity.this, getLanguageCode(mCode), getCountryCode(mCode));
                try {
                    preference.setLanguageForServer(languagesItemSelected.getId());
                    preference.setPrefPrimaryLang(languagesItemSelected.getName());
                } catch (Exception ignore) {
                }
            } else if (operationType.equals(Constants.PRIMARY_LANGUAGE)) {
                RestartPopup restartPopup = new RestartPopup(LanguageActivity.this, new ReportConcernDialog() {
                    @Override
                    public void isPositive(boolean flag, String msg) {
                        if (flag) {
                            AnalyticsEvents.INSTANCE.logEvent(LanguageActivity.this,
                                    Events.APP_LANGUAGE_SELECTION_CLICK);
                            if (cacheManager != null) {
                                cacheManager.clearDb();
                                if (preference != null)
                                    preference.setAppStateHomeTabs("");
                                Constants.homeDataUpdate = true;
                                Constants.menuDataUpdate = true;
                                Constants.reelDataUpdate = true;
                            }

//                            LocaleManager.setLocale(LanguageActivity.this, getLanguageCode(languagesItemSelected.getCode()), getCountryCode(languagesItemSelected.getCode()));
                            if (selectedRegion.getId().equals(preference.getSelectedRegion())) {
//                                preference.setLanguageForServer(languagesItemSelected.getId());
//                                preference.setPrefPrimaryLang(languagesItemSelected.getName());
                                presenter.updateLanguage(languagesItemSelected.getId(), operationType);
                            } else {
                                presenter.updateRegion(selectedRegion.getId());
                            }
                        }
                    }
                });
                restartPopup.showDialog();
            } else if (operationType.equals(Constants.SECONDARY_LANGUAGE)) {
                presenter.updateLanguage(languagesItemSelected.getId(), operationType);
                preference.setPrefSecondaryLang(languagesItemSelected.getName());
                showLoadingView(true);
            }
        });
    }

    private void bindViews() {
        ll_no_results = findViewById(R.id.ll_no_results);
        header = findViewById(R.id.header);
        done = findViewById(R.id.done);
        progress = findViewById(R.id.progress);
        language_list = findViewById(R.id.language_list);
        selectLanguageTitle = findViewById(R.id.tv_change_lang_title);
        ivBack = findViewById(R.id.leftArrow);
        tvSelectedRegion = findViewById(R.id.tv_select_region);
        tvContinue = findViewById(R.id.continue_text);
        enableSaveButton(false);
    }


    @Override
    public void languageResult(int size) {
        if (size > 0) {
            ll_no_results.setVisibility(View.GONE);
        } else {
            ll_no_results.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void loaderShow(boolean flag) {
        showLoadingView(flag);
    }

    @Override
    public void error(String error) {
        Utils.showSnacky(getWindow().getDecorView().getRootView(), error);
    }

    @Override
    public void success(Object response) {
        isLoading = false;
        showLoadingView(false);
        if (response instanceof LanguageResponse) {
            LanguageResponse languageResponse = (LanguageResponse) response;
            if (languageResponse != null && languageResponse.getLanguages() != null && !languageResponse.getLanguages().isEmpty()) {
                languages = (ArrayList<LanguagesItem>) languageResponse.getLanguages();
                LanguagesItem languagesItem = null;
                for (int i = 0; i < languageResponse.getLanguages().size(); i++) {
                    if (operationType.equals(Constants.SECONDARY_LANGUAGE)) {
                        if (languages.get(i).getTag().equals(Constants.SECONDARY_LANGUAGE) || languages.get(i).getName().equalsIgnoreCase(preference.getPrefSecondaryLang())) {
                            languagesItem = languages.get(i);
                            languagesItem.setTag(Constants.SECONDARY_LANGUAGE);
                        }
                    } else if (languages.get(i).getTag().equals(Constants.PRIMARY_LANGUAGE)) {
                        languagesItem = languages.get(i);
                        languagesItem.setTag(Constants.PRIMARY_LANGUAGE);
                    }
                }
                if (languagesItem != null) {
                    languages.remove(languagesItem);
                    languages.add(0, languagesItem);
                    languagesItemSelected = languagesItem;
                } /*else {
                    if (operationType.equals(Constants.PRIMARY_LANGUAGE)) {
                        languages.get(0).setTag(Constants.PRIMARY_LANGUAGE);
                        languagesItemSelected = languages.get(0);
                    }
                }*/
                languageAdapter.updateLanguageList(languages);
                tvContinue.setText(this.getResources().getString(R.string.select));
                if (operationType.equals(Constants.REGION)) {
                    language_list.setAdapter(languageAdapter);
                    // tvSelectedRegion.setVisibility(View.VISIBLE);
                    selectLanguageTitle.setText(R.string.select_region_langauge);
                }
            }
        } else if (response instanceof RegionApiResponse) {
            RegionApiResponse regionApiResponse = (RegionApiResponse) response;
            if (regionApiResponse != null && regionApiResponse.getRegions() != null && !regionApiResponse.getRegions().isEmpty()) {
                regionList = regionApiResponse.getRegions();
                for (Region r : regionList) {
                    if (r.getFavorite()) {
                        selectedRegion = r;
                    }
                }
                if (selectedRegion != null) {
                    tvSelectedRegion.setText(selectedRegion.getName());
                    regionList.remove(selectedRegion);
                    regionList.add(0, selectedRegion);
                    if (operationType.equals(Constants.PRIMARY_LANGUAGE)) {
                        showLoadingView(true);
                        presenter.getLanguages(selectedRegion.getId());
                    }
                }
                regionDialogAdapter.updateRegion(regionList);
                if (operationType.equals(Constants.REGION)) {
                    regionAdapter.updateRegion(regionList);
                    language_list.setAdapter(regionAdapter);
                }
            }
        } else if (response instanceof BaseModel) {
            BaseModel baseModel = (BaseModel) response;
            if (baseModel.message.equals("Success")) {
                if (Objects.equals(operationType, Constants.PRIMARY_LANGUAGE)) {
                    if (!selectedRegion.getId().equals(preference.getSelectedRegion())) {
                        showLoadingView(true);
                        preference.setSelectedRegion(selectedRegion.getId());
                        presenter.updateLanguage(languagesItemSelected.getId(), operationType);
                        return;
                    }
                    preference.setLanguageForServer(languagesItemSelected.getId());
                    preference.setPrefPrimaryLang(languagesItemSelected.getName());
                    setLanguage(Locale.forLanguageTag(languagesItemSelected.getCode()));

                    Intent intent = new Intent(LanguageActivity.this, MainActivityNew.class);
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                    return;
                }
                Toast.makeText(this, this.getResources().getString(R.string.preference_updated), Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (operationType.equals(Constants.REGION)) {
            if (tvSelectedRegion.getVisibility() == View.VISIBLE) {
                // tvSelectedRegion.setVisibility(View.GONE);
                language_list.setAdapter(regionAdapter);
                selectLanguageTitle.setText(this.getResources().getString(R.string.region));
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    private String getLanguageCode(String code) {
        String[] codesArray = code.split("-");
        return codesArray[0];
    }

    private String getCountryCode(String code) {
        String[] codesArray = code.split("-");
        if (codesArray.length > 1)
            return codesArray[1];
        else return "";
    }

    private void showLoadingView(boolean isShow) {
        if (isFinishing()) {
            return;
        }
        if (isShow) {
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRegionSelection(@NonNull Region region, int position) {
        if (operationType.equals(Constants.REGION) || selectedRegion == null || !region.getId().equals(preference.getSelectedRegion())) {
            enableSaveButton(true);
        }
        selectedRegion = region;
        tvSelectedRegion.setText(selectedRegion.getName());
        List<Region> updateRegion = new ArrayList<>();
        for (Region r : regionList) {
            if (r.getId().equals(region.getId())) {
                r.setFavorite(true);
            } else {
                r.setFavorite(false);
            }
            updateRegion.add(r);
        }
        regionList = updateRegion;
        regionAdapter.updateRegion(regionList);
        regionDialogAdapter.updateRegion(regionList);
//        if (operationType.equals(Constants.PRIMARY_LANGUAGE)) {
        if (selectRegionDialog != null && selectRegionDialog.isShowing()) {
            showLoadingView(true);
            selectRegionDialog.dismiss();
            presenter.getLanguages(selectedRegion.getId());
        }
//        }
    }

    private void enableSaveButton(boolean isEnable) {
        if (isEnable) {
            done.setEnabled(true);
            DrawableCompat.setTint(done.getBackground(), ContextCompat.getColor(this, R.color.theme_color_1));
        } else {
            done.setEnabled(false);
            DrawableCompat.setTint(done.getBackground(), ContextCompat.getColor(this, R.color.disabled_setting_btn));
        }
    }

    @Override
    public void onClick(int position, String id, String code) {
        languagesItemSelected = languages.get(position);

        if ((operationType.equals(Constants.PRIMARY_LANGUAGE) && !preference.isLanguagePushedToServer().equals(languagesItemSelected.getId()))) {
            enableSaveButton(true);
        } else if (operationType.equals(Constants.SECONDARY_LANGUAGE) && !preference.getPrefSecondaryLang().equals(languagesItemSelected.getName())) {
            enableSaveButton(true);
        } else enableSaveButton(operationType.equals(Constants.REGION));

        isChanges = true;
        if (isPostLanguageSelection) {
            int itemPos = position;
            for (int i = 0; i < languages.size(); i++) {
                languages.get(i).setTag("");
                if (languages.get(i).getId().equalsIgnoreCase(id)) {
                    itemPos = i;
                }
            }
            if (itemPos < languages.size()) {
                languages.get(itemPos).setTag(operationType);
                languageAdapter.notifyDataSetChanged();
            }
            mCode = code;
        } else if (isReg) {
            int itemPos = position;
            for (int i = 0; i < languages.size(); i++) {
                languages.get(i).setSelected(false);
                if (languages.get(i).getId().equalsIgnoreCase(id)) {
                    itemPos = i;
                }
            }
            if (itemPos < languages.size()) {
                languages.get(itemPos).setSelected(true);
                preference.setLanguageForServer(id);
                preference.setDefaultLanguage(languages.get(itemPos));
                languageAdapter.notifyDataSetChanged();
            }
            mCode = code;
        } else {
            int itemPos = position;
            for (int i = 0; i < languages.size(); i++) {
                languages.get(i).setTag("");
                if (languages.get(i).getId().equalsIgnoreCase(id)) {
                    itemPos = i;
                }
            }
            if (itemPos < languages.size()) {
                if (!operationType.equals(Constants.REGION)) {
                    languages.get(itemPos).setTag(operationType);
                } else {
                    languages.get(itemPos).setTag(Constants.PRIMARY_LANGUAGE);
                }
                languageAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void isLastItem(boolean flag) {

    }
}
package com.ziro.bullet.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PostArticleParams;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.ArticleCallback;
import com.ziro.bullet.interfaces.PostArticleCallback;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.presenter.PostArticlePresenter;
import com.ziro.bullet.utills.Utils;

public class CreateYoutubePopup implements PostArticleCallback {
    private Activity activity;
    private Dialog dialog;
    private ArticleCallback callback;
    private PostArticleParams postArticleParams;
    private PostArticlePresenter postArticlePresenter;
    private PrefConfig prefConfig;
    private RelativeLayout progress;

    // CONSTRUCTORS
    public CreateYoutubePopup(Activity activity) {
        this.activity = activity;
        postArticlePresenter = new PostArticlePresenter(activity, this);
    }

    // SHOW DIALOG METHOD
    public void showDialog(ArticleCallback callback) {
        this.callback = callback;
        postArticleParams = new PostArticleParams();
        dialog = new Dialog(activity, R.style.FullScreenDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.youtube_upload_popup);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        prefConfig = new PrefConfig(activity);
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        RelativeLayout popup_bg = dialog.findViewById(R.id.popup_bg);
        progress = dialog.findViewById(R.id.progress);
        ImageView loader = dialog.findViewById(R.id.loader);
        TextView importLink = dialog.findViewById(R.id.import_link);
        EditText youtubeEditText = dialog.findViewById(R.id.youtube_edit_text);

        Glide.with(loader)
                .load(Utils.getLoaderForTheme(prefConfig.getAppTheme()))
                .into(loader);

        popup_bg.setOnClickListener(v -> {
            if (dialog != null)
                dialog.dismiss();
        });
        importLink.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(youtubeEditText.getText())) {
                if (postArticleParams != null) {
                    postArticleParams.setYoutube_id(youtubeEditText.getText().toString());
                    postArticleParams.setType("youtube");
                    postArticlePresenter.createArticle(postArticleParams, false);
                }
            } else {
                Toast.makeText(activity, activity.getString(R.string.youtube_link_not_valid_mg), Toast.LENGTH_SHORT).show();
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }
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
        if(activity != null && error != null) {
            Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void success(Object responseBody) {

    }

    @Override
    public void successDelete() {

    }

    @Override
    public void createSuccess(Article article, String type) {
        if (callback != null && article != null) {
            callback.onCallback(article);
        }
        if (dialog != null)
            dialog.dismiss();
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

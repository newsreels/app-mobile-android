package com.ziro.bullet.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.adapters.TabsItemAdapter;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.MenuInterface;
import com.ziro.bullet.model.Tabs.DataItem;
import com.ziro.bullet.utills.SpacesItemDecoration;

import java.util.ArrayList;

public class MenuPopup {

    private Context context;
    private Dialog dialog;
    private MenuInterface listener;
    private ImageView popup_cancel;
    private RelativeLayout popup;
    private PrefConfig prefConfig;
    private RecyclerView tabs_list;
    private ArrayList<DataItem> arrayList;
    private SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(13);

    // CONSTRUCTORS
    public MenuPopup(Context context, ArrayList<DataItem> arrayList, MenuInterface listener) {
        this.context = context;
        this.listener = listener;
        this.arrayList = arrayList;
        prefConfig = new PrefConfig(context);
    }


    // SHOW DIALOG METHOD
    public void hideDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public boolean isVisible() {
        if (dialog != null) {
            return dialog.isShowing();
        } else {
            return false;
        }
    }

    public void updateData(ArrayList<DataItem> arrayList) {
        this.arrayList = arrayList;
    }

    public void showDialog() {
        Log.d("TAG", "showDialog: " + dialog);
        if (dialog == null) {
            dialog = new Dialog(context, R.style.FullScreenDialogStyle);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.menu_popup);
            dialog.setCancelable(false);
            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();

//        wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
            window.setAttributes(wlp);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

            initViews(dialog);

            // menu popup status bar color change
            int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                window.setStatusBarColor(ContextCompat.getColor(context, R.color.black));
                View decorView = window.getDecorView();
                decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); //set status text  light
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
                window.setStatusBarColor(ContextCompat.getColor(context, R.color.white));
            }
        }
        setPopup(arrayList);

        if (!((Activity) context).isFinishing())
            if (!dialog.isShowing()) {
                dialog.show();
            }

    }

    private void initViews(Dialog view) {
        popup_cancel = view.findViewById(R.id.popup_cancel);
        popup = view.findViewById(R.id.popup);
        tabs_list = view.findViewById(R.id.tabs_list);
        popup.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));

        RotateAnimation rotateAnimation = new RotateAnimation(-45, 0,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration((long) 200);
        rotateAnimation.setRepeatCount(0);

        popup_cancel.startAnimation(rotateAnimation);
        popup_cancel.setOnClickListener(v -> {

            RotateAnimation rotateAnimation1 = new RotateAnimation(0, -45,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation1.setDuration((long) 200);
            rotateAnimation1.setRepeatCount(0);
            popup_cancel.startAnimation(rotateAnimation1);

            if (listener != null) listener.selectOption("cancel");

            //hideDialog();
        });
    }

    private void setPopup(ArrayList<DataItem> arrayList) {
        if (arrayList == null)
            return;

        GridLayoutManager manager = new GridLayoutManager(context, 2, RecyclerView.VERTICAL, false);
        tabs_list.removeItemDecoration(spacesItemDecoration);
        tabs_list.addItemDecoration(spacesItemDecoration);
        tabs_list.setLayoutManager(manager);
        TabsItemAdapter bigAdapter = new TabsItemAdapter(context, arrayList);
        bigAdapter.setCallback((item1, position1) -> {
            if (listener != null)
                listener.selectTab(item1);
            if (dialog != null)
                dialog.dismiss();
        });
        tabs_list.setAdapter(bigAdapter);

        tabs_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int position = manager.findFirstCompletelyVisibleItemPosition();
                if (position == 0 || position == -1) {
                    popup_cancel.setVisibility(View.VISIBLE);
                } else {
                    popup_cancel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }
}

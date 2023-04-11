package com.ziro.bullet.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.EditionActivity;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.FloatSeekBar;
import com.ziro.bullet.utills.Utils;

import static com.ziro.bullet.activities.MainActivityNew.RESULT_INTENT_CHANGE_EDITION;


public class MenuDialogFragment extends DialogFragment {

    private MenuCallback menuCallback;
    private PrefConfig prefConfig;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Set transparent background and no title
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

            Window window = getDialog().getWindow();

            window.setGravity(Gravity.TOP | Gravity.END);

//            // after that, setting values for x and y works "naturally"
//            WindowManager.LayoutParams params = window.getAttributes();
//            params.x = 300;
//            params.y = 100;
//            window.setAttributes(params);
        }

        return inflater.inflate(R.layout.fragment_menu, container);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefConfig = new PrefConfig(getContext());
        RadioGroup rgViewMode = view.findViewById(R.id.radio_group_view_mode);

        RadioButton rb_list = view.findViewById(R.id.rb_list);
        RadioButton rb_extended = view.findViewById(R.id.rb_extended);
        ImageView ivClose = view.findViewById(R.id.ivClose);
        RoundedImageView ivFlag1 = view.findViewById(R.id.ivFlag1);
        RoundedImageView ivFlag2 = view.findViewById(R.id.ivFlag2);
        RoundedImageView ivFlag3 = view.findViewById(R.id.ivFlag3);
        TextView tvEdition = view.findViewById(R.id.tvEdition);
        RelativeLayout rlEditionContainer = view.findViewById(R.id.rlEditionContainer);

        ivClose.setOnClickListener(v -> {
            if (getDialog() != null)
                getDialog().dismiss();
        });

        rlEditionContainer.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditionActivity.class);
            intent.putExtra("flow", "menu");
            getActivity().startActivityForResult(intent, RESULT_INTENT_CHANGE_EDITION);
            if (getDialog() != null)
                getDialog().dismiss();
        });


        if (prefConfig.getEditions().size() > 0) {
            StringBuilder txt = new StringBuilder();
            for (int i = 0; i < prefConfig.getEditions().size(); i++) {
                if (i == 0) {
                    ivFlag1.setVisibility(View.VISIBLE);
                    GlideToVectorYou.init().with(getContext()).load(Uri.parse(prefConfig.getEditions().get(i).getImage()),ivFlag1);
//                    Utils.fetchSvg(getContext(), prefConfig.getEditions().get(i).getImage(), ivFlag1);
                } else if (i == 1) {
                    ivFlag2.setVisibility(View.VISIBLE);
                    GlideToVectorYou.init().with(getContext()).load(Uri.parse(prefConfig.getEditions().get(i).getImage()),ivFlag2);
//                    Utils.fetchSvg(getContext(), prefConfig.getEditions().get(i).getImage(), ivFlag2);
                } else if (i == 2) {
                    ivFlag3.setVisibility(View.VISIBLE);
                    GlideToVectorYou.init().with(getContext()).load(Uri.parse(prefConfig.getEditions().get(i).getImage()),ivFlag3);
//                    Utils.fetchSvg(getContext(), prefConfig.getEditions().get(i).getImage(), ivFlag3);
                }

                txt.append(prefConfig.getEditions().get(i).getName());
                if (i != prefConfig.getEditions().size() - 1) {
                    txt.append(", ");
                }
            }
            tvEdition.setText(txt);
        }


//        speed.setProgress(Constants.reading_speed);
//        speed.setOnSeekChangeListener(new OnSeekChangeListener() {
//            @Override
//            public void onSeeking(SeekParams seekParams) {
//                speeder.setText(seekParams.progressFloat +"x");
//                menuCallback.onSpeedChange(seekParams.progressFloat);
//            }
//
//            @Override
//            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
//
//            }
//        });

        if (!TextUtils.isEmpty(prefConfig.getMenuViewMode())) {
            if (prefConfig.getMenuViewMode().equalsIgnoreCase(Constants.EXTENDED)) {
                rb_list.setChecked(false);
                rb_extended.setChecked(true);
            } else if (prefConfig.getMenuViewMode().equalsIgnoreCase(Constants.LIST)) {
                rb_list.setChecked(true);
                rb_extended.setChecked(false);
            }
        }

        rgViewMode.setOnCheckedChangeListener((RadioGroup.OnCheckedChangeListener) (group, checkedId) -> {
            RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);

            if (checkedRadioButton == rb_list) {
                menuCallback.onViewModeSelected(0);
            } else {
                menuCallback.onViewModeSelected(1);
            }
            if (getDialog() != null)
                getDialog().dismiss();
        });

    }

    public void setMenuCallback(MenuCallback menuCallback) {
        this.menuCallback = menuCallback;
    }

    interface MenuCallback {
        void onViewModeSelected(int mode);

        void onNarratorMode(int mode);

        void onSpeedChange(float speed);
    }
}

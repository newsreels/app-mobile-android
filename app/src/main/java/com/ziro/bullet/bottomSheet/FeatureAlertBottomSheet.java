package com.ziro.bullet.bottomSheet;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;

import org.jetbrains.annotations.NotNull;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeatureAlertBottomSheet extends BottomSheetDialogFragment {

    public static FeatureAlertBottomSheet newInstance(String id, String title, String msg, String imageUrl) {
        FeatureAlertBottomSheet featureAlertBottomSheet = new FeatureAlertBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("title", title);
        bundle.putString("msg", msg);
        bundle.putString("imageUrl", imageUrl);
        featureAlertBottomSheet.setArguments(bundle);
        return featureAlertBottomSheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_feature_alert, container,
                false);

        String title = "", msg = "", url = "", id = "";
        if (getArguments() != null && getArguments().containsKey("title")) {
            title = getArguments().getString("title");
            msg = getArguments().getString("msg");
            url = getArguments().getString("imageUrl");
            id = getArguments().getString("id");
        }

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvMsg = view.findViewById(R.id.tvMsg);
        RelativeLayout rlOK = view.findViewById(R.id.rlOK);
        ImageView image = view.findViewById(R.id.image);

        tvTitle.setText(title);
        tvMsg.setText(msg);

        PrefConfig prefConfig = new PrefConfig(getActivity());

        if(!TextUtils.isEmpty(url)) {
            Picasso.get()
                    .load(url)
                    .error(R.drawable.img_place_holder)
                    .into(image);
        }

        String finalId = id;
        rlOK.setOnClickListener(v -> {
            Call<ResponseBody> call = ApiClient
                    .getInstance(getActivity())
                    .getApi()
                    .dismissAlert("Bearer " + prefConfig.getAccessToken(), finalId);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    getDialog().dismiss();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    getDialog().dismiss();
                }
            });
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //bottom sheet round corners can be obtained but the while background appears to remove that we need to add this.
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetDialogTheme);
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        bottomSheetDialog.setOnShowListener(dialog -> {
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            BottomSheetBehavior behavior = null;
            if (bottomSheet != null) {
                behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setSkipCollapsed(true);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        return bottomSheetDialog;
    }
}

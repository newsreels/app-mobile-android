package com.ziro.bullet.bottomSheet;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.DirectShare;
import com.ziro.bullet.adapters.ReelBottomSheetAdapter;
import com.ziro.bullet.adapters.ReelShareAdapter;
import com.ziro.bullet.data.models.ShareInfo;
import com.ziro.bullet.fragments.LoadingProgressDialog;
import com.ziro.bullet.interfaces.ReelBottomSheetCallback;
import com.ziro.bullet.mediapicker.permissions.PermissionChecker;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.ProcessVideo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReelsBottomSheet {

    public static final int PERMISSION_REQUEST_STORAGE = 31243;

    LoadingProgressDialog mLoadingDialog;
    String whatsappLabel = "Whatsapp";
    String whatsappStatusLabel = "Whatsapp Status";
    String instagramLabel = "Instagram";
    String instagramStatusLabel = "Stories";
    String facebookLabel = "Facebook";
    String facebookStatusLabel = "Stories";
    private Activity context;
    private BottomSheetDialog dialog;
    private String[] nameOfAppsToShareWith = new String[]{
            "com.whatsapp"
            , "com.facebook.katana"
            , "com.facebook.orca"
            , "com.facebook.mlite"
//            , "com.instagram.android"
            , "com.snapchat.android"
            , "org.telegram.messenger"
            , "com.twitter.android"
            , "com.tencent.mm"
            , "jp.naver.line.android"
            , "com.skype.raider"
//            , "org.thoughtcrime.securesms"
            , "com.viber.voip"
            , "kik.android"
//            , "com.google.android.apps.messaging"
            , "com.samsung.android.messaging"
    };
    private String[] nameOfAppsToDirectShareWith = new String[]{
            "com.whatsapp"
            , "com.facebook.katana"
            , "com.instagram.android"
            , "com.snapchat.android"
            , "com.zhiliaoapp.musically"
            , "org.telegram.messenger"
            , "com.twitter.android"
            , "com.tencent.mm"
            , "jp.naver.line.android"
            , "com.viber.voip"
            , "kik.android"
    };
    private String whatsappPackage = "com.whatsapp";
    private String instagramPackage = "com.instagram.android";
    private String facebookPackage = "com.facebook.katana";

    private ShareInfo mShareInfo;

    public ReelsBottomSheet(Activity context) {
        this.context = context;
        dialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
    }

    public void show(ReelBottomSheetCallback callback, ShareInfo shareInfo, DialogInterface.OnDismissListener onDismissListener) {
        mShareInfo = shareInfo;
        if (dialog != null && shareInfo != null) {

            View dialogView = context.getLayoutInflater().inflate(R.layout.reel_bottom_sheet, null);

            ImageView saveIcon = dialogView.findViewById(R.id.saveIcon);
            TextView saveText = dialogView.findViewById(R.id.saveText);
            RecyclerView appsLink = dialogView.findViewById(R.id.apps);
            RecyclerView appsDirect = dialogView.findViewById(R.id.appsDirect);
            LinearLayout report = dialogView.findViewById(R.id.report);
            LinearLayout save = dialogView.findViewById(R.id.save);
            LinearLayout saveToDevice = dialogView.findViewById(R.id.saveToDevice);

            appsLink.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            appsLink.setAdapter(getLinkShareAdapter(shareInfo, callback));

            appsDirect.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            appsDirect.setAdapter(getDirectShareAdapter(shareInfo, callback));

            if (shareInfo.isArticle_archived()) {
                saveText.setText(context.getResources().getString(R.string.remove_fav));
            } else {
                saveText.setText(context.getResources().getString(R.string.add_fav));
            }

            report.setOnClickListener(v -> {
                if (callback == null) return;
                callback.onReport();
                hide();
            });

            saveToDevice.setOnClickListener(v -> {
                if (PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                        PermissionChecker.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    addWaterMark(false, Environment.getExternalStorageDirectory().getPath() + "/Movies", shareInfo, null);
                } else {
                    PermissionChecker.requestPermissions(context, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
                }
            });
            save.setOnClickListener(v -> {
                if (callback == null) return;
                callback.onSave();
                hide();
            });

            dialog.setContentView(dialogView);
            dialog.setDismissWithAnimation(true);
            dialog.setOnDismissListener(onDismissListener);
            dialog.setOnShowListener(dialogInterface -> {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = (FrameLayout) d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                if (bottomSheet != null) {
                    BottomSheetBehavior.from(bottomSheet)
                            .setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            });
            dialog.show();
        }
    }

    private ReelBottomSheetAdapter getLinkShareAdapter(ShareInfo shareInfo, ReelBottomSheetCallback callback) {
        ArrayList<ResolveInfo> targets = new ArrayList<>();
        ArrayList<String> packageNames = new ArrayList<>();
        Intent template = new Intent(Intent.ACTION_SEND);
        template.setType("text/plain");
        List<ResolveInfo> candidates = context.getPackageManager().queryIntentActivities(template, 0);

        for (ResolveInfo candidate : candidates) {
            String packageName = candidate.activityInfo.packageName;
            if (!packageNames.contains(packageName)) {
                if (Arrays.asList(nameOfAppsToShareWith).contains(packageName.toLowerCase())) {
                    Log.d("geneXrate", "show List = [" + packageName + "]");
                    targets.add(candidate);
                    packageNames.add(packageName);
                }
            }
        }
        targets.add(null);

        ReelBottomSheetAdapter adapterLink = new ReelBottomSheetAdapter(context, targets, shareInfo, info -> {
            if (info != null) {
                Intent target = new Intent(Intent.ACTION_SEND);
                target.setType("text/plain");
                target.putExtra(Intent.EXTRA_TEXT, shareInfo.getShare_message());
                target.setPackage(info.getPackageName());
                context.startActivity(target);
            } else {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareInfo.getShare_message());
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                context.startActivity(shareIntent);
            }

            if (dialog != null) {
                if (callback == null) return;
                callback.onIgnore();
                dialog.dismiss();
            }
        });

        return adapterLink;
    }

    private ReelShareAdapter getDirectShareAdapter(ShareInfo shareInfo, ReelBottomSheetCallback callback) {
        ArrayList<String> packageNames = new ArrayList<>();
        Intent template = new Intent(Intent.ACTION_SEND);
        template.setType("video/mp4");
        List<ResolveInfo> candidates = context.getPackageManager().queryIntentActivities(template, 0);

        List<DirectShare> shareList = new ArrayList<>();

        for (ResolveInfo candidate : candidates) {
            String packageName = candidate.activityInfo.packageName;
            if (!packageNames.contains(packageName)) {
                if (Arrays.asList(nameOfAppsToDirectShareWith).contains(packageName.toLowerCase())) {
                    if (packageName.toLowerCase().equals(whatsappPackage)) {
                        shareList.add(0, new DirectShare(
                                whatsappLabel, candidate.activityInfo.loadIcon(context.getPackageManager()), candidate.activityInfo.packageName
                        ));
                        shareList.add(1, new DirectShare(
                                whatsappStatusLabel, candidate.activityInfo.loadIcon(context.getPackageManager()), candidate.activityInfo.packageName
                        ));
                    } else if (packageName.toLowerCase().equals(instagramPackage)) {
                        shareList.add(new DirectShare(
                                instagramLabel, candidate.activityInfo.loadIcon(context.getPackageManager()), candidate.activityInfo.packageName
                        ));
                        shareList.add(new DirectShare(
                                instagramStatusLabel, candidate.activityInfo.loadIcon(context.getPackageManager()), candidate.activityInfo.packageName
                        ));
                    } else if (packageName.toLowerCase().equals(facebookPackage)) {
                        shareList.add(new DirectShare(
                                facebookLabel, candidate.activityInfo.loadIcon(context.getPackageManager()), candidate.activityInfo.packageName
                        ));
                        shareList.add(new DirectShare(
                                facebookStatusLabel, candidate.activityInfo.loadIcon(context.getPackageManager()), candidate.activityInfo.packageName
                        ));
                    } else {
                        shareList.add(new DirectShare(
                                candidate.loadLabel(context.getPackageManager()).toString(), candidate.activityInfo.loadIcon(context.getPackageManager()), candidate.activityInfo.packageName
                        ));
                    }
                    packageNames.add(packageName);
                }
            }
        }

        shareList.add(null);
        ReelShareAdapter adapterDirect = new ReelShareAdapter(context, shareList, shareInfo, new MediaShare.ShareSheet() {
            @Override
            public void onShare(DirectShare info) {
                if (info != null)
                    addWaterMark(true, context.getCacheDir().getPath(), shareInfo, null);
                else {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, shareInfo.getShare_message());
                    sendIntent.setType("text/plain");
                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    context.startActivity(shareIntent);
                }
                if (dialog != null) {
                    if (callback == null) return;
                    callback.onIgnore();
                    dialog.dismiss();
                }
            }
        });
        return adapterDirect;
    }

    public void hide() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }

    public void saveToDevice() {
        if (mShareInfo != null)
            addWaterMark(false, Environment.getExternalStorageDirectory().getPath() + "/Movies", mShareInfo, null);
    }

    private void addWaterMark(
            boolean isShare,
            String root, ShareInfo shareInfo,
            ResolveInfo info) {
        if (!InternetCheckHelper.isConnected()) {
            Toast.makeText(context, "" + context.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        showLoadingView(true);
        mLoadingDialog.updateProgress(0);
        File f = new File(root, "compressed.mp4");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String originalPath = f.getPath();

        File f1 = new File(root, "out.mp4");
        try {
            f1.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String outPath = f1.getPath();

        Uri uriForFile;

        saveImage();

        Log.d("TAG", "addWaterMark: originalPath= " + originalPath);
        Log.d("TAG", "addWaterMark: outPath= " + outPath);

        String authority = context.getPackageName() + ".fileprovider";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uriForFile = FileProvider.getUriForFile(context, authority, f1);
        } else
            uriForFile = Uri.fromFile(f1);

        ProcessVideo processVideo = new ProcessVideo(context);

        Log.d("TAG", "addWaterMark: url= " + shareInfo.getMedia());
        processVideo.download(shareInfo.getMedia(), originalPath, new ProcessVideo.DownloadProgress() {

            @Override
            public void onProgress(long progress, long length) {
                Log.d("TAG", "onProgress() called with: progress = [" + progress + "], length = [" + length + "]");

                double l = ((double) progress / length) * 100;

                Log.d("TAG", "onProgress: ======" + l);
                Log.d("TAG", "onProgress: ======" + (int) (l / 2));
                updateProgress((int) (l / 2));
            }

            @Override
            public void onError() {
                Log.d("TAG", "onError: ");
            }

            @Override
            public void onComplete() {
                updateProgress(51);
                int ss = 51;
                while (ss != 95) {
                    ss++;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateProgress(ss);
                }
//                FFmpeg fmpeg = FFmpeg.getInstance(context);
//                if (fmpeg.isSupported()) {
//                    File file = new File(context.getCacheDir(), "watermark5.png");
//                    String rootPath = file.getPath();
//
//                    String[] cmd =
//                            {"-y",
//                                    "-i",
//                                    originalPath,
//                                    "-i",
//                                    rootPath,
//                                    "-filter_complex",
//                                    "[0:v][1:v]overlay=" + 20 + ":" + 20 +
//                                            ":enable='between" + "(t," + 0 + "," + 120 + ")" + "'[out]",
//                                    "-map",
//                                    "[out]",
//                                    outPath
//                            };
//
//                    fmpeg.execute(cmd, new FFcommandExecuteResponseHandler() {
//                        @Override
//                        public void onSuccess(String message) {
//                            Log.d("TAG", "onSuccess() called with: message = [" + message + "]");
//                        }
//
//                        @Override
//                        public void onProgress(String message) {
//                            Log.d("TAG", "onProgress() called with: message = [" + message + "]");
//                        }
//
//                        @Override
//                        public void onFailure(String message) {
//                            Log.d("TAG", "onFailure() called with: message = [" + message + "]");
//                        }
//
//                        @Override
//                        public void onStart() {
//                            Log.d("TAG", "onStart: ");
//                        }
//
//                        @Override
//                        public void onFinish() {
//                            updateProgress(99);
//                            showLoadingView(false);
//                            if (isShare) {
//                                Intent target = new Intent(android.content.Intent.ACTION_SEND);
//                                target.setType("video/*");
//                                target.putExtra(Intent.EXTRA_TEXT, shareInfo.getShare_message());
//                                target.putExtra(Intent.EXTRA_STREAM, uriForFile);
//                                if (info != null && info.activityInfo != null && !TextUtils.isEmpty(info.activityInfo.packageName))
//                                    target.setPackage(info.activityInfo.packageName);
//                                target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                context.startActivity(Intent.createChooser(target,
//                                        "Share Video"));
//                            } else {
//                                context.runOnUiThread(() -> Toast.makeText(context, "Newsreel saved.", Toast.LENGTH_SHORT).show());
//                            }
//                        }
//                    });
//                } else {
//                    Toast.makeText(context, "not supported", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }

    private void updateProgress(int i) {
        try{
        context.runOnUiThread(() -> {
            mLoadingDialog.updateProgress(i);
        });}catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveImage() {
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_watermark);
        File file = new File(context.getCacheDir(), "watermark5.png");
        if (!file.exists()) {
            try {
                FileOutputStream outStream = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.flush();
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showLoadingView(boolean isShow) {
        if (context == null || context.isFinishing()) {
            return;
        }
        if (isShow) {
            if (mLoadingDialog == null) {
                mLoadingDialog = new LoadingProgressDialog(context);
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

    public interface ShareSheet {
        void onShare(ResolveInfo info);
    }
}

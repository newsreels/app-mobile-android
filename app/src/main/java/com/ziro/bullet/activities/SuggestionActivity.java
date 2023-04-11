package com.ziro.bullet.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.adapters.ImagesAdapter;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.ReportBottomSheetListener;
import com.ziro.bullet.interfaces.SuggestionCallback;
import com.ziro.bullet.presenter.SuggestionPresenter;
import com.ziro.bullet.utills.Utils;

import java.io.File;
import java.util.ArrayList;

public class SuggestionActivity extends BaseActivity implements ReportBottomSheetListener, SuggestionCallback {

    private EditText notes, email;
    private ArrayList<Uri> arrayList = new ArrayList<>();
    private ArrayList<File> arrayListFiles = new ArrayList<>();
    private ImagesAdapter adapter;
    private RecyclerView img_list;
    private PrefConfig prefConfig;
    private Dialog dialog;
    private SuggestionPresenter presenter;
    private CardView send;
    private RelativeLayout progress;
    private RelativeLayout btn_color;
    private String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.checkAppModeColor(this, false);
        setContentView(R.layout.activity_suggestion);
        prefConfig = new PrefConfig(this);
        presenter = new SuggestionPresenter(this, this);
        bindView();
        setlistener();
        setData();
    }

    private void bindView() {
        img_list = findViewById(R.id.img_list);
        email = findViewById(R.id.email);
        notes = findViewById(R.id.notes);
        send = findViewById(R.id.continue_);
        progress = findViewById(R.id.progress);
        btn_color = findViewById(R.id.btn_color);
        notes.requestFocus();
    }

    private void setData() {
        send.setEnabled(false);
        btn_color.setBackgroundColor(ContextCompat.getColor(SuggestionActivity.this, R.color.edittextredHint));
        adapter = new ImagesAdapter(this, this, arrayList);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        img_list.setLayoutManager(manager);
        img_list.setAdapter(adapter);
        email.setText(prefConfig.getUserEmail());
        notes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence != null) {
                    if (charSequence.length() > 0) {
                        send.setEnabled(true);
                        btn_color.setBackgroundColor(ContextCompat.getColor(SuggestionActivity.this, R.color.theme_color_1));
                    } else {
                        send.setEnabled(false);
                        btn_color.setBackgroundColor(ContextCompat.getColor(SuggestionActivity.this, R.color.edittextredHint));
                    }
                } else {
                    send.setEnabled(false);
                    btn_color.setBackgroundColor(ContextCompat.getColor(SuggestionActivity.this, R.color.edittextredHint));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setlistener() {
        send.setOnClickListener(v -> sendSuggestions());
        findViewById(R.id.back).setOnClickListener(v -> finish());
//        findViewById(R.id.footer).setOnClickListener(v -> {
//            fromGallery();
//        });
    }

    private void sendSuggestions() {
        if (!TextUtils.isEmpty(email.getText())) {
            if (Utils.isValidEmail(email.getText().toString())) {
                if (!TextUtils.isEmpty(notes.getText())) {
                    Utils.hideKeyboard(this, email);
                    if (presenter != null) {
                        presenter.submitSuggestion(email.getText().toString(), notes.getText().toString(), arrayListFiles);
                    }
                } else {
                    Utils.showSnacky(getWindow().getDecorView().getRootView(), getString(R.string.enter_suggestions));
                }
            } else {
                Utils.showSnacky(getWindow().getDecorView().getRootView(), getString(R.string.enter_valid_email));
            }
        } else {
            Utils.showSnacky(getWindow().getDecorView().getRootView(), getString(R.string.enter_email));
        }
    }

    public void fromGallery() {
        //for multi images
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);

//        //for single images
//        Intent i = new Intent(Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(i, 2);

        if (!checkPermissions(PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 6709);
            error(getString(R.string.permision));
            return;
        }

        String[] mimeTypes = {"image/jpeg", "image/jpg"};
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                .setType("image/jpg")
                .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(pickPhoto, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && null != data) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();

                    String selectedImagePath = getRealPathFromURI(imageUri);
                    File file = new File(selectedImagePath);
                    arrayListFiles.add(file);
                    arrayList.add(imageUri);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            } else if (data.getData() != null) {
                String selectedImagePath = getRealPathFromURI(data.getData());
                File file = new File(selectedImagePath);
                arrayListFiles.add(file);
                arrayList.add(data.getData());
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        } else {
            Log.i("SonaSys", "resultCode: " + resultCode);
            switch (resultCode) {
                case 0:
                    Log.i("SonaSys", "User cancelled");
                    break;
                case -1:
                    break;
            }
        }
    }

    @Override
    public void selectReport(boolean isSelect, int position) {
        if (!isSelect && position < arrayList.size() && adapter != null) {
            arrayList.remove(position);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void error(String error) {
        Utils.showPopupMessageWithCloseButton(this, 2000, error, true);
    }

    @Override
    public void error404(String error) {
        Utils.showPopupMessageWithCloseButton(this, 2000, error, true);
    }

    public void openDialog() {
        dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_success);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        ImageView dialog_cancel = dialog.findViewById(R.id.dialog_cancel);
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();

    }

    @Override
    public void onSuccess() {
        arrayList.clear();
        arrayListFiles.clear();
        if (adapter != null)
            adapter.notifyDataSetChanged();
        notes.setText("");
//        Utils.showPopupMessageWithCloseButton(this, 2000, "Feedback sent!", false);
//        new Handler().postDelayed(() -> finish(),3000);
        openDialog();
//            SimpleDialog simpleDialog = new SimpleDialog(this);
//            simpleDialog.showDialog();
    }

    @Override
    public void loaderShow(boolean b) {
        if (b) {
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void success(ArrayList<String> list) {

    }

    @Override
    public void success() {

    }

    /*METHOD TO GET REAL IMAGE PATH FROM URI*/
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String path = cursor.getString(idx);
            cursor.close();
            return path;
        }
        return "";
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
}
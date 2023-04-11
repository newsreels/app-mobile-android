package com.ziro.bullet.utills;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class ChatEditText extends EditText {
    /* Must use this constructor in order for the layout files to instantiate the class properly */
    public ChatEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    private KeyImeChange keyImeChangeListener;

    public void setKeyImeChangeListener(KeyImeChange listener) {
        keyImeChangeListener = listener;
    }

    public interface KeyImeChange {
        void onKeyIme(int keyCode, KeyEvent event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 67) {
            if (keyImeChangeListener != null) {
                keyImeChangeListener.onKeyIme(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
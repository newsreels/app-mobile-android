package com.ziro.bullet.mediapicker.utils;

import android.content.Context;
import android.content.ContextWrapper;


/**
 * @author：luck
 * @date：2019-12-15 19:34
 * @describe：ContextWrapper
 */
public class PictureContextWrapper extends ContextWrapper {

    public PictureContextWrapper(Context base) {
        super(base);
    }

    public static ContextWrapper wrap(Context context, int language) {
//        PictureLanguageUtils.setAppLanguage(context, language);
        return new PictureContextWrapper(context);
    }
}

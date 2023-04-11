package com.ziro.bullet.utills;

import android.content.Context;

public class HandleResolutions {

    private Context context;

    public HandleResolutions(Context context) {
        this.context = context;
//        - 1440 x 2560  xxxhdpi
//        - 1080 x 2280  xxhdpi
//        - 1080 x 1920  xxxhdpi
//        - 1440 x 3040  xxxhdpi
//        - galaxy nexus xxhdpi
    }

    public String checkScreen() {
        float density = context.getResources().getDisplayMetrics().density;
        String i = String.valueOf(density);
        if (density < 0.75) {
            //ldpi
            i = "ldpi";
        } else if (density >= 0.75 && density < 1.0) {
            //mdpi
            i = "mdpi";
        } else if (density >= 1.0 && density < 1.5) {
            //hdpi
            i = "hdpi";
        } else if (density >= 1.5 && density < 2.0) {
            //xhdpi
            i = "xhdpi";
        }else if (density >= 2.0 && density < 3.0) {
            //xxhdpi
            i = "xxhdpi";
        }else if (density >= 3.0 && density <= 4.0) {
            //xxxhdpi
            i = "xxxhdpi";
        }
        return i;
    }
}

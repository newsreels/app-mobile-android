package com.ziro.bullet.adapters;

import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

public class DirectShare {
    String name,packageName;
    Drawable image;

    public DirectShare(String name,Drawable image,String packageName) {
        this.name = name;
        this.image = image;
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }
}

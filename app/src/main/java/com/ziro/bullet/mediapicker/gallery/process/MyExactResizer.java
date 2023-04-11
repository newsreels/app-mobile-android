package com.ziro.bullet.mediapicker.gallery.process;

import androidx.annotation.NonNull;

import com.otaliastudios.transcoder.strategy.size.ExactSize;
import com.otaliastudios.transcoder.strategy.size.Resizer;
import com.otaliastudios.transcoder.strategy.size.Size;

public class MyExactResizer implements Resizer {
    private int height;
    private int width;

    public MyExactResizer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @NonNull
    @Override
    public Size getOutputSize(@NonNull Size inputSize) throws Exception {
        return new ExactSize(width, height);
    }
}

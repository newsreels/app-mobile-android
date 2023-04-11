package com.ziro.bullet.interfaces;

import java.util.ArrayList;

public interface ReportBottomSheetListener {
    void selectReport(boolean isSelect, int position);

    void error(String string);

    void loaderShow(boolean b);

    void success(ArrayList<String> list);
    void success();
}

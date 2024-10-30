package com.newsreels.app.interfaces;

import com.newsreels.app.model.TCP.TCPResponse;

public interface TCPInterface {
    void onTCPSuccess(String type, TCPResponse response);
    void onTCPSuccessPagination(String type, TCPResponse response);
    void error(String error);
    void loaderShow(boolean flag);
}
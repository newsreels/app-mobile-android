package com.ziro.bullet.interfaces;

public interface PasswordInterface {
    void loaderShow(boolean flag);

    void error(String error);

    void error404(String error);

    void success(boolean flag);

    void reset();

    void resetSuccess(String str);

}

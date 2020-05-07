package com.example.picca;


import androidx.annotation.StringRes;

public interface TopBarInteractions {
    void showBackIcon(boolean visible);
    void setTitle(@StringRes int res);
    void setTitle(String res);
    void showMenuIcon(boolean visible);
    void showBasketIcon(boolean visible);
    int isMenuIconVisible();
    void showTopBar(boolean visible);
}
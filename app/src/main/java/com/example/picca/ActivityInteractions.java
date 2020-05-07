package com.example.picca;

public interface ActivityInteractions {
    boolean navigateTo(BaseFragment fragment, boolean addToBackstack);
    boolean navigateBack();
    TopBarInteractions topBar();
    void changeDrawerMenuState();
}

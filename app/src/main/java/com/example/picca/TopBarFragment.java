package com.example.picca;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.example.picca.fragments.BasketFragment;


public class TopBarFragment extends BaseFragment implements TopBarInteractions, View.OnClickListener {
    private View goBackBtn;
    private View menuBtn;
    private View basketBtn;
    private TextView title;
    private RelativeLayout topBar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_bar, container, false);
        title = view.findViewById(R.id.logo);
        menuBtn = view.findViewById(R.id.menu_btn);
        basketBtn = view.findViewById(R.id.basket_bt);
        topBar=view.findViewById(R.id.root_topbar);
        menuBtn.setOnClickListener(this);
        basketBtn.setOnClickListener(this);


        return view;
    }

    @Override
    public void showBackIcon(boolean visible) {
    }

    @Override
    public void setTitle(@StringRes int res) {

    }

    @Override
    public void setTitle(String res) {
        title.setText(res);
    }

    @Override
    public void showMenuIcon(boolean visible) {
        menuBtn.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showBasketIcon(boolean visible) {
        basketBtn.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public int isMenuIconVisible() {
        return menuBtn.getVisibility();
    }

    @Override
    public void showTopBar(boolean visible) {
        topBar.setVisibility(visible? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View view) {
        if (getActions() == null) {
            return;
        }

        switch (view.getId()) {
            case R.id.menu_btn:
                if(actions!=null){
                    actions.changeDrawerMenuState();
                }

           break;
            case R.id.basket_bt:
                assert getActions() != null;
                getActions().navigateTo(BasketFragment.Companion.newInstance(), true);
                break;
        }
    }


}


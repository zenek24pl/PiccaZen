package com.example.picca;

import android.content.Context;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class BaseFragment extends Fragment {
    ActivityInteractions actions;

    @Nullable
    public ActivityInteractions getActions() {
        return actions;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            //noinspection unchecked // will check later ^^
            actions = (ActivityInteractions) context;
        } catch (Exception ex) {
            throw new IllegalStateException("Activity must implement correct action interface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        actions = null;
    }
}

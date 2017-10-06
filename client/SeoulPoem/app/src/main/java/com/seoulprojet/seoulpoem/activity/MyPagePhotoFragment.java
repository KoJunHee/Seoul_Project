package com.seoulprojet.seoulpoem.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.seoulprojet.seoulpoem.R;

/**
 * Created by lynn on 2017-09-17.
 */

public class MyPagePhotoFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mypage_photo_fragment, container, false);
    }
}

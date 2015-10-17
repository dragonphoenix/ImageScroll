package com.dragon.imagescroll;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageFragment extends Fragment {

    private int mImageRes;

    public ImageFragment() {
        //mImageRes = R.mipmap.e;
    }

    public ImageFragment(int res) {
        mImageRes = res;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_fragment, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        int res;
        if (savedInstanceState != null) {
            res = savedInstanceState.getInt("image");
            mImageRes = res;
        }
        imageView.setBackgroundResource(mImageRes);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putInt("image", mImageRes);
        }
    }

}

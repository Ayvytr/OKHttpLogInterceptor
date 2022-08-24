package com.ayvytr.networkapp.t;

import android.os.Bundle;

import com.ayvytr.networkapp.R;

import androidx.annotation.Nullable;

/**
 * @author EDZ
 */
public class ChildActivity extends BaseActivity3<ChildJavaT> {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t.childFunction();
    }

    @Override
    public Class<ChildJavaT> getViewModelClass() {
        return ChildJavaT.class;
    }

}

package com.ayvytr.coroutines.t;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


/**
 * @author EDZ
 */
public abstract class BaseActivity<T extends BaseJavaT> extends AppCompatActivity {
    //    private Class<T> tClass;
    protected T t;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        t = new ViewModelProvider(this).get(getViewModelClass());
    }

    public abstract Class<T> getViewModelClass();
}

package com.ayvytr.coroutines.t;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;

/**
 * @author EDZ
 */
public class BaseActivity2<T extends ViewModel> extends AppCompatActivity {
    private T t;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        t = new ViewModelProvider(this).get(T.class);
//        t = new ViewModelProvider(this).get(new TypeToken<T>(){}.getRawType());
//        t = new ViewModelProvider(this).<T>get(Class.forName(T.class.getName()));
//        t = new ViewModelProvider(this).<T>get(Class(T.class));
    }
}

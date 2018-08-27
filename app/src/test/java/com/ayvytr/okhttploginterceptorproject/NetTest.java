package com.ayvytr.okhttploginterceptorproject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;

/**
 * Created by Do on 2017/6/21.
 */

public class NetTest
{
    public void test()
    {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception
            {
                e.onNext(1);
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception
            {
                System.out.println(integer);
            }
        });
    }
}

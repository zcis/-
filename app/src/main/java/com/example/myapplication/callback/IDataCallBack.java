package com.example.myapplication.callback;

import io.reactivex.disposables.Disposable;

public interface IDataCallBack<T> {
    void onStateSucess(T t);
    void onStateError(String msg);
    void onResponseDisposable(Disposable disposable);
}

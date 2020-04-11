package com.example.myapplication.mvp.model;

import android.util.Log;

import com.example.myapplication.callback.IDataCallBack;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class RxObserver<T> implements Observer<T> {
    private IDataCallBack mIDataCallBack;

    public RxObserver(IDataCallBack<T> mIDataCallBack) {
        this.mIDataCallBack = mIDataCallBack;
    }

    @Override
    public abstract void onSubscribe(Disposable d);

    @Override
    public abstract void onNext(T value);

    @Override
    public void onError(Throwable e) {
        if (e instanceof SocketException) {
            mIDataCallBack.onStateError("SocketException");
            Log.e("TAG", "SocketException: ");
        } else if (e instanceof SocketTimeoutException) {
            mIDataCallBack.onStateError("SocketTimeoutException");
            Log.e("TAG", "SocketTimeoutException: ");
        } else if (e instanceof UnknownError) {
            mIDataCallBack.onStateError("UnknownError");
            Log.e("TAG", "UnknownError: ");
        } else if (e instanceof ConnectException) {
            mIDataCallBack.onStateError("ConnectException");
            Log.e("TAG", "ConnectException: ");
        } else {
            if (e != null) {
                Log.e("TAG", "onError: " + e.getMessage());
            } else {
                Log.e("TAG", "空的: ");
            }
        }
    }

    @Override
    public void onComplete() {
        Log.e("TAG", "onComplete:...... ");
    }
}

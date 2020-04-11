package com.example.myapplication.base;

import java.lang.ref.WeakReference;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BasePresenter<V extends IBaseView<T>, T> implements IBasePresenter<T> {
    private WeakReference mWr;
    private CompositeDisposable mCompositeDisposable;

    @Override
    public void start() {

    }

    @Override
    public void start(T t) {

    }

    public void attachView(V view) {
        if (mWr == null)
            mWr = new WeakReference<V>(view);
    }

    public void detachView() {
        if (mWr != null) {
            mWr.clear();
            mWr = null;
        }
        deleteDisposable();
    }

    public void addDisposable(Disposable disposable) {
        if (mCompositeDisposable == null)
            mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(disposable);
    }

    public void deleteDisposable() {
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
            mCompositeDisposable = null;
        }
    }
}

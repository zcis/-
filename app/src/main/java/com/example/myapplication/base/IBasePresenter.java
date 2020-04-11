package com.example.myapplication.base;

public interface IBasePresenter<T> {
    void start();

    void start(T t);
}

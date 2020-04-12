package com.example.myapplication.base;

public interface IBaseView<T> {
    void stateScuess(T t);
    void stateError(String msg);
}

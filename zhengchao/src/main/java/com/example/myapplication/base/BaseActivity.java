package com.example.myapplication.base;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.app.App;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity<T> extends AppCompatActivity implements IBaseView<T> {

    private Unbinder bind;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        int layoutId = getLayout();
        if (layoutId != 0)
            setContentView(layoutId);
        App.getInstance().addActivity(this);
        bind = ButterKnife.bind(this);
        onViewCreated();
        initListenner();
    }

    protected abstract void initListenner();

    protected abstract void onViewCreated();

    protected abstract int getLayout();

    @Override
    public void stateScuess(T t) {

    }

    @Override
    public void stateError(String msg) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind != null) {
            bind.unbind();
            bind = null;
        }
        App.getInstance().removeActivity();
    }
}


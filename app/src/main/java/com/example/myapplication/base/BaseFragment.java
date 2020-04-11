package com.example.myapplication.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment<P extends BasePresenter, T>
        extends Fragment implements IBaseView<T> {
    private Unbinder unbinder;
    private P mPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutId = getLayoutId();
        View view = null;
        if (layoutId != 0)
            view = inflater.inflate(layoutId, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        if (mPresenter == null)
            mPresenter = createPresenter();

        //2.P层关联V层的生命周期
        mPresenter.attachView(this);

        initInject();
    }


    protected abstract void initInject();


    protected abstract P createPresenter();

    protected abstract int getLayoutId();


    public P getmPresenter() {
        if (mPresenter != null)
            return mPresenter;
        return null;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
        //3.P层释放V层引用
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }
    }
}

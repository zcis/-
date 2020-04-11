package com.example.myapplication.di.component;


import com.example.myapplication.di.annotation.PerSinglelton;
import com.example.myapplication.mvp.model.RxOperateImpl;

import dagger.Component;

@PerSinglelton
@Component(dependencies = AppComponent.class)
public interface RxOperateComponent {
    void inject(RxOperateImpl rxOperate);
}

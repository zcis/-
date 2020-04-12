package com.example.myapplication.di.component;



import com.example.myapplication.di.annotation.PerSinglelton;
import com.example.myapplication.mvp.presenter.HomePresenter;

import dagger.Component;

@PerSinglelton
@Component(dependencies = AppComponent.class)
public interface HomeComponent {
    void inject(HomePresenter homePresenter);
}

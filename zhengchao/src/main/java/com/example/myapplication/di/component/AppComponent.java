package com.example.myapplication.di.component;

import android.content.SharedPreferences;


import com.example.myapplication.di.module.AppModule;
import com.example.myapplication.mvp.model.OkManagerModule;
import com.example.myapplication.mvp.model.api.ApiService;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;

//专门对外提供单例(SP,OK,OkHttClient.Builder ,Retrofit和RetrofitBuidelr ApiService)的桥梁
@Singleton
@Component(modules = {AppModule.class, OkManagerModule.class})
public interface AppComponent {
    SharedPreferences provideSp();
    OkHttpClient provideOk();
    ApiService provideApiService();
}

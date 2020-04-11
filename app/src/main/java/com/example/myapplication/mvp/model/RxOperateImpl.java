package com.example.myapplication.mvp.model;


import com.example.myapplication.app.App;
import com.example.myapplication.callback.IDataCallBack;
import com.example.myapplication.di.component.DaggerRxOperateComponent;
import com.example.myapplication.mvp.model.api.ApiService;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;

/**
 * RxJava和OkHttp关联的封装类
 */
public class RxOperateImpl {
    @Inject
    ApiService mApiService;

    public RxOperateImpl() {
        //将apiService注入到RxOperateImpl里面

        DaggerRxOperateComponent.builder().
                appComponent(App.daggerAppComponent()).build().
                inject(this);
    }

    /**
     * @param url          get请求的URL地址
     * @param dataCallBack 结果回调(实际上是接口回调)
     * @param <T>          接口回调获取的值
     */
    public <T> void requestData(String url, IDataCallBack<T> dataCallBack) {
        RxSchedulersOperator.retryWhenOperator(mApiService.requestData(url)).
                subscribe(getObserver(dataCallBack));
    }

    //get请求的url
    public <T> void requestData(String url, Map<String, T> map, IDataCallBack<T> dataCallBack) {
        if (map != null || map.size() == 0) {
            requestData(url, dataCallBack);
        } else {
            RxSchedulersOperator.retryWhenOperator(mApiService.requestData(url, map))
                    .subscribe(getObserver(dataCallBack));
        }
    }

    //没有参数的post请求
    public <T> void requestFormData(String url, IDataCallBack<T> dataCallBack) {
        RxSchedulersOperator.retryWhenOperator(mApiService.requestFormData(url))
                .subscribe(getObserver(dataCallBack));
    }

    //有参数的post请求
    public <T> void requestFormData(String url, Map<String, T> map, IDataCallBack<T> dataCallBack) {
        if (map != null || map.size() == 0) {
            requestFormData(url, dataCallBack);
        } else
            RxSchedulersOperator.retryWhenOperator(mApiService.requestFormData(url, map))
                    .subscribe(getObserver(dataCallBack));
    }


    //文件下载
    public <T> void DownLoad(String Url, IDataCallBack<T> dataCallBack) {
        RxSchedulersOperator.retryWhenOperator(mApiService.downloadFile(Url))
                .subscribe(getObserver(dataCallBack));
    }

    //封装单个文件上传
    public <T> void uploading(String url, File file, IDataCallBack<T> dataCallBack) {
        /*File file = new File(url);*/
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("headimg", file.getName(), requestFile);
        RxSchedulersOperator.retryWhenOperator(mApiService.uploadSingleFile(url, filePart))
                .subscribe(getObserver(dataCallBack));
    }

    //既有请求头又有参数的post请求
    public <T> void requestFormData(String url, Map<String, T> headers, Map<String, T> params, IDataCallBack<T> dataCallBack) {
        if (headers == null || headers.size() == 0)  //请求头为空 但是参数不为空
            requestFormData(url, params, dataCallBack);
        else if (params == null || params.size() == 0)  //TODO参数为空但是请求头不为空
            requestFormData(url, headers, dataCallBack);
        else if ((headers == null || headers.size() == 0) && // 请求头和参数都为空
                (params == null || params.size() == 0))
            requestFormData(url, dataCallBack);
        else
            //请求头和参数都不为空
            RxSchedulersOperator.retryWhenOperator(mApiService.requestFormData(url, headers, params)).
                    subscribe(getObserver(dataCallBack));
    }

    //带有Json串的没有请求头的Post请求

    public <T> void requestFormData(String url, String strJson, IDataCallBack<T> dataCallBack) {
        if (strJson.isEmpty()) {
            return;
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset = utf - 8"), strJson);
        RxSchedulersOperator
                .retryWhenOperator(mApiService.requestFormData(url, body))
                .subscribe(getObserver(dataCallBack));
    }

    //带请求头的上传json串的post请求
    public <T> void requestFormData(String url, Map<String, T> haders, String jsonStr, IDataCallBack<T> dataCallBack) {
        if (haders.size() == 0 || haders == null) {
            requestFormData(url, jsonStr, dataCallBack);
        } else {
            if (jsonStr.isEmpty())
                return;
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset = utf - 8"), jsonStr);
            RxSchedulersOperator
                    .retryWhenOperator(mApiService.requestFormData(url, haders, body))
                    .subscribe(getObserver(dataCallBack));
        }
    }


    //封装单个文件携带参数上传
    public <T> void upLoadingData(String url, File file, IDataCallBack<T> dataCallBack) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("headimg", file.getName(), requestFile);
        RxSchedulersOperator.retryWhenOperator(mApiService.uploadStrFile(url, requestFile, filePart))
                .subscribe(getObserver(dataCallBack));
    }

    //封装回调的方法
    private <T> RxObserver<T> getObserver(IDataCallBack<T> iDataCallBack) {
        return new RxObserver<T>(iDataCallBack) {
            @Override
            public void onSubscribe(Disposable d) {
                if (iDataCallBack != null) {
                    iDataCallBack.onResponseDisposable(d);
                }
            }

            @Override
            public void onNext(T t) {
                if (iDataCallBack != null) {
                    iDataCallBack.onStateSucess(t);
                }
            }
        };
    }
}

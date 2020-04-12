package com.example.myapplication.mvp.model;

import android.util.Log;
import android.view.View;

import com.example.myapplication.callback.IObserableListener;
import com.jakewharton.rxbinding2.view.RxView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 封装RxJava线程切换 和操作符
 */
public class RxSchedulersOperator<T> {
    private static int currentConnectCount = 0;
    private static int connectCount = 10;
    private static long retryTime;

    //线程切换封装的方法
    public static <T> ObservableTransformer<T, T> io_main() {
        return upstream ->  //Lambda箭头《===》实现类+实现方法
                upstream.subscribeOn(Schedulers.io()). //注意是subscribeOn 而不是 observeOn
                        observeOn(AndroidSchedulers.mainThread());
    }

    //封装retryWhen操作符  (错误链接10次 如果10次没链上就认定为链接超时)
    public static <T> ObservableSource<T> retryWhenOperator(Observable<T> observable) {
        return observable.subscribeOn(Schedulers.io()).
                //错误重连操作符
                        retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    //返回值是 Obserable被观察者
                    @Override
                    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws
                            Exception {
                        //上面retryWhen操作符执行完之后 直接执行下面flatmap中的方法   作用：要获取下面的throwable异常
                        return throwableObservable.flatMap(
                                new Function<Throwable, ObservableSource<?>>() {
                                    @Override
                                    public ObservableSource<?> apply(Throwable throwable) throws Exception {

                                        if (throwable instanceof IOException) {
                                            //属于IO异常  就是网络链接异常  就是网络链接失败异常 就需要错误重连

                                            //设置重连次数和每次重连的间隔时间
                                            if (currentConnectCount < connectCount) {
                                                //错误重连了10次 链接上了

                                                currentConnectCount++;
                                                Log.e("TAG", "网络链接失败，正在进行第" + currentConnectCount + "次重连");
                                                //设置每次重连间隔时间
                                                retryTime = 1000 + currentConnectCount * 500;

                                                //每次错误重连的话同时只发送1个事件，  delay延迟发送
                                                return Observable.just(1).delay(retryTime, TimeUnit.MILLISECONDS);
                                            } else {
                                                //链了10次，10次都没链上
                                                return Observable.error(new Throwable("已经错误重连10次了，一直没链上，请尝试换个网试试"));
                                            }

                                        } else {
                                            //不属于IO网络链接异常，不需要错误重连 直接把这个异常抛出去
                                            return Observable.error(new Throwable("不属于IO网络链接异常," + throwable.getMessage()));
                                        }
                                    }
                                });
                    }
                }).observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableSource<T> flatMapOprator(Observable<T> observable1,
                                                         Observable<T> observable2,
                                                         IObserableListener<T> listener) {
        return observable1.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<T>() {
                    @Override
                    public void accept(T t) throws Exception {
                        if (listener != null) {
                            listener.getObserListener(t);
                        }
                    }
                }).observeOn(Schedulers.io()).flatMap(new Function<T, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(T t) throws Exception {
                        return observable2;
                    }
                }).observeOn(AndroidSchedulers.mainThread());
    }

    //三个网络并发请求
    public static <T> ObservableSource<T> concurr(Observable<T> observable1, Observable<T> observable2, Observable<T> observable3) {
        return Observable.merge(observable1, observable2, observable3)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    //多个网络请求并发
    public static <T> ObservableSource<T> concurr(Observable<T>... observables) {
        return Observable.mergeArray(observables).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    //防止VIew被多次点击
    public static <T> Observable<Object> requestFirstData(View view) {
        return RxView.clicks(view).subscribeOn(Schedulers.io())
                .throttleFirst(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread());
    }
}


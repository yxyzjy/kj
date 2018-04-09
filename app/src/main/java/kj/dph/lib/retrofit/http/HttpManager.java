package kj.dph.lib.retrofit.http;


import com.google.gson.Gson;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.lang.ref.SoftReference;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import kj.dph.lib.retrofit.exception.RetryWhenNetworkException;
import kj.dph.lib.retrofit.http.cookie.CacheInterceptor;
import kj.dph.lib.retrofit.http.cookie.HttpInterceptor;
import kj.dph.lib.retrofit.http.cookie.TokenInterceptor;
import kj.dph.lib.retrofit.listener.HttpOnNextListener;
import kj.dph.lib.retrofit.subscribers.ProgressSubscriber;
import kj.dph.com.common.Constants;
import kj.dph.com.common.MainApplication;
import kj.dph.com.network.entity.api.Base.BaseApi;
import kj.dph.com.network.httpService.HttpService;
import kj.dph.com.util.Utils;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * http交互处理类
 * Created by WZG on 2016/7/16.
 */
public class HttpManager {
    /*弱引用對象*/
    private SoftReference<HttpOnNextListener> onNextListener;
    private SoftReference<RxAppCompatActivity> appCompatActivity;

    public HttpManager(HttpOnNextListener onNextListener, RxAppCompatActivity appCompatActivity) {
        this.onNextListener=new SoftReference(onNextListener);
        this.appCompatActivity=new SoftReference(appCompatActivity);
    }

    /**
     * 处理http请求
     *
     * @param basePar 封装的请求数据
     */
    public void doHttpDeal(BaseApi basePar) {

        //手动创建一个OkHttpClient并设置超时时间缓存等设置
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new CacheInterceptor());
        builder.addInterceptor(new HttpInterceptor(basePar,basePar.isNeedToken()));
        builder.addInterceptor(new TokenInterceptor(appCompatActivity.get(),basePar.isShowErrorMsg()));
        if(Constants.IS_LOG_BODY){
            builder.addInterceptor(Utils.getHttpLoggingInterceptor());
        }
        builder.connectTimeout(basePar.getConnectionTime(), TimeUnit.SECONDS);
        builder.sslSocketFactory(HttpsTrustManager.createSSLSocketFactory());
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        builder.addNetworkInterceptor(new CacheInterceptor());
        /*缓存位置和大小*/
        builder.cache(new Cache(MainApplication.appContext.getCacheDir(),10*1024*1024));
        /*创建retrofit对象*/
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(basePar.getBaseUrl())
                .build();

        HttpService httpService = retrofit.create(HttpService.class);

        /*rx处理*/
        ProgressSubscriber subscriber=new ProgressSubscriber(basePar,onNextListener,appCompatActivity);
        Observable observable = basePar.getObservable(httpService)
                /*失败后的retry配置*/
                .retryWhen(new RetryWhenNetworkException())
                /*生命周期管理*/
                .compose(appCompatActivity.get().bindToLifecycle())
                /*http请求线程*/
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                /*回调线程*/
                .observeOn(AndroidSchedulers.mainThread())
                /*结果判断*/
                .map(basePar);

        /*数据回调*/
        observable.subscribe(subscriber);
    }
}

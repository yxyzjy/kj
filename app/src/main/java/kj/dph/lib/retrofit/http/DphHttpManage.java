package kj.dph.lib.retrofit.http;

import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import kj.dph.lib.retrofit.http.cookie.CacheInterceptor;
import kj.dph.lib.retrofit.http.cookie.HttpInterceptor;
import kj.dph.com.common.Constants;
import kj.dph.com.common.MainApplication;
import kj.dph.com.network.HttpBaseUrl;
import kj.dph.com.network.httpService.HttpService;
import kj.dph.com.util.Utils;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 项目名称： WiseMedical.Client.User.Android
 * 包名：com.dph.user.nim
 * 类描述： describe
 * 创建人： wxw https://github.com/huatianjiajia
 * 创建时间： 2017/9/19 17:41
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class DphHttpManage {
    public static HttpService getService(String mothod){
        //手动创建一个OkHttpClient并设置超时时间缓存等设置
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new CacheInterceptor());
        builder.addInterceptor(new HttpInterceptor(mothod,true));
//        builder.addInterceptor(new TokenInterceptor(appCompatActivity.get(),basePar.isShowErrorMsg()));
        if(Constants.IS_LOG_BODY){
            builder.addInterceptor(Utils.getHttpLoggingInterceptor());
        }
        builder.connectTimeout(60*5, TimeUnit.SECONDS);
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
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(getBaseUrl(mothod))
                .build();
        HttpService service = retrofit.create(HttpService.class);
        return service;
    }

    @NonNull
    private static String getBaseUrl(String mothod) {
        String str="";
        if ("web_pic_upload".equals(mothod)){
            str= HttpBaseUrl.DPH_DOCTOR_HOST_URL_EXTENSIONS.url();
        }else if ("server_time".equals(mothod)){
            str= HttpBaseUrl.DPH_DOCTOR_HOST_URL_EXTENSIONS.url();
        }else{
            str= HttpBaseUrl.DPH_DOCTOR_HOST_URL_USER.url();
        }
        return str;
    }

    /**
     * 高德地图专用
     * @param mothod
     * @return
     */
    public static HttpService getGDService(String mothod){
        //手动创建一个OkHttpClient并设置超时时间缓存等设置
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new CacheInterceptor());
        builder.addInterceptor(new HttpInterceptor(mothod,true));
//        builder.addInterceptor(new TokenInterceptor(appCompatActivity.get(),basePar.isShowErrorMsg()));
        if(Constants.IS_LOG_BODY){
            builder.addInterceptor(Utils.getHttpLoggingInterceptor());
        }
        builder.connectTimeout(60*5, TimeUnit.SECONDS);
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
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(HttpBaseUrl.DPH_GAODE_HOST_URL_USER.url())
                .build();
        HttpService service = retrofit.create(HttpService.class);
        return service;
    }
}

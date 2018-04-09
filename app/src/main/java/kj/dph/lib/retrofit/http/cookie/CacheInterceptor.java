package kj.dph.lib.retrofit.http.cookie;

import java.io.IOException;

import kj.dph.com.common.Constants;
import kj.dph.com.common.MainApplication;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static kj.dph.lib.retrofit.utils.AppUtil.isNetworkAvailable;


/**
 * get缓存方式拦截器
 * Created by WZG on 2016/10/26.
 */

public class CacheInterceptor implements Interceptor {
    public CacheInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        if (!isNetworkAvailable(MainApplication.appContext)) {//没网强制从缓存读取(必须得写，不然断网状态下，退出应用，或者等待一分钟后，就获取不到缓存）
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
        }
        Response response = null;
        try {
            response = chain.proceed(request);
        } catch (IOException e) {
            e.printStackTrace();
//            if(isShowErrorMsg){
//                EventBus.getDefault().post(new MsgEvent(HttpCode.MSG_HTTP_408));
//            }
        }
        if (response==null){
            return null;
        }
        Response responseLatest;
        if (isNetworkAvailable(MainApplication.appContext)) {
            int maxAge = Constants.NETWORK_CATCH_TIME_YES; //有网失效5s
            responseLatest = response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build();
        } else {
            int maxStale = Constants.NETWORK_CATCH_TIME_NO; // 没网失效6小时
            responseLatest= response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .build();
        }
        return  responseLatest;
    }

}

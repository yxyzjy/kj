package kj.dph.lib.retrofit.http.cookie;


import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import kj.dph.lib.retrofit.utils.LoginUtil;
import kj.dph.com.network.entity.api.Base.BaseApi;
import kj.dph.com.network.event.HttpCodeEvent;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 作者：wxw on 2016/11/28 15:13
 * 邮箱：1069289509@qq.com
 */
public class HttpInterceptor implements Interceptor {
    private boolean isNeedToken;
    private BaseApi basePar;
    private String mothod;

    public HttpInterceptor() {
    }

    public HttpInterceptor(BaseApi basePar,boolean isNeedToken) {
        this.isNeedToken = isNeedToken;
        this.basePar = basePar;
    }
    public HttpInterceptor(String  mothod,boolean isNeedToken) {
        this.isNeedToken = isNeedToken;
        this.mothod = mothod;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        Request requst=null;
        if (isNeedToken){
            requst = builder.addHeader("Content-type", "application/json")
                    .addHeader("Authorization", LoginUtil.getYzkUserToken())
                    .build();
        }else{
            requst = builder.addHeader("Content-type", "application/json")
                    .build();
        }
        Response response=chain.proceed(requst);
        if (response.code() == 204) {
            if (basePar!=null){
                EventBus.getDefault().post(new HttpCodeEvent(204,basePar.getMothed()));
            }else{
                EventBus.getDefault().post(new HttpCodeEvent(204,mothod));
            }
            return null;
        }
        return response;
    }
}
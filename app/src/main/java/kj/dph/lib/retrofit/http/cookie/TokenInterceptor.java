package kj.dph.lib.retrofit.http.cookie;

import android.app.Activity;
import android.text.TextUtils;


import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import kj.dph.lib.retrofit.utils.LoginUtil;
import kj.dph.com.util.Utils;
import kj.dph.com.common.HttpCode;
import kj.dph.com.network.event.MsgEvent;
import kj.dph.com.util.logUtil.LogUtilYxy;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 作者：wxw on 2016/11/29 12:01
 * 邮箱：1069289509@qq.com
 */
public class TokenInterceptor implements Interceptor {
    private Activity activity;
    private boolean isShowErrorMsg=true;
    public TokenInterceptor(Activity activity,boolean isShowErrorMsg) {
        this.activity = activity;
        this.isShowErrorMsg=isShowErrorMsg;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = null;
        try {
            response = chain.proceed(request);
        } catch (IOException e) {
            LogUtilYxy.i("TokenInterceptor", e.toString());
            if (isShowErrorMsg) {
                if (e instanceof UnknownHostException) {
                    EventBus.getDefault().post(new MsgEvent(HttpCode.MSG_HTTP_502));
                }
                if (e instanceof SocketTimeoutException) {
                    EventBus.getDefault().post(new MsgEvent(HttpCode.MSG_HTTP_408));
                }
            }
            e.printStackTrace();
        }
        refreshToken(response);
        if (isTokenExpired(response.code())) {//根据和服务端的约定判断token过期
//                    EventBus.getDefault().post(new MsgEvent("" + HttpCode.MSG_HTTP_401_WITHOUT_TOKEN));
            Utils.doLogout(activity, true, true);
            return null;
        }
        return response;
    }

    private void refreshToken(Response response) {
        Headers requestHeaders = response.headers();

        int requestHeadersLength = requestHeaders.size();

        for (int i = 0; i < requestHeadersLength; i++){

            String headerName = requestHeaders.name(i);

            String headerValue = requestHeaders.get(headerName);

//            LogUtilWxw.i("TAG----------->Name:"+headerName+"------------>Value:"+headerValue+"\n");

        }

        String token=response.header("Token");
        if (!TextUtils.isEmpty(token)){
            LoginUtil.setYzkUserToken(token);
        }
    }
    /**
     * 根据Response，判断Token是否失效
     *
     * @return
     */
    private boolean isTokenExpired(int code) {
        return code == HttpCode.HTTP_401;
    }


    /**
     * 同步请求方式，获取最新的Token
     *
     * @param bodyStr
     * @return
     */
    private String getNewToken(String bodyStr) throws IOException {
        String newSession = null;
        if (bodyStr.contains("NewUserToken")) {
//            NewUserToken newUserToken = com.alibaba.fastjson.JSONObject.parseObject(bodyStr, NewUserToken.class);
//            if (null == newUserToken.getNewUserToken()) {
//                newSession = "";
//                UserLoginUtil.setYzkUserToken("");
//            } else {
//                newSession = newUserToken.getNewUserToken();
//                UserLoginUtil.setYzkUserToken(newSession + "");
//            }
        }
        return newSession;
    }
}

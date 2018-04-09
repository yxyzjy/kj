package kj.dph.com.network.entity.api.Base;



import kj.dph.com.network.httpService.HttpService;
import rx.Observable;
import rx.functions.Func1;

import static kj.dph.com.network.HttpBaseUrl.DPH_DOCTOR_HOST_URL_USER;


/**
 * 请求数据统一封装类
 * Created by WZG on 2016/7/16.
 */
public abstract class BaseApi<T> implements Func1<T, String> {
    /*是否能取消加载框*/
    private boolean cancel=false;
    /*是否显示加载框*/
    private boolean showProgress=true;
    /*是否需要缓存处理*/
    private boolean cache=true;
    /*基础url*/
    private String baseUrl= DPH_DOCTOR_HOST_URL_USER.url();
    //    private String baseUrl="http://api.life.v5.com/v2.0/";
    /*方法-如果需要缓存必须设置这个参数；不需要不用設置*/
    private String mothed;
    /*超时时间-默认6秒*/
    private int connectionTime = 60;
    /*有网情况下的本地缓存时间默认60秒*/
    private int cookieNetWorkTime=60;
    /*无网络的情况下本地缓存时间默认30天*/
    private int cookieNoNetWorkTime=24*60*60*30;
    /*是否需要Token，默认false*/
    private boolean isNeedToken=false;
    private String dialogMsg;
    /*出错时是否提示信息*/
    private boolean isShowErrorMsg =true;

    public boolean isShowErrorMsg() {
        return isShowErrorMsg;
    }

    public void setShowErrorMsg(boolean showErrorMsg) {
        isShowErrorMsg = showErrorMsg;
    }

    public String getDialogMsg() {
        return dialogMsg;
    }

    /**
     * 设置加载框显示信息，为null时使用默认字样
     * @param dialogMsg
     */
    public void setDialogMsg(String dialogMsg) {
        this.dialogMsg = dialogMsg;
    }

    /**
     * 设置参数
     *
     * @param methods
     * @return
     */
    public abstract Observable getObservable(HttpService methods);


    public boolean isNeedToken() {
        return isNeedToken;
    }

    /**
     *  设置当前请求方法是否需要传递Token
     * @param needToken
     */
    public void setNeedToken(boolean needToken) {
        isNeedToken = needToken;
    }

    public int getCookieNoNetWorkTime() {
        return cookieNoNetWorkTime;
    }

    public void setCookieNoNetWorkTime(int cookieNoNetWorkTime) {
        this.cookieNoNetWorkTime = cookieNoNetWorkTime;
    }

    public int getCookieNetWorkTime() {
        return cookieNetWorkTime;
    }

    public void setCookieNetWorkTime(int cookieNetWorkTime) {
        this.cookieNetWorkTime = cookieNetWorkTime;
    }

    public String getMothed() {
        return mothed;
    }

    public int getConnectionTime() {
        return connectionTime;
    }

    public void setConnectionTime(int connectionTime) {
        this.connectionTime = connectionTime;
    }

    /**
     * 方法标记
     * 用途：
     * 1.方法单独处理；
     * 2.缓存时使用；
     * @param mothed
     */
    public void setMothed(String mothed) {
        this.mothed = mothed;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * 设置当前请求服务器基地址
     * @param baseUrl
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getUrl() {
        return baseUrl+mothed;
    }

    public boolean isCache() {
        return cache;
    }




    /**
     * 设置当前请求是否需要缓存
     * @param cache
     */
    public void setCache(boolean cache) {
        this.cache = cache;
    }
    public boolean isShowProgress() {
        return showProgress;
    }

    /**
     * 设置当前请求是否显示加载框
     * @param showProgress
     */
    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public String call(T httpResult) {
        return httpResult.toString();
    }
}

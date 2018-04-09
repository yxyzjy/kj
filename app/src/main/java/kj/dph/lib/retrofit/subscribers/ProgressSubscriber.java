package kj.dph.lib.retrofit.subscribers;

import android.content.Context;
import android.content.DialogInterface;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.SoftReference;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import kj.dph.lib.retrofit.exception.HttpTimeException;
import kj.dph.lib.retrofit.http.cookie.CookieResulte;
import kj.dph.lib.retrofit.listener.HttpOnNextListener;
import kj.dph.lib.retrofit.utils.AppUtil;
import kj.dph.lib.retrofit.utils.CookieDbUtil;
import kj.dph.com.common.Constants;
import kj.dph.com.common.HttpCode;
import kj.dph.com.common.MainApplication;
import kj.dph.com.network.entity.api.Base.BaseApi;
import kj.dph.com.network.event.HttpCodeEvent;
import kj.dph.com.network.event.MsgEvent;
import kj.dph.com.util.InternetHelper;
import kj.dph.com.util.ToastUtil;
import kj.dph.com.util.logUtil.LogUtil;
import rx.Observable;
import rx.Subscriber;


/**
 * 用于在Http请求开始时，自动显示一个ProgressDialog
 * 在Http请求结束时，关闭ProgressDialog
 * 调用者自己对请求数据进行处理
 * Created by WZG on 2016/7/16.
 */
public class ProgressSubscriber<T> extends Subscriber<T> {
    /*是否弹框*/
    private boolean showPorgress = true;
    //    回调接口
    private SoftReference<HttpOnNextListener> mSubscriberOnNextListener;
    //    弱引用反正内存泄露
    private SoftReference<Context> mActivity;
    //    加载框可自己定义
    private SweetAlertDialog pd;
    /*请求数据*/
    private BaseApi api;


    /**
     * 构造
     *
     * @param api
     */
    public ProgressSubscriber(BaseApi api, SoftReference<HttpOnNextListener> listenerSoftReference, SoftReference<Context>
            mActivity) {
        //,Activity activity
        this.api = api;
        this.mSubscriberOnNextListener = listenerSoftReference;
        this.mActivity = mActivity;
        setShowPorgress(api.isShowProgress());

        if (api.isShowProgress()) {
            initProgressDialog(api.isCancel());
        }
        EventBus.getDefault().register(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMainThread(MsgEvent msg) {
//        ToastUtil.showLong(msg.getMsg(),msg.getMothead());
        ToastUtil.showLong(msg.getMsg());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMainThread(HttpCodeEvent msg) {
        String Mothod_now = api.getMothed();
        String Mothod_msg = msg.getMothod();
//        LogUtilWxw.i("Mothod_now",Mothod_now);
//        LogUtilWxw.i("Mothod_msg",Mothod_msg);
        if (Mothod_now.equals(Mothod_msg)) {
            mSubscriberOnNextListener.get().onNext(msg.getCode() + "", api.getMothed());
            onCompleted();
        }
    }

    /**
     * 初始化加载框
     */
    private void initProgressDialog(boolean cancel) {
        Context context = mActivity.get();
        if (pd == null && context != null) {
            pd = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);//这是进度style
            String dialogMsg = api.getDialogMsg();
            if (dialogMsg == null || "".equals(dialogMsg)) {
                pd.setTitleText("正在加载中...");
            } else {
                pd.setTitleText("" + dialogMsg);
            }
            pd.setCancelable(true);
            pd.setCanceledOnTouchOutside(cancel);
            if (cancel) {
                pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        onCancelProgress();
                    }
                });
            }

        }
    }


    /**
     * 显示加载框
     */
    private void showProgressDialog() {
        if (!isShowPorgress()) return;
        Context context = mActivity.get();
        if (pd == null || context == null) return;
        if (!pd.isShowing()) {
            pd.show();
        }
    }


    /**
     * 隐藏
     */
    private void dismissProgressDialog() {
        if (!isShowPorgress()) return;
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }


    /**
     * 订阅开始时调用
     * 显示ProgressDialog
     */
    @Override
    public void onStart() {
        showProgressDialog();
        if (!InternetHelper.getNetworkIsConnected(MainApplication.getAppContext(), 1)) {
            onError(new HttpTimeException(Constants.DPH_NO_NETWORK));
            unsubscribe();
            return;
        }
        /*缓存并且有网*/
        if (api.isCache() && AppUtil.isNetworkAvailable(MainApplication.appContext)) {
             /*获取缓存数据*/
            CookieResulte cookieResulte = CookieDbUtil.getInstance().queryCookieBy(api.getUrl());
            if (cookieResulte != null) {
                long time = (System.currentTimeMillis() - cookieResulte.getTime()) / 1000;
                if (time < api.getCookieNetWorkTime()) {
                    if (mSubscriberOnNextListener.get() != null) {
                        mSubscriberOnNextListener.get().onNext(cookieResulte.getResulte() == null ? "" : cookieResulte.getResulte(), api.getMothed());
                    }
                    onCompleted();
                    unsubscribe();
                }
            }
        }
    }

    /**
     * 完成，隐藏ProgressDialog
     */
    @Override
    public void onCompleted() {
        dismissProgressDialog();
        EventBus.getDefault().unregister(this);
        if (mSubscriberOnNextListener.get() != null) {
            mSubscriberOnNextListener.get().onCompleted(api.getMothed());
        }
    }

    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     *
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        dismissProgressDialog();

        /*需要緩存并且本地有缓存才返回*/
        if (api.isCache()) {
            Observable.just(api.getUrl()).subscribe(new Subscriber<String>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    errorDo(e);
                }

                @Override
                public void onNext(String s) {
                    /*获取缓存数据*/
                    CookieResulte cookieResulte = CookieDbUtil.getInstance().queryCookieBy(s);
                    if (cookieResulte == null) {
                        throw new HttpTimeException("网络错误");
                    }
                    long time = (System.currentTimeMillis() - cookieResulte.getTime()) / 1000;
                    if (time < api.getCookieNoNetWorkTime()) {
                        if (mSubscriberOnNextListener.get() != null) {
                            mSubscriberOnNextListener.get().onNext(cookieResulte.getResulte() == null ? "" : cookieResulte.getResulte(), api.getMothed());
                        }
                    } else {
                        CookieDbUtil.getInstance().deleteCookie(cookieResulte);
                        throw new HttpTimeException("网络错误");
                    }
                }
            });
        } else {
            try {
                errorDo(e);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    /*错误统一处理*/
    private void errorDo(Throwable e) {

        Context context = mActivity.get();
        if (context == null) return;
        if (e instanceof SocketTimeoutException) {
            ToastUtil.showLong("网络中断，请检查您的网络状态");
        } else if (e instanceof ConnectException) {
            ToastUtil.showLong("网络中断，请检查您的网络状态");
        } else if (e instanceof UnknownHostException) {
            ToastUtil.showLong(HttpCode.MSG_HTTP_ERROR);
        } else {
//            ToastUtil.showLong(""+e.getMessage());
            LogUtil.i("errorDo", api.getMothed() + "---------" + e.getMessage());
        }

        if (mSubscriberOnNextListener.get() != null) {
            mSubscriberOnNextListener.get().onError(e, api.getMothed());
            mSubscriberOnNextListener.get().onCompleted(api.getMothed());

        }
    }


    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param t 创建Subscriber时的泛型类型
     */
    @Override
    public void onNext(T t) {
         /*缓存处理*/
        if (api.isCache()) {
            CookieResulte resulte = CookieDbUtil.getInstance().queryCookieBy(api.getUrl());
            long time = System.currentTimeMillis();
            /*保存和更新本地数据*/
            if (resulte == null) {
                resulte = new CookieResulte(api.getUrl(), t.toString(), time);
                CookieDbUtil.getInstance().saveCookie(resulte);
            } else {
                resulte.setResulte(t.toString());
                resulte.setTime(time);
                CookieDbUtil.getInstance().updateCookie(resulte);
            }
        }
        if (mSubscriberOnNextListener.get() != null) {
            if (t == null) {
                mSubscriberOnNextListener.get().onNext("", api.getMothed());
            } else {
                mSubscriberOnNextListener.get().onNext((String) t, api.getMothed());
            }

        }
    }

    /**
     * 取消ProgressDialog的时候，取消对observable的订阅，同时也取消了http请求
     */
    public void onCancelProgress() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }


    public boolean isShowPorgress() {
        return showPorgress;
    }

    /**
     * 是否需要弹框设置
     *
     * @param showPorgress
     */
    public void setShowPorgress(boolean showPorgress) {
        this.showPorgress = showPorgress;
    }
}
package kj.dph.lib.retrofit.subscribers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import kj.dph.lib.retrofit.exception.HttpTimeException;
import kj.dph.com.common.Constants;
import kj.dph.com.common.HttpCode;
import kj.dph.com.common.MainApplication;
import kj.dph.com.network.event.HttpCodeEvent;
import kj.dph.com.util.InternetHelper;
import kj.dph.com.util.JsonUtils;
import kj.dph.com.util.StringUtil;
import kj.dph.com.util.ToastUtil;
import kj.dph.com.util.Utils;
import kj.dph.com.util.logUtil.LogUtil;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;


/**
 * 用于在Http请求开始时，自动显示一个ProgressDialog
 * 在Http请求结束时，关闭ProgressDialog
 * 调用者自己对请求数据进行处理
 * Created by WZG on 2016/7/16.
 */
public class DphSubscriber<T> extends Subscriber<T> {
    /*是否弹框*/
    private boolean showPorgress = true;
    //    弱引用反正内存泄露
    private Activity mActivity;
    //    加载框可自己定义
    private SweetAlertDialog pd;

    private String mothod;
    /**
     * 构造
     *
     */
    public DphSubscriber(Activity
                                 mActivity, boolean isShowPorgress,String mothod) {
        this.mActivity = mActivity;
        this.mothod=mothod;
        setShowPorgress(isShowPorgress);
        if (isShowPorgress) {
            initProgressDialog(true);
        }
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMainThread(HttpCodeEvent msg) {
        String Mothod_now = mothod;
        String Mothod_msg = msg.getMothod();
        if (Mothod_now.equals(Mothod_msg)) {
            processSuccessResult(msg.getCode() + "",mothod);
            onCompleted();
        }
    }

    /**
     * 初始化加载框
     */
    private void initProgressDialog(boolean cancel) {
        if (pd == null && mActivity != null) {
            pd = new SweetAlertDialog(mActivity, SweetAlertDialog.PROGRESS_TYPE);//这是进度style
            pd.setTitleText("正在加载中...");
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
        if (pd == null || mActivity == null) return;
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
    }

    /**
     * 完成，隐藏ProgressDialog
     */
    @Override
    public void onCompleted() {
        dismissProgressDialog();
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
        errorDo(e);
    }

    /*错误统一处理*/
    private void errorDo(Throwable e) {

        Context context = mActivity;
        if (context == null) return;
        if (e instanceof SocketTimeoutException) {
            ToastUtil.showLong("网络中断，请检查您的网络状态");
        } else if (e instanceof ConnectException) {
            ToastUtil.showLong("网络中断，请检查您的网络状态");
        } else if (e instanceof UnknownHostException) {
            ToastUtil.showLong(HttpCode.MSG_HTTP_ERROR);
        } else {
            LogUtil.i("errorDo", "---------" + e.getMessage());
        }
        if (e instanceof HttpException) {
            Response response = ((HttpException) e).response();
            int code = response.code();
            String bodyStr = null;
            try {
                bodyStr = response.errorBody().string() + "";
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            if (code == HttpCode.HTTP_200) {
            } else {
                processFalResult(code, bodyStr,mothod);
                return;
            }
        } else {
            //暂时做静默处理
//            EventBus.getDefault().post(new MsgEvent(e.toString()));
            LogUtil.i("解析过程onError", e.toString() + "");
            if (e.getMessage().equals(Constants.DPH_NO_NETWORK)) {
                processFalResult(-1, e.getMessage() + "",mothod);
            }

        }
    }

    protected void processFalResult(int httpCode, String result,String mothod) {
        LogUtil.i("processFalResult", httpCode + "");
        LogUtil.i("processFalResult", result + "");
        LogUtil.i("processFalResult", mothod + "");
        switch (httpCode) {
            case HttpCode.HTTP_403:
                ToastUtil.showLong(HttpCode.MSG_HTTP_403);
                break;
            case HttpCode.HTTP_404:
                ToastUtil.showLong(HttpCode.MSG_HTTP_404);
                break;
            case HttpCode.HTTP_405:
                ToastUtil.showLong(HttpCode.MSG_HTTP_405);
                break;
            case HttpCode.HTTP_408:
                ToastUtil.showLong(HttpCode.MSG_HTTP_408);
                break;
            case HttpCode.HTTP_414:
                ToastUtil.showLong(HttpCode.MSG_HTTP_414);
                break;
            case HttpCode.HTTP_500:
                ToastUtil.showLong(HttpCode.MSG_HTTP_500);
                break;
            case HttpCode.HTTP_502:
                ToastUtil.showLong(HttpCode.MSG_HTTP_502);
                break;
            case HttpCode.HTTP_503:
                ToastUtil.showLong(HttpCode.MSG_HTTP_503);
                break;
            case HttpCode.HTTP_504:
                ToastUtil.showLong(result);
                break;
            case HttpCode.HTTP_400:
                if (!StringUtil.isEmpty(result)) {
                    parseFailToast(result, true);
                }
                break;
            default:
                ToastUtil.showLong(HttpCode.MSG_HTTP_ERROR);
                break;
        }
    }
    public static boolean parseFailToast(String result, boolean isNewApi) {
        JSONObject jsonObject = JsonUtils.parseFromJson(result);
        if (null != jsonObject) {
            Iterator<String> it = jsonObject.keys();
            List<String> keyListstr = new ArrayList<String>();
            while (it.hasNext()) {
                String key = it.next().toString() + "";
                keyListstr.add(key);
            }

            boolean containsMsg = keyListstr.contains("msg");
            boolean containsData = keyListstr.contains("dat");
            String dateStr = "";
            if (JsonUtils.getJsonObj(jsonObject, "dat") != null) {
                dateStr = JsonUtils.getJsonString(jsonObject, "dat");
            }
            if (containsData && !TextUtils.isEmpty(dateStr)) {
                parseFailDat(jsonObject);
            } else {
                if (containsMsg) {
                    parseFailMsg(jsonObject);
                }
            }
            return containsMsg;
        }
        return false;
    }
    private static void parseFailDat(JSONObject jsonObject) {
        try {
            JSONObject dateObj = jsonObject.getJSONObject("dat");
            Iterator<String> dat_it = dateObj.keys();
            while (dat_it.hasNext()) {
                //"dat":{"Logo":"请上传 店铺照片。","IDCardPicture":"请上传 身份证照片正反面。"}
                String key = dat_it.next().toString() + "";
                String keyValue = dateObj.getString(key);
                ToastUtil.showLong(keyValue);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            parseFailMsg(jsonObject);
        }
    }
    private static void parseFailMsg(JSONObject jsonObject) {
        try {
            String resultStr = jsonObject.getJSONObject("msg").getString("desc");
            if (resultStr != null) {
                ToastUtil.showLong(resultStr);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param t 创建Subscriber时的泛型类型
     */
    @Override
    public void onNext(T t) {
        processSuccessResult(Utils.deliver(t.toString()),mothod);
    }
    // 请求成功后处理
    protected void processSuccessResult(String resulte,String mothod) {
        if (Constants.IS_LOG_BODY) {
            LogUtil.i("processSuccessResult", resulte + "");
            LogUtil.i("processSuccessResult", mothod + "");
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
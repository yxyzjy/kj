package kj.dph.com.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindColor;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;
import kj.dph.lib.retrofit.http.HttpManager;
import kj.dph.lib.retrofit.listener.HttpOnNextListener;
import kj.dph.com.R;
import kj.dph.com.common.Constants;
import kj.dph.com.common.HttpCode;
import kj.dph.com.network.event.MsgEvent;
import kj.dph.com.util.InternetHelper;
import kj.dph.com.util.JsonUtils;
import kj.dph.com.util.StringUtil;
import kj.dph.com.util.logUtil.LogUtil;
import retrofit2.HttpException;
import retrofit2.Response;

public abstract class BaseFragment extends Fragment implements HttpOnNextListener {
    @BindColor(R.color.blue)
    public int blue;
    @BindColor(R.color.red)
    public int red;
    @BindColor(R.color.green)
    public int green;
    @BindColor(R.color.yellow)
    public int yellow;
    protected Activity _context;
    protected Intent intent;
    public Map<String, String> map;
    protected HttpManager httpManager;
    private Unbinder unbind;
    private SweetAlertDialog pd;

    public BaseFragment() {
        super();
    }

    /**
     * 显示加载框-点击外部区域不可取消，点击返回键可以取消
     */
    protected void showProgressDialog(String dialogMsg) {
        Context context = getActivity();
        if (pd == null && context != null) {
            pd = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);//这是进度style
            if (dialogMsg == null || "".equals(dialogMsg)) {
                pd.setTitleText("正在加载中...");
            } else {
                pd.setTitleText("" + dialogMsg);
            }
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);
        }
        if (!pd.isShowing()) {
            pd.show();
        }
    }

    /**
     * 显示加载框-无论何时都不鞥取消，必须等待任务完成-不建议使用
     */
    protected void showProgressDialog(String dialogMsg, String Force) {
        Context context = getActivity();
        if (pd == null && context != null) {
            pd = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);//这是进度style
            if (dialogMsg == null || "".equals(dialogMsg)) {
                pd.setTitleText("正在加载中...");
            } else {
                pd.setTitleText("" + dialogMsg);
            }
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);
        }
        if (!pd.isShowing()) {
            pd.show();
        }
    }

    /**
     * 显示加载框-默认点击返回键取消
     *
     * @param dialogMsg    内容
     * @param isCancelable 点击外部区域是否可以取消
     */
    protected void showProgressDialog(String dialogMsg, boolean isCancelable) {
        Context context = getActivity();
        if (pd == null && context != null) {
            pd = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);//这是进度style
            if (dialogMsg == null || "".equals(dialogMsg)) {
                pd.setTitleText("正在加载中...");
            } else {
                pd.setTitleText("" + dialogMsg);
            }
            pd.setCancelable(isCancelable);
            pd.setCanceledOnTouchOutside(isCancelable);
        }
        if (!pd.isShowing()) {
            pd.show();
        }
    }

    /**
     * 隐藏
     */
    protected void dismissProgressDialog() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(layoutId(), container, false);
        unbind = ButterKnife.bind(this, v);
        LogUtil.i(this.getClass().getSimpleName() + "onCreateView");
        httpManager = new HttpManager(this, (RxAppCompatActivity) getActivity());
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        InternetHelper.getNetworkIsConnected(getActivity(), 1);
        LogUtil.i("BaseActivity2016", getClass().getSimpleName());
        _context = getActivity();
        intent = new Intent();
        setCustomLayout(savedInstanceState);
        map = new HashMap<>();
        initView();
        initData();
        BindComponentEvent();
    }

    /**
     * 设定自定义布局
     */
    public void setCustomLayout(Bundle savedInstanceState) {

    }


    /**
     * 初始化界面
     */
    public void initView() {
    }

    /**
     * 绑定控件事件
     */
    public void BindComponentEvent() {
    }

    /**
     * 初始化数据
     */
    public void initData() {
    }

    /**
     * 在{@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}中调用，view中控件的初始化请在
     * {@link #onViewCreated(View, Bundle)}中进行
     *
     * @return fragment 的布局id
     */
    protected abstract int layoutId();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbind.unbind();
    }


    @Override
    public void onNext(String resulte, String mothead) {
        processSuccessResult(deliver(resulte), mothead);
    }

    /**
     * 过滤掉外层无用数据，仅返回data字段
     *
     * @param result
     * @return
     */
    public String deliver(String result) {
        if (!result.startsWith("{")) {
            return result;
        } else {
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

                if (containsMsg) {
                    if (containsData) {
                        String data = JsonUtils.getJsonString(jsonObject, "dat");
                        return data;
                    } else {
                        return result;
                    }
                } else {
                    return result;
                }
            } else {
                return result;
            }
        }

    }

    // 请求成功后处理
    protected void processSuccessResult(String resulte, String mothead) {
        if (Constants.IS_LOG_BODY) {
            LogUtil.i("processSuccessResult", mothead + "");
            LogUtil.i("processSuccessResult", resulte + "");
        }
    }


    @Override
    public void onError(Throwable e, String mothead) {
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
                processFalResult(code, bodyStr, mothead);
                return;
            }
        } else {
            //暂时做静默处理
//            EventBus.getDefault().post(new MsgEvent(e.toString()));
            LogUtil.i("解析过程onError", e.toString() + "");
            if (e.getMessage().equals(Constants.DPH_NO_NETWORK)) {
                processFalResult(-1, e.getMessage() + "", mothead);
            }

        }
    }

    // 请求失败后处理
    protected void processFalResult(int httpCode, String result, String mothead) {
        LogUtil.i("processFalResult", mothead + "");
        LogUtil.i("processFalResult", httpCode + "");
        LogUtil.i("processFalResult", result + "");
        switch (httpCode) {
            case HttpCode.HTTP_403:
                EventBus.getDefault().post(new MsgEvent(HttpCode.MSG_HTTP_403));
                break;
            case HttpCode.HTTP_404:
                EventBus.getDefault().post(new MsgEvent(HttpCode.MSG_HTTP_404));
                break;
            case HttpCode.HTTP_405:
                EventBus.getDefault().post(new MsgEvent(HttpCode.MSG_HTTP_405));
                break;
            case HttpCode.HTTP_408:
                EventBus.getDefault().post(new MsgEvent(HttpCode.MSG_HTTP_408));
                break;
            case HttpCode.HTTP_414:
                EventBus.getDefault().post(new MsgEvent(HttpCode.MSG_HTTP_414));
                break;
            case HttpCode.HTTP_500:
                EventBus.getDefault().post(new MsgEvent(HttpCode.MSG_HTTP_500));
                break;
            case HttpCode.HTTP_502:
                EventBus.getDefault().post(new MsgEvent(HttpCode.MSG_HTTP_502));
                break;
            case HttpCode.HTTP_503:
                EventBus.getDefault().post(new MsgEvent(HttpCode.MSG_HTTP_503));
                break;
            case HttpCode.HTTP_504:
                EventBus.getDefault().post(new MsgEvent(result));
                break;
            case HttpCode.HTTP_400:
                if (!StringUtil.isEmpty(result)) {
                    parseFailToast(result, true, mothead);
                }
                break;
            default:
                EventBus.getDefault().post(new MsgEvent(HttpCode.MSG_HTTP_ERROR));
                break;
        }
    }

    public static boolean parseFailToast(String result, boolean isNewApi, String mothead) {
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
            if (containsData) {
                dateStr = JsonUtils.getJsonString(jsonObject, "dat");
            }
            if (containsData && !TextUtils.isEmpty(dateStr)) {
                parseFailDat(jsonObject, mothead);
            } else {
                if (containsMsg) {
                    parseFailMsg(jsonObject);
                }
            }
            return containsMsg;
        }
        return false;
    }

    private static void parseFailDat(JSONObject jsonObject, String mothead) {
        try {
            JSONObject dateObj = jsonObject.getJSONObject("dat");
            Iterator<String> dat_it = dateObj.keys();
            while (dat_it.hasNext()) {
                //"dat":{"Logo":"请上传 店铺照片。","IDCardPicture":"请上传 身份证照片正反面。"}
                String key = dat_it.next().toString() + "";
                String keyValue = dateObj.getString(key);
                EventBus.getDefault().post(new MsgEvent(keyValue, mothead));
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
                EventBus.getDefault().post(new MsgEvent(resultStr));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * 任务处理完毕-默认不需要处理此方法，特殊情况例外
     *
     * @param mothead 当前请求接口
     */
    @Override
    public void onCompleted(String mothead) {

    }

}

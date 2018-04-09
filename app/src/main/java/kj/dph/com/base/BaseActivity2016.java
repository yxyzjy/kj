package kj.dph.com.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindColor;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import kj.dph.lib.retrofit.http.HttpManager;
import kj.dph.lib.retrofit.listener.HttpOnNextListener;
import kj.dph.lib.retrofit.utils.PermissionUtils;
import kj.dph.com.R;
import kj.dph.com.common.Constants;
import kj.dph.com.common.HttpCode;
import kj.dph.com.common.MainApplication;
import kj.dph.com.network.event.MsgEvent;
import kj.dph.com.util.InternetHelper;
import kj.dph.com.util.JsonUtils;
import kj.dph.com.util.StringUtil;
import kj.dph.com.util.Utils;
import kj.dph.com.util.logUtil.LogUtil;
import retrofit2.HttpException;
import retrofit2.Response;


/**
 * 基类--无title
 * 使用MVC框架，封装功能：
 * 3.基本方法调用流程（对setCustomLayout(savedInstanceState)，initView（）， initData()，BindComponentEvent()等方法调用流程进行封装）；
 * 4.网络请求（成功处理方法：processSuccessResult（）；失败处理方法：processFalResult（）；）；
 * 5.butterKnife注解（常用@BindView，@Color，@OnClick）；
 * 6.数据统计（友盟统计）；
 * <p>
 * 作者：wxw on 2016/7/15 15:58
 * 邮箱：1069289509@qq.com
 */
public class BaseActivity2016 extends RxAppCompatActivity implements HttpOnNextListener {
    protected final String TAG = getClass().getSimpleName();
    @BindColor(R.color.blue)
    public int blue;
    @BindColor(R.color.red)
    public int red;
    @BindColor(R.color.green)
    public int green;
    @BindColor(R.color.yellow)
    public int yellow;

    LinearLayout llTitleBarLeft, llEmptyContent;
    TextView tvTitleBarCenter, tvTitleRight;


    private View ll_empty_title_bar;
    private TitleRightListener titleRightListener;
    public HttpManager httpManager;
    private SweetAlertDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        InternetHelper.getNetworkIsConnected(BaseActivity2016.this, 1);
        // 判断当前界面对应的是哪一个活动
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setCustomLayout(savedInstanceState);
        MainApplication application = (MainApplication) this.getApplication();
        application.addActivity(this);
         /*初始化数据*/
        httpManager = new HttpManager(this, this);
        initView();
        initData();
        BindComponentEvent();
    }

    /**
     * 设置内容页面--请写在setCustomLayout()方法里
     *
     * @param layoutResID 内容页
     * @param titleStr    标题
     */
    public void setLayoutView(int layoutResID, String titleStr) {
        // TODO Auto-generated method stub
        View v = LayoutInflater.from(this).inflate(
                R.layout.activity_base_empty, null);
        initMyView(v);
        RelativeLayout.LayoutParams p1 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        p1.addRule(RelativeLayout.BELOW, v.getId());
        View contentView = LayoutInflater.from(this).inflate(layoutResID, null);
        llEmptyContent.addView(contentView, p1);
        setTopTitle(titleStr);
        setContentView(v);
        showContentView();
    }

    /**
     * 设置内容页面--黄色主题
     *
     * @param layoutResID        内容页
     * @param titleStr           标题
     * @param titleBarRighdes    右边按钮文字
     * @param rightCoclor        右边按钮文字颜色
     * @param titleRightListener 右边按钮点击监听
     */
    public void setLayoutView(int layoutResID, String titleStr,
                              String titleBarRighdes, int rightCoclor, TitleRightListener titleRightListener) {
        // TODO Auto-generated method stub
        setLayoutView(layoutResID, titleStr);
        if (TextUtils.isEmpty(titleBarRighdes)) {
            showTitleRight(false);
        } else {
            showTitleRight(true);
            tvTitleRight.setText(titleBarRighdes);
            tvTitleRight.setTextColor(getResources().getColor(rightCoclor));
        }
        this.titleRightListener = titleRightListener;
    }

    private void initMyView(View view) {
        llTitleBarLeft = view.findViewById(R.id.ll_title_bar_left);
        tvTitleBarCenter = view.findViewById(R.id.tv_title_bar_center);
        tvTitleRight = view.findViewById(R.id.tv_title_right);
        llEmptyContent = view.findViewById(R.id.ll_empty_content);
        ll_empty_title_bar = view.findViewById(R.id.ll_empty_title_bar);
    }

    @OnClick({R.id.ll_title_bar_left, R.id.ll_title_bar_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_title_bar_left:
                onBackPressed();
                break;
            case R.id.ll_title_bar_right:
                if (titleRightListener != null) {
                    titleRightListener.HandleTitleRight();
                }
                break;
        }
    }

    /**
     * 点击title右边按钮处理事件
     */
    public interface TitleRightListener {
        void HandleTitleRight();
    }

    /**
     * 设置title--默认为空
     *
     * @param titleStr
     */
    public void setTopTitle(String titleStr) {
        tvTitleBarCenter.setText(titleStr + "");
    }

    public void showTitleRight(boolean isShow) {
        if (isShow) {
            tvTitleRight.setVisibility(View.VISIBLE);
        } else {
            tvTitleRight.setVisibility(View.GONE);
        }
    }

    /**
     * 显示内容页面
     */
    public void showContentView() {
        llEmptyContent.setVisibility(View.VISIBLE);
    }

    /**
     * 返回右边文字布局
     *
     * @return
     */
    public TextView getTvTitleRight() {
        return tvTitleRight;
    }

    public void setRightTitle(String str) {
        if (!StringUtil.isEmpty(str)) {
            tvTitleRight.setText(str);
        }
    }

    public String getTopTitle() {
        String titleStr = tvTitleBarCenter.getText().toString().trim();
        if (TextUtils.isEmpty(titleStr)) {
            titleStr = "";
        }
        return titleStr;
    }

    /**
     * 是否显示title--默认显示
     *
     * @param isShow
     */
    public void showTitle(boolean isShow) {
        if (isShow) {
            ll_empty_title_bar.setVisibility(View.VISIBLE);
        } else {
            ll_empty_title_bar.setVisibility(View.GONE);
        }
    }

    public void showLeftBack(boolean isShow) {
        if (isShow) {
            llTitleBarLeft.setVisibility(View.VISIBLE);
        } else {
            llTitleBarLeft.setVisibility(View.GONE);
        }
    }

    /**
     * 设定自定义布局
     */
    public void setCustomLayout(Bundle savedInstanceState) {
    }

    /**
     * 初始化数据
     */
    public void initData() {
    }

    /**
     * 绑定控件事件
     */
    public void BindComponentEvent() {
    }

    /**
     * 初始化界面
     */
    public void initView() {
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ButterKnife.bind(this);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        ButterKnife.bind(this);
    }

    /**
     * 设置状态栏颜色，默认跟随系统
     *
     * @param resColorId 资源颜色id，例如R.color.red
     */
    public void setStatusBarColor(int resColorId) {
        Utils.setStatusBarColor(this, resColorId);
    }

    protected Context getContext() {
        return this;
    }

    /**
     * 显示加载框-点击外部区域不可取消，点击返回键可以取消
     */
    protected void showProgressDialog(String dialogMsg) {
        Context context = this;
        if (pd == null && context != null) {
            pd = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);//这是进度style
            if (dialogMsg == null || "".equals(dialogMsg)) {
                pd.setTitleText("正在加载中...");
            } else {
                pd.setTitleText("" + dialogMsg);
            }
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(true);
        }
        if (!pd.isShowing()) {
            pd.show();
        }
    }

    /**
     * 显示加载框-无论何时都不鞥取消，必须等待任务完成-不建议使用
     */
    protected void showProgressDialog(String dialogMsg, String Force) {
        Context context = this;
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
        Context context = this;
        if (pd == null && context != null) {
            pd = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);//这是进度style
            if (dialogMsg == null || "".equals(dialogMsg)) {
                pd.setTitleText("正在加载中...");
            } else {
                pd.setTitleText("" + dialogMsg);
            }
            pd.setCancelable(isCancelable);
            pd.setCanceledOnTouchOutside(true);
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

    public void back(View v) {
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /**
     * 判断触摸时间派发间隔
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (Utils.isFastDoubleClick()) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public void onNext(String resulte, String mothead) {
        processSuccessResult(Utils.deliver(resulte), mothead);
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
            LogUtil.i("解析过程onError", e.toString() + "");
            if (e.getMessage().equals(Constants.DPH_NO_NETWORK)) {
                processFalResult(-1, e.getMessage() + "", mothead);
            }

        }
    }

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

    public boolean parseFailToast(String result, boolean isNewApi, String mothead) {
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
            if (containsData && !StringUtil.isEmpty(dateStr)) {
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
        //{"msg":{"code":0,"desc":"“,”附近有语法错误。"},"dat":null}
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

    @Override
    public void onCompleted(String mothead) {

    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }
}

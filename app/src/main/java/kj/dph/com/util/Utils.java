package kj.dph.com.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.EditText;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.api.widget.Widget;
import com.yanzhenjie.durban.Controller;
import com.yanzhenjie.durban.Durban;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kj.dph.com.R;
import kj.dph.com.common.Constants;
import kj.dph.com.common.HttpCode;
import kj.dph.com.common.MainApplication;
import kj.dph.com.listener.AlbumImageListener;
import kj.dph.com.network.event.MsgEvent;
import kj.dph.com.util.logUtil.LogUtil;
import okhttp3.logging.HttpLoggingInterceptor;


/**
 * 字面意思为普通工具类，但目前已演化为工具类和通用逻辑的聚合地
 */
public class Utils {

    /**
     * 判断是否是快速点击
     */
    private static long lastClickTime;
    private static Activity mActivity;

    /**
     * 禁止输入 空格
     *
     * @param editText
     */
    public static void setEditTextInhibitInputSpace(EditText editText) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" ")) {
                    return "";
                } else return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }


    public static boolean isFastLog(@NonNull Activity activity) {
        if (activity != null) {
            if (mActivity != activity) {
                mActivity = activity;
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < Constants.FAST_DOUBLE_TIME) {
            LogUtil.i("isFastDoubleClick", "FAST_DOUBLE_TIME=" + timeD);
            return true;
        }
        lastClickTime = time;
        return false;

    }

    /**
     * 退出登录逻辑处理
     *
     * @param activity
     * @param isNeedLogin 是否需要打开登录页面
     */
    public static void doLogout(Activity activity, boolean isNeedLogin, boolean is401) {
        LogUtil.i("doLogout", "======退出登录======" + activity);
        LogUtil.i("isNeedLogin", isNeedLogin);
        LogUtil.i("is401", is401);
        if (is401) {
            EventBus.getDefault().post(new MsgEvent("" + HttpCode.MSG_HTTP_401_WITHOUT_TOKEN));
            SPUtil.clear(activity);
            if (isNeedLogin) {
//                LoginActivity.actionStart(activity, false);
            }
            MainApplication.finishActivity(isNeedLogin);
        } else {
            SPUtil.clear(activity);
            if (isNeedLogin) {
//                LoginActivity.actionStart(activity, false);
            }
            MainApplication.finishActivity(isNeedLogin);
        }
    }


    /**
     * 获取InputStream中字符串内容的方法
     *
     * @param inputStream
     * @return
     */
    public static String getString(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "gbk");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 手机号隐藏，显示前三后四
     *
     * @param phone 要隐藏的手机号码
     * @return
     */
    public static String PhoneHide(String phone) {
        if (phone.length() > 0) {
            String phoneStr = phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4, phone.length());
            return phoneStr;
        } else {
            return "";
        }
    }

    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 隐藏系统软键盘
     */
    public static void hideSystemSoftInput(Activity activity) {
        ((InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(activity.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 显示系统软键盘
     */
    public static void showSystemSoftInput(Activity activity, EditText editTextMessage) {
        // TODO Auto-generated method stub
        editTextMessage.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editTextMessage, 0);
    }

    /**
     * 获取总页数
     *
     * @param dataCount 数据总数
     * @param TakeCount 每次获取的数额
     * @return
     */
    public static int getPageCount(int dataCount, int TakeCount) {
        // TODO Auto-generated method stub
        int int_part = dataCount / TakeCount;
        int double_part = dataCount % TakeCount == 0 ? 0 : 1;
        return int_part + double_part;
    }

    /**
     * 获取忽略条目
     *
     * @param TakeCount  每次获取的数额
     * @param pageNumber 页码
     * @return
     */
    public static int getSkipCount(int TakeCount, int pageNumber) {
        // TODO Auto-generated method stub
        int skipCount = TakeCount * (pageNumber - 1);
        return skipCount;
    }

    /**
     * 日志打印拦截器
     *
     * @return
     */
    public static HttpLoggingInterceptor getHttpLoggingInterceptor() {
        //日志打印初始化
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                //打印retrofit日志
                LogUtil.d("请求体***返回体", "元数据 = " + message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    /**
     * 过滤掉外层无用数据，仅返回data字段
     *
     * @param result
     * @return
     */
    public static String deliver(String result) {
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
                        if (data == null) {
                            data = "";
                            return data;
                        } else {
                            if (data.startsWith("[")) {
                                return result;
                            } else {
                                return data;
                            }
                        }
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

    /**
     * 获取手机硬件信息
     *
     * @return
     */
    public static String getHandSetInfo() {
        String handSetInfo =
                "手机型号:" + Build.MODEL +
                        "\nSDK版本:" + Build.VERSION.SDK +
                        "\n系统版本:" + Build.VERSION.RELEASE +
                        "\n软件版本:" + getAppVersionName(MainApplication.appContext);
        return handSetInfo;
    }


    public static void parseFailToast(JSONObject obj) {
        // TODO Auto-generated method stub
        try {
            String resultStr = obj.getString("Message");
            if (resultStr != null) {
                ToastUtil.showShort(MainApplication.getInstance(), resultStr);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * double保存一位小数
     *
     * @return
     */
    public static String getOneString(double d) {
        String str = "0.0";
        if (d != 0) {
            DecimalFormat df = new DecimalFormat("#0.0");
            str = df.format(d) + "";
        }
        return str;

    }

    /**
     * double保存两位小数
     *
     * @return
     */
    public static String getDoubleString(String doubleStr) {
        double d = Double.parseDouble(doubleStr);
        String str = "0.00";
        if (d != 0) {
            DecimalFormat df = new DecimalFormat("#0.00");
            str = df.format(d) + "";
        }
        return str;

    }

    /**
     * 格式化东八区时间
     *
     * @param time
     * @return
     */
    public static String getChinaTime(String time) {
        // String str = "2015-10-22T16:14:20.8467678+08:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",
                Locale.CHINA);
        try {
            Date date = sdf.parse(time);
            return getDateToString(date, "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 格式化东八区时间的毫秒值
     *
     * @param time
     * @return
     */
    public static Long getChinaMillisecond(String time) {
        // String str = "2015-10-22T16:14:20.8467678+08:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",
                Locale.CHINA);
        try {
            long millisecond = sdf.parse(time).getTime();
            return millisecond;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0l;
    }

    /**
     * 格式化时间
     *
     * @param time 时间字符
     * @return
     */
    public static Date getDate(String time) {
        // String str = "2015-10-22T16:14:20.8467678+08:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",
                Locale.CHINA);
        Date date = new Date();
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取yyyy-MM-dd'T'HH:mm格式日期
     *
     * @param time
     * @return
     */
    public static String getTime(String time) {
        // String str = "2015-10-22T16:14:20.8467678+08:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",
                Locale.CHINA);
        try {
            Date date = sdf.parse(time);
            return getDateToString(date, "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 格式化东八区时间--年月日
     *
     * @param time
     * @return
     */
    public static String getChinaTimeNYRHM(String time) {
        // String str = "2015-10-22T16:14:20.8467678+08:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm",
                Locale.CHINA);
        try {
            Date date = sdf.parse(time);
            return getDateToString(date, "yyyy-MM-dd HH:mm");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 格式化东八区时间--年月日
     *
     * @param time
     * @return
     */
    public static String getChinaTimeNYR(String time) {
        String type = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
                Locale.CHINA);
        try {
            Date date = sdf.parse(time);
            return getDateToString(date, type);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 格式化东八区时间--年月日
     *
     * @param time
     * @return
     */
    public static String getChinaTimeNYRNotMust(String time) {
        String type = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",
                Locale.CHINA);
        Calendar c = Calendar.getInstance();//首先要获取日历对象
        int mYear = c.get(Calendar.YEAR); // 获取当前年份
        try {
            Date date = sdf.parse(time);
            String nyr = getDateToString(date, type);
            if (nyr.startsWith(mYear + "")) {
                return nyr.substring(5);
            } else {
                return nyr;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 格式化东八区时间--年月日
     *
     * @param time
     * @return
     */
    public static String getChinaTimeWeek(String time) {
        String type = "yyyy-MM-dd (EEEE) ";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
                Locale.CHINA);
        try {
            Date date = sdf.parse(time);
            return getDateToString(date, type);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 日期转换为字符串格式
     *
     * @param date     Date类型
     * @param sFormate : "yyyy-MM-dd HH:mm:ss"
     * @return 字符串
     */
    public static String getDateToString(Date date, String sFormate) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(sFormate);
        if (date == null) {
            return "";
        } else {
            return dateFormat.format(date);
        }
    }

    /**
     * double保存两位小数
     *
     * @return
     */
    public static String getDoubleString(double d) {
        String str = "0.00";
        if (d != 0) {
            DecimalFormat df = new DecimalFormat("#0.00");
            str = df.format(d) + "";
        }
        return str;
    }

    /**
     * @param number
     * @return
     */
    public static String FormatMenoy(double number) {
        String m = null;
        DecimalFormat df = new DecimalFormat("0.00");
        m = df.format(number);
        return m;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    /**
     * 判断sdcard是否存在
     *
     * @return
     */
    public static boolean sdCardExist() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 验证手机号 联通号段:130/131/132/155/156/185/186/145/176；
     * 电信号段:133/153/180/181/189/177； 移动号段
     * 134/135/136/137/138/139/150/151/152/157
     * /158/159/182/183/184/187/188/147/178
     *
     * @param phone
     * @return
     */
    public static boolean isPhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        } else {
            String regExp = "^[1]([3][0-9]{1}|59|58|88|89|87|76|56|86|85|45|83|80|81|77|82|84|50|51|52|53|55|57|47|78)[0-9]{8}$";
            Pattern p = Pattern.compile(regExp);
            Matcher m = p.matcher(phone);
            return m.find();
        }
    }

    public static boolean isPwd(String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            return false;
        } else {
            if (isContainChinese(pwd)) {//如果包括汉字 就返回
                return false;
            }
            if (pwd.length() < 6 || pwd.length() > 16) {
                return false;
            }
            return true;
        }
    }

    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 返回版本号（String）
     *
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageInfo pinfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            versionName = pinfo.versionName;
        } catch (Exception e) {
        }
        return versionName;
    }

    /**
     * 背景shape
     *
     * @param solidcolor  填充色
     * @param roundRadius 如果传一个值是4个都带角度  ,要么传8个值,两两一组,左上右上右下左下
     * @return
     */
    public static Drawable makeShape(String solidcolor, float... roundRadius) {
        int strokeWidth = 3; //  边框宽度
        //  int roundRadius = 3; //  圆角半径
        int fillColor = Color.parseColor(solidcolor);//内部填充颜色
//        int strokeColor = Color.parseColor(strokcolor);//边框颜色
        GradientDrawable gd = new GradientDrawable();//创建drawable
        gd.setColor(fillColor);
        if (roundRadius.length > 1) {
            gd.setCornerRadii(roundRadius);
        } else {
            gd.setCornerRadius(roundRadius[0]);
        }
        return gd;
    }

    /**
     * 代码创建状态选择器
     *
     * @param pressedDrawable
     * @param normalDrawable
     * @return
     */
    public static StateListDrawable createStateListDrawable(Drawable pressedDrawable, Drawable normalDrawable) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
        stateListDrawable.addState(new int[]{}, normalDrawable);
        return stateListDrawable;
    }

    /**
     * 设置状态栏颜色，默认跟随系统
     *
     * @param resColorId 资源颜色id，例如R.color.red
     */
    public static void setStatusBarColor(Activity activity, int resColorId) {
        StatusBarCompat.setStatusBarColor(activity, ContextCompat.getColor(activity, resColorId));
    }




    /**
     * 用字符串生成二维码
     *
     * @param text
     * @return
     * @throws WriterException
     */
    public static Bitmap create2DCode(String text) {
        //生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 0);
        BitMatrix matrix = null;
        try {
            matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 500, 500, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        //二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                } else {
                    pixels[y * width + x] = 0xffffffff;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        //通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }


    /**
     * 返回true 表示可以使用  返回false表示不可以使用
     */
    public static boolean cameraIsCanUse() {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters(); //针对魅族手机
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            isCanUse = false;
        }

        if (mCamera != null) {
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
                return isCanUse;
            }
        }
        return isCanUse;
    }

    /**
     * 调用拨号界面
     *
     * @param phone 电话号码
     */
    public static void call(Context context, String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    /**
     * 检查系统应用程序，并打开
     */
    public static String isContains(Context context, String pkgName) {
        //应用过滤条件
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager mPackageManager = context.getPackageManager();
        List<ResolveInfo> mAllApps = mPackageManager.queryIntentActivities(mainIntent, 0);
        //按包名排序
        Collections.sort(mAllApps, new ResolveInfo.DisplayNameComparator(mPackageManager));

        for (ResolveInfo res : mAllApps) {
            //该应用的包名和主Activity
            String pkg = res.activityInfo.packageName;
            String cls = res.activityInfo.name;
            if (pkg.contains(pkgName)) {
                return cls;
            }
        }
        return "";
    }


    /**
     * 解决图片太大不能适配屏幕
     *
     * @param htmltext
     * @return
     */
    public static String getHtmlNewContent(String htmltext) {
        Document doc = Jsoup.parse(htmltext);
        Elements elements = doc.getElementsByTag("img");
        for (Element element : elements) {
            element.attr("width", "100%").attr("height", "auto");
        }
        return doc.toString();
    }


    /**
     * 选择图片时-指定Toolbar的颜色
     */
    public static int toolBarColor() {
        return ContextCompat.getColor(MainApplication.getAppContext(), R.color.colorAccent);
    }

    /**
     * 选择图片时-指定状态栏的颜色
     */
    public static int stateBarColor() {
        return ContextCompat.getColor(MainApplication.getAppContext(), R.color.black);
    }

    /**
     * web中处理返回键
     *
     * @param webview
     * @return
     */
    public static boolean handlerWebBack(final WebView webview, Activity activity) {
        if (!InternetHelper.getNetworkIsConnected(activity, 0)) {
            activity.finish();
            return true;
        }
        if (webview != null) {
            webview.post(new Runnable() {
                @Override
                public void run() {
                    // Android版本变量
                    final int version = Build.VERSION.SDK_INT;
                    // 因为该方法在 Android 4.4 版本才可使用，所以使用时需进行版本判断
                    if (version < 18) {
                        webview.loadUrl("javascript:goBack()");
                    } else {
                        webview.evaluateJavascript("javascript:goBack()", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                //此处为 js 返回的结果
                                LogUtil.i("onReceiveValue", value);
                            }
                        });
                    }
                }
            });
            return true;
        }
        return false;
    }

    /**
     * 图片选择封装器
     *
     * @param context            上下文
     * @param requestCode        请求码
     * @param maxSelectCount     最大选择数量
     * @param albumImageListener 回调监听
     */
    public static void albumImage(Context context, int requestCode, int maxSelectCount, final AlbumImageListener albumImageListener) {
        Album.image(context) // 选择图片。
                .multipleChoice()
                .requestCode(requestCode)
                .camera(true)
                .columnCount(2)
                .selectCount(maxSelectCount)
                .checkedList(null)
                .filterSize(null)
                .filterMimeType(null)
                .afterFilterVisibility(true) // 显示被过滤掉的文件，但它们是不可用的。
                .widget(Widget.newDarkBuilder(context)
                        .title("选择图片") // 标题。
                        .statusBarColor(Utils.toolBarColor()) // 状态栏颜色。
                        .toolBarColor(Utils.toolBarColor()) // Toolbar颜色。
                        .navigationBarColor(Color.WHITE) // Android5.0+的虚拟导航栏颜色。
                        .mediaItemCheckSelector(Color.WHITE, Color.BLUE) // 图片或者视频选择框的选择器。
                        .bucketItemCheckSelector(Utils.toolBarColor(), Color.BLUE) // 切换文件夹时文件夹选择框的选择器。
                        .buttonStyle( // 用来配置当没有发现图片/视频时的拍照按钮和录视频按钮的风格。
                                Widget.ButtonStyle.newLightBuilder(context) // 同Widget的Builder模式。
                                        .setButtonSelector(Color.WHITE, Color.WHITE) // 按钮的选择器。
                                        .build()
                        )
                        .build())
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(int requestCode, @NonNull ArrayList<AlbumFile> result) {
                        ArrayList<String> parseResult = new ArrayList<>();
                        for (AlbumFile albumFile : result) {
                            parseResult.add(albumFile.getPath());
                        }
                        LogUtil.i("onResult()", requestCode);
                        LogUtil.i("onResult()", parseResult);
                        albumImageListener.onResult(requestCode, parseResult);
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(int requestCode, @NonNull String result) {
                        LogUtil.i("onCancel()", requestCode);
                        LogUtil.i("onCancel()", result);
                        albumImageListener.onCancel(requestCode, result);
                    }
                })
                .start();
    }

    /**
     * 图片裁剪封装（使用Durban插件）
     * <p>
     * 1.裁剪结果请在onActivityResult（）方法中接收处理，可参考MyUserInfoActivity集成使用
     * 2.目前流程是先去图库选择或者拍照，然后跳转界面裁剪，如有其它需求，可通知本人另行修改，谢谢配合
     *
     * @param activity     上下文
     * @param reqCode      请求码
     * @param maxCropCount 最大裁剪数量
     */
    public static void cropImage(final Activity activity, final int reqCode, int maxCropCount) {
        albumImage(activity,
                666,
                maxCropCount,
                new AlbumImageListener() {
                    @Override
                    public void onResult(int requestCode, @NonNull ArrayList<String> result) {
                        Durban.with(activity)
                                // 裁剪界面的标题。
                                .title("裁剪图片")
                                .statusBarColor(Utils.toolBarColor())
                                .toolBarColor(Utils.toolBarColor())
                                .navigationBarColor(Utils.toolBarColor())
                                // 图片路径list或者数组。
                                .inputImagePaths(result)
                                // 图片输出文件夹路径。
                                .outputDirectory(Constants.PATH_SDCARD_CROP_PIC)
                                // 裁剪图片输出的最大宽高。
                                .maxWidthHeight(500, 500)
                                // 裁剪时的宽高比。
                                .aspectRatio(1, 1)
                                // 图片压缩格式：JPEG、PNG。
                                .compressFormat(Durban.COMPRESS_JPEG)
                                // 图片压缩质量，请参考：Bitmap#compress(Bitmap.CompressFormat, int, OutputStream)
                                .compressQuality(90)
                                // 裁剪时的手势支持：ROTATE, SCALE, ALL, NONE.
                                .gesture(Durban.GESTURE_ALL)
                                .controller(
                                        Controller.newBuilder()
                                                .enable(false) // 是否开启控制面板。
                                                .rotation(true) // 是否有旋转按钮。
                                                .rotationTitle(true) // 旋转控制按钮上面的标题。
                                                .scale(true) // 是否有缩放按钮。
                                                .scaleTitle(true) // 缩放控制按钮上面的标题。
                                                .build()) // 创建控制面板配置。
                                .requestCode(reqCode)
                                .start();
                    }

                    @Override
                    public void onCancel(int requestCode, @NonNull String result) {

                    }
                });
    }


}

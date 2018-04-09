package kj.dph.com.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import kj.dph.com.R;
import kj.dph.com.common.MainApplication;


/**
 * Created by zhangyong on 2015/6/25. Toast工具类, 防止多个toast重叠
 */
public class ToastUtil {
    private static Toast toast;

    public static void showShort(Context context, CharSequence message) {
        if (null == toast) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

    /**
     * 无数据时统一处理显示
     */
    public static void showNoData() {
        showShort(MainApplication.getAppContext().getResources()
                .getString(R.string.no_data));
    }

    /**
     * 列表数据加载完时统一处理显示
     */
    public static void showNoMoreData() {
        showShort(MainApplication.getAppContext().getResources()
                .getString(R.string.already_no_earth));
    }

    public static void showShort(CharSequence message) {
        showShort(MainApplication.getAppContext(), message);
    }

    public static String mMothod = "";
    public static String mMessage = "";

    /**
     * 错误信息提示-单独定义
     *
     * @param message
     * @param mothod
     */
    public static void showLong(String message, String mothod) {
        if (TextUtils.isEmpty(mMothod)) {
            if (null == toast) {
                toast = Toast.makeText(MainApplication.getAppContext(), message,
                        Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                toast.setText(message);
            }
            toast.show();
        } else {
            if (mMothod.equals(mothod)) {
                if (mMessage.equals(message)) {

                } else {
                    toast = Toast.makeText(MainApplication.getAppContext(), message,
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.setText(message);
                    toast.show();
                }
            } else {
                toast = Toast.makeText(MainApplication.getAppContext(), message,
                        Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setText(message);
                toast.show();
            }
        }
        mMothod = mothod;
        mMessage = message;
    }

    public static void showLong(Context context, CharSequence message) {
        if (null == toast) {
            toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

    public static void showLong(CharSequence message) {
        showLong(MainApplication.getAppContext(), message);
    }

    public static void showLong(Object obj) {
        showLong(MainApplication.getAppContext(), obj + "");
    }

    public static void hideToast() {
        if (null != toast) {
            toast.cancel();
        }
    }

    public static void showTest(Context context, String s) {
        showLong(context, s);
    }

    /*public static void showLongWithImage(String message) {
        showToastWithImage(MainApplication.getAppContext(), message, R.mipmap.iv_dph_alert, Toast.LENGTH_LONG);
    }

    public static void showShortWithImage(String message) {
        showToastWithImage(MainApplication.getAppContext(), message, R.mipmap.iv_dph_alert, Toast.LENGTH_SHORT);
    }*/

   /* public static void showToastWithImage(Context context, String message, int imageResId, int time) {
        //加载Toast布局
        View toastRoot = LayoutInflater.from(context).inflate(R.layout.dph_toast, null);
        //初始化布局控件
        TextView mTextView = (TextView) toastRoot.findViewById(R.id.message);
        ImageView mImageView = (ImageView) toastRoot.findViewById(R.id.imageView);
        //为控件设置属性
        mTextView.setText(message);
        if (imageResId != 0) {
            mImageView.setImageResource(imageResId);
        }
        //Toast的初始化
        Toast toastStart = new Toast(context);
        //获取屏幕高度
        toastStart.setGravity(Gravity.CENTER, 0, 0);
        toastStart.setDuration(time);
        toastStart.setView(toastRoot);
        toastStart.show();
    }*/


}

package kj.dph.com.common;

import android.os.Environment;

import java.io.File;

import kj.dph.com.util.SettingUtil;
import kj.dph.com.util.logUtil.LogUtil;

/**
 * 常量类
 *
 * @author seeker
 */
public class Constants {
    //*************************************************一级开关**********************************************************
    /**
     * 是否需要在设置界面更改环境 -----true：测试时 false：发布时-----全局环境变量
     */
    public static final boolean IS_NEED = true;
    /**
     * 按钮快速点击监听时间（毫秒值）
     */
    public static final long FAST_DOUBLE_TIME = 365;
    /**
     * 是否打印请求报文&返回报文(抓包使用，默认开启)
     */
    public static final boolean IS_LOG_BODY = true;
    /**
     * 网络缓存时间-有网(单位s)
     */
    public static final int NETWORK_CATCH_TIME_YES = 1;//1s
    /**
     * 网络缓存时间-无网(单位s)
     */
    public static final int NETWORK_CATCH_TIME_NO = 1;//6小时(60 * 60 * 6)
    /**
     * 无网络异常
     */
    public static final String DPH_NO_NETWORK = "dph-no-network";
    //*************************************************日志打印相关配置**********************************************************
    public static final int LOG_LEVEL = IS_NEED ? LogUtil.ERROR : LogUtil.NONE;
    //    public static final int LOG_LEVEL = IS_NEED ? LogUtil.ERROR : LogUtil.ERROR;
    public static final String LOG_SPLITE = "〄〄〄";
    public static final String LOG_SPLITE_LGY = LOG_SPLITE;
    public static final String LOG_SPLITE_YXY = "-->_<--0_0--";
    public static final String LOG_SPLITE_WLY = LOG_SPLITE;
    public static final String FLAG_END = "〄〄〄→";
    public static final String FLAG_END_YXY = "→";
    public static final String FLAG_END_LGY = FLAG_END;
    public static final String FLAG_END_WXW = FLAG_END;
    public static final String FLAG_END_WLY = FLAG_END;
    public static final String LOG_FLAG_LGY = "李桂云" + LOG_SPLITE_LGY;
    public static final String LOG_FLAG_YXY = "yxy" + LOG_SPLITE_YXY;
    public static final String LOG_FLAG_WLY = "王刘义" + LOG_SPLITE_WLY;
    public static final String LOG_FLAG_DEFAULT = LOG_SPLITE;
    //*************************************************二级开关**********************************************************
    /**
     * 环境开关-----true：内网 -------false：外网
     **/
    public static final boolean IS_DEBUG = IS_NEED && SettingUtil.getIsDebug();
    public static final String main_url_type = IS_DEBUG ? "http://" : "https://";//服务器网关类别
    public static final String main_url_new = IS_DEBUG ? "api.ysj-wm.com/" : "api.lzyszyg.com/";//服务器域名

    //************************************************常量模块***************************************************
    /**
     * 图片前缀
     */
    public static final String DPH_PIC_PATH = "http://image." + main_url_new + "/";
    public static final String DPH_PIC_PATH_ = "http://image." + main_url_new + "//";


    public static final String IS_FIRST_LOAD = "IS_FIRST_LOAD";
    public static final String IS_ONLY_LOGIN = "IS_ONLY_LOGIN";
    public static final String PHONE_NUMBER = "PHONE_NUMBER";//保存手机号

    public static final int pageCount = 20;


    public static final int REQUEST_CODE_SUCCESS = 100;
    public static final int REQUEST_CODE_WEB_PIC = 201;
    public static final int REQUEST_CODE_Contacts = 300;//联系人
    public static final int REQUEST_CODE_ChatRoom = 400;//直播

    /**
     * 强制更新
     */
    public final static int CLIEN_FORE = 1;

    // 版本信息
    public final static int TH_DOWN_SUCC = 10203; // 下载apk成功
    public final static int TH_DOWN_FAILD = 10204; // 下载apk失败
    public static final String BUNDLE_KEY_VER_APKURL = "BUNDLE_KEY_VER_APKURL";
    public static final String BUNDLE_KEY_VER_SAVEPATH = "BUNDLE_KEY_VER_SAVEPATH";
    public final static String MSG_UPDATE_APP_START = "开始下载";
    public final static String MSG_UPDATE_APP_NAME = "辅仁中医馆";
    // sd卡的绝对路径
    public final static String SDPATH = Environment
            .getExternalStorageDirectory().getAbsolutePath();
    public final static String PATH_SDCARD_APK = SDPATH + File.separator
            + "dph_wm_user" + File.separator + "install" + File.separator;
    public final static String PATH_SYS_APK = File.separator + "install"
            + File.separator;
    public final static String APK_NAME = "wm_user.apk";
    //*******************************裁剪图片路径
    public final static String PATH_SDCARD_CROP_PIC = SDPATH + File.separator
            + "dph_wm_user" + File.separator + "crop" + File.separator;
}

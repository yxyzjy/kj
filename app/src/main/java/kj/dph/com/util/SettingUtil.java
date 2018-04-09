package kj.dph.com.util;

/**
 * 项目名称： Dph.v1.Client.Merchant.Android
 * 类描述： describe
 * 创建人： wxw
 * GitHub:  https://github.com/huatianjiajia
 * 创建时间： 2017/3/25-13:38
 * 修改人：
 * 修改时间： time
 * 修改备注： describe
 */
public class SettingUtil {
    private static String is_debug = "is_debug";//是否为debug模式
    private static String is_debug_web = "is_debug_web";//是否为debug模式

    public static void setIsDebug(boolean isDebug) {
        SPUtil.put(is_debug,isDebug);
    }
    public static boolean getIsDebug() {
        return SPUtil.getBoolean(is_debug,true);
    }

    public static void setIsDebugWeb(boolean isDebug) {
        SPUtil.put(is_debug_web,isDebug);
    }
    public static boolean getIsDebugWeb() {
        return SPUtil.getBoolean(is_debug_web,false);
    }


}

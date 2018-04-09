package kj.dph.lib.retrofit.utils;

import android.content.Context;
import android.text.TextUtils;

import kj.dph.com.common.MainApplication;
import kj.dph.com.network.entity.response.LoginRes;
import kj.dph.com.util.SPUtil;


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
public class LoginUtil {
    private static String yzkUserToken = "yzkUserToken";//会话时间
    private static String yzkLoginBean = "yzkLoginBean";//会话时间


    public static void setLoginBean(Context context, LoginRes loginRes) {
        SPUtil.putBean(context,yzkLoginBean, loginRes);
        setYzkUserToken(loginRes.accessToken);
    }
    public static LoginRes getLoginBean() {
        return  SPUtil.getBean(MainApplication.getAppContext(), yzkLoginBean, LoginRes.class);
    }


    public static void setYzkUserToken(String UserToken) {
        SPUtil.put(yzkUserToken, UserToken);
    }


    public static String getYzkUserToken() {
        String tokenStr=SPUtil.getString(yzkUserToken, "");
        return "Bearer "+tokenStr;
    }
    public static boolean isTokenEmpty(){
        String tokenStr=SPUtil.getString(yzkUserToken, "");
        if(TextUtils.isEmpty(tokenStr)){
            return true;
        }else{
            return false;
        }
    }
}

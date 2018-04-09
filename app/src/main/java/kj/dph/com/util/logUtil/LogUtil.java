package kj.dph.com.util.logUtil;


import android.util.Log;

import kj.dph.com.common.Constants;


/**
 * Created by zhangyong on 2015/7/5.
 */
public class LogUtil {
	/** Log等级:上打印Log, 上线后改为此级别 */
	public static final int NONE = -1;
	public static final int VERBOSE = 0;
	public static final int DEBUG = 1;
	public static final int INFO = 2;
	public static final int WARN = 3;
	public static final int ERROR = 4;
	public static  String FLAG= Constants.LOG_FLAG_DEFAULT;
	public static  String FLAG_END=Constants.FLAG_END;
	public static void v(String tag, String content) {
		if (Constants.LOG_LEVEL >= VERBOSE) {
			Log.v(FLAG+tag+FLAG_END+"", content+"");
		}
	}

	public static void d(String tag, String content) {
		if (Constants.LOG_LEVEL >= DEBUG) {
			Log.d(FLAG+tag+FLAG_END+"", content+"");
		}
	}

	public static void i(String tag, String content) {
		if (Constants.LOG_LEVEL >= INFO) {
			Log.i(FLAG+tag+FLAG_END+"", content + "");
		}
	}

	public static void i(String tag, Object obj) {
		if (Constants.LOG_LEVEL >= INFO) {
			Log.i(FLAG+tag+FLAG_END+"", obj + "");
		}
	}

	public static void w(String tag, String content) {
		if (Constants.LOG_LEVEL >= WARN) {
			Log.w(FLAG+tag+FLAG_END+"", content+"");
		}
	}

	public static void e(String tag, String content) {
		if (Constants.LOG_LEVEL >= ERROR) {
			Log.e(FLAG+tag+FLAG_END+"", content+"");
		}
	}


	/**
	 * 打印日志全部内容
	 *
	 * 过于繁琐，咱不采用,特殊情况例外
	 * @param content
	 */
	public static void all(String tag,String content){
		if (Constants.LOG_LEVEL >= ERROR) {
//        我们采取分段打印日志的方法：当长度超过4000时，我们就来分段截取打印
			//剩余的字符串长度如果大于4000
			if (content.length() > 4000) {
				for (int i = 0; i < content.length(); i += 4000) {
					//当前截取的长度<总长度则继续截取最大的长度来打印
					if (i + 4000 < content.length()) {
						Log.i(FLAG+tag+i+FLAG_END, content.substring(i, i + 4000));
					} else {
						//当前截取的长度已经超过了总长度，则打印出剩下的全部信息
						Log.i(FLAG+tag+i+FLAG_END, content.substring(i, content.length()));
					}
				}
			} else {
				//直接打印
				Log.i("msg", content);
			}
		}
	}

	//***********************************以下方法适用于模糊打印，只输出详细信息，flag仅打印相关人员信息
	public static void v(String content) {
		v("",content);
	}

	public static void d( String content) {
		d("",content);
	}

	public static void i(String content) {
		if (Constants.LOG_LEVEL >= INFO) {
			Log.i(FLAG+FLAG_END+"", content + "");
		}
	}

	public static void i( Object obj) {
		i("",obj);
	}

	public static void w( String content) {
		w("",content);
	}

	public static void e(String content) {
		e("",content);
	}
	/**
	 * 打印日志全部内容
	 *
	 * 过于繁琐，咱不采用,特殊情况例外
	 * @param content
	 */
	public static void all(String content){
		all("",content);
	}
}
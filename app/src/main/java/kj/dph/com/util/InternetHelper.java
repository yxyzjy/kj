package kj.dph.com.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;



public class InternetHelper {

	/**
	 * 获取网络状态并弹出提示(需要添加权限: <uses-permission
	 * android:name="android.permission.ACCESS_NETWORK_STATE" />)
	 * 
	 * @param context
	 * @param dialogType
	 *            如果无网络连接弹出提示类型(0:不弹出提示, 1:弹出提示语, 2:弹出带设置网络引导的提示框)
	 * @return
	 */
	public static boolean getNetworkIsConnected(final Context context,
			int dialogType) {
		NetworkInfo info = null;
		try {
			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			info = manager.getActiveNetworkInfo();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (info != null && info.isAvailable()) {
			return true;
		}
		// 弹出提示
		switch (dialogType) {
		case 1:
			if (context != null) {
				ToastUtil.showLong(context,"请求失败！请检查网络！");
			}
			break;
		case 2:
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("设置网络")
					.setMessage("网络错误，请设置网络")
					.setNegativeButton("取消", null);
			builder.setPositiveButton("设置",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							context.startActivity(new Intent(
									android.provider.Settings.ACTION_SETTINGS));
						}
					});
			builder.show();
			break;
		}
		return false;
	}
}

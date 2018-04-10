package kj.dph.com.ui.service

import android.app.IntentService
import android.content.Intent
import android.os.SystemClock
import kj.dph.com.util.logUtil.LogUtilYxy

/**
 * Created by yxy on 2018/4/9 0009.
 */
class LocalIntentService : IntentService {
    constructor(name: String?) : super(name)


    override fun onCreate() {
        LogUtilYxy.e("service onCreate.")
        super.onCreate()

    }

    override fun onHandleIntent(p0: Intent?) {
        var action: String = p0!!.getStringExtra("task_action")
        LogUtilYxy.e("receive task:" + action)
        SystemClock.sleep(1000)
        if (action.equals("com.yxy.action.task1")) {
            LogUtilYxy.e("====task===" + action)
        }
    }

    override fun onDestroy() {
        LogUtilYxy.e("service destroyed.")
        super.onDestroy()
    }
}
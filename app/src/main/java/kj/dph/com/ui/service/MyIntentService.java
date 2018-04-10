package kj.dph.com.ui.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by yxy on 2018/4/10 0010.
 */

public class MyIntentService extends IntentService {

    public MyIntentService(){
        super("a");

    }

    public MyIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}

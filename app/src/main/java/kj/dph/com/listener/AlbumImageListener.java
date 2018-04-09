package kj.dph.com.listener;

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by ${lgy} on 2017/10/3014:47
 * 邮箱1343168198@qq.com
 * 描述： describe
 * 修改内容：
 * 压缩和上传图片监听
 */

public interface AlbumImageListener {

    /**
     * 图片选择-返回结果
     *
     * @param requestCode 请求码
     * @param result      图片集合
     */
    void onResult(int requestCode, @NonNull ArrayList<String> result);

    /**
     * 图片选择-取消
     *
     * @param requestCode 请求码
     * @param result      取消原因
     */
    void onCancel(int requestCode, @NonNull String result);
}

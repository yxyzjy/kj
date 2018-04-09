package kj.dph.com.network.entity.api.Base.dph;


import kj.dph.com.network.entity.api.Base.BaseApi;

import static kj.dph.com.network.HttpBaseUrl.DPH_DOCTOR_HOST_URL_USER;

/**
 * core--接口
 * 作者：wxw on 2016/11/30 17:00
 * 邮箱：1069289509@qq.com
 */
public abstract class BaseUserApi extends BaseApi {
    /**
     * 默认初始化需要给定回调和rx周期类
     * 可以额外设置请求设置加载框显示，回调等（可扩展）
     */
    public BaseUserApi() {
        setBaseUrl(DPH_DOCTOR_HOST_URL_USER.url());
        setShowProgress(true);
        setCache(false);
    }
}

package kj.dph.com.network;

import static kj.dph.com.common.Constants.main_url_new;
import static kj.dph.com.common.Constants.main_url_type;

public enum HttpBaseUrl {
    //--------------------------------------------------------------------商家-多普汇商家接口
    /**
     * Admin
     */
    DPH_DOCTOR_HOST_URL_ADMIN(main_url_type, main_url_new, "Admin/"),
    /**
     * Doctor
     */
    DPH_DOCTOR_HOST_URL_DOCTOR(main_url_type, main_url_new, "Doctor/"),
    /**
     * Extensions
     */
    DPH_DOCTOR_HOST_URL_EXTENSIONS(main_url_type, main_url_new, "Extensions/"),
    /**
     * Hospital
     */
    DPH_DOCTOR_HOST_URL_HOSPITAL(main_url_type, main_url_new, "Hospital/"),
    /**
     * User
     */
    DPH_DOCTOR_HOST_URL_USER(main_url_type, main_url_new, "User/"),
    /**
     * 高德地图
     */
    DPH_GAODE_HOST_URL_USER("http://", "restapi.amap.com/", "v3/"),
    ;
    private String url_type;
    private String host_port;
    private String module;

    HttpBaseUrl(String url_type, String host_port, String module) {
        this.url_type = url_type;
        this.host_port = host_port;
        this.module = module;
    }

    public String url_type() {
        return url_type;
    }

    public String host_port() {
        return host_port;
    }

    public String module() {
        return module;
    }

    public String url() {
        return url_type + host_port + module;
    }

}
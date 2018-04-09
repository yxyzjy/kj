package kj.dph.com.network.event;

/**
 * 作者：wxw on 2016/11/29 16:55
 * 邮箱：1069289509@qq.com
 */
public class HttpCodeEvent {
    private int code;
    private String Mothod;

    public HttpCodeEvent(int code,String Mothod) {
        this.code = code;
        this.Mothod = Mothod;
    }

    public String getMothod() {
        return Mothod;
    }

    public void setMothod(String mothod) {
        Mothod = mothod;
    }

    public String getCode() {
        String jsonStr="{\"code\":"+code+"}";
        return jsonStr;
    }

    public void setCode(int code) {
        this.code = code;
    }
}

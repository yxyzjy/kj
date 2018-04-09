package kj.dph.com.network.event;

/**
 * 作者：wxw on 2016/11/29 16:55
 * 邮箱：1069289509@qq.com
 */
public class MsgEvent {
    private String msg="";
    private String mothead="";

    public String getMothead() {
        return mothead;
    }

    public void setMothead(String mothead) {
        this.mothead = mothead;
    }

    public MsgEvent(String msg) {
        this.msg = msg;
    }

    public MsgEvent(String msg, String mothead) {
        this.msg = msg;
        this.mothead = mothead;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

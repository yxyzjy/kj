package kj.dph.com.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import kj.dph.com.R;
import kj.dph.com.view.CircularImage;

/**
 * Created by yxy on 2016/7/26 0026.
 */
public class DemoAdapter extends CommonAdapter<String> {

    private Intent intent = new Intent();
    private Context context;

    public DemoAdapter(Context context, int layoutId, List<String> datas) {
        super(context, layoutId, datas);
        this.context = context;
    }

    @Override
    protected void convert(ViewHolder holder, String bean, int i) {
        CircularImage iv_health_pic = holder.getView(R.id.iv_health_pic);
        TextView tv_hospitals_type = holder.getView(R.id.tv_hospitals_type);

        holder.setText(R.id.tv_health_jf, bean);
//        ImageLoaderProxy.getInstance().displayImage(context, bean.picture, iv_health_pic);
    }
}

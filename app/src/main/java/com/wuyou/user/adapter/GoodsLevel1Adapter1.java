package com.wuyou.user.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.gs.buluo.common.widget.recyclerHelper.BaseHolder;
import com.gs.buluo.common.widget.recyclerHelper.BaseQuickAdapter;
import com.wuyou.user.R;
import com.wuyou.user.data.remote.ServeSpecification;

import java.util.List;

/**
 * Created by hjn on 2016/11/18.
 */
public class GoodsLevel1Adapter1 extends BaseQuickAdapter<ServeSpecification, BaseHolder> {

    public GoodsLevel1Adapter1(int layoutResId, @Nullable List<ServeSpecification> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseHolder baseHolder, ServeSpecification goodsSpecification) {
        TextView textView = baseHolder.getView(R.id.goods_level_item_text);
        textView.setText(goodsSpecification.name);
        if (goodsSpecification.stock == 0) {
            textView.setBackgroundResource(R.drawable.gray_round_bg);
            textView.setTextColor(textView.getResources().getColor(R.color.white));
        } else if (baseHolder.getAdapterPosition()==selectedPos) {
            textView.setBackgroundResource(R.drawable.login_normal);
            textView.setTextColor(textView.getResources().getColor(R.color.white));
        } else {
            textView.setBackgroundResource(R.drawable.night_blue_border);
            textView.setTextColor(textView.getResources().getColor(R.color.night_blue));
        }
    }

    public int selectedPos = -1;

}

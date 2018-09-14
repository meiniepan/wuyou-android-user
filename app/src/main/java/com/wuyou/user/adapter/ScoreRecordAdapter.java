package com.wuyou.user.adapter;

import com.gs.buluo.common.widget.recyclerHelper.BaseHolder;
import com.gs.buluo.common.widget.recyclerHelper.BaseQuickAdapter;
import com.wuyou.user.R;
import com.wuyou.user.data.remote.ScoreRecordBean;

/**
 * Created by DELL on 2018/6/4.
 */

public class ScoreRecordAdapter extends BaseQuickAdapter<ScoreRecordBean, BaseHolder> {
    private int flag;

    public ScoreRecordAdapter(int flag) {
        super(R.layout.item_score_record);
        this.flag = flag;
    }

    @Override
    protected void convert(BaseHolder helper, ScoreRecordBean bean) {
        helper.setText(R.id.item_score_record_title, (translateSource(bean.source)))
                .setText(R.id.item_score_record_point, bean.points.replaceAll("EOS", "").split("\\.")[0])
                .setText(R.id.item_score_record_point_flag, flag == 0 ? "+" : "-")
                .setText(R.id.item_score_record_time, bean.created_at);
    }

    private int translateSource(String source) {
        if ("dailyrewords".equals(source)) {
            return R.string.app_sign;
        } else if ("actirewards".equals(source)) {
            return R.string.activity;
        } else {
            return R.string.system_rewards;
        }
    }
}

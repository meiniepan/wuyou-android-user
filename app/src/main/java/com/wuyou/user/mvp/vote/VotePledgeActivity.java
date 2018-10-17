package com.wuyou.user.mvp.vote;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.gs.buluo.common.network.BaseSubscriber;
import com.gs.buluo.common.utils.ToastUtils;
import com.wuyou.user.CarefreeDaoSession;
import com.wuyou.user.Constant;
import com.wuyou.user.R;
import com.wuyou.user.data.EoscDataManager;
import com.wuyou.user.util.CommonUtil;
import com.wuyou.user.util.RxUtil;
import com.wuyou.user.view.activity.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Solang on 2018/10/15.
 */

public class VotePledgeActivity extends BaseActivity {
    @BindView(R.id.tv_vote_pledge_score_num)
    TextView tvVotePledgeScoreNum;
    @BindView(R.id.et_vote_pledge_vote_num)
    EditText etVotePledgeVoteNum;
    private int scoreInt;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_vote_pledge;
    }

    @Override
    protected void bindView(Bundle savedInstanceState) {
        setTitleText(getString(R.string.vote));
    }


    @OnClick(R.id.tv_vote_pledge_confirm)
    public void onViewClicked() {
        int input = Integer.parseInt(etVotePledgeVoteNum.getText().toString());
        if (input>scoreInt){
            ToastUtils.ToastMessage(getCtx(),"请检查输入！");
            return;
        }
    }

    public void getAccountScore(String name) {
        showLoadingDialog();
        EoscDataManager.getIns().getCurrencyBalance(Constant.EOSIO_TOKEN_CONTRACT, name, "EOS").compose(RxUtil.switchSchedulers())
                .subscribe(new BaseSubscriber<JsonArray>() {
                    @Override
                    public void onSuccess(JsonArray eosAccountInfo) {
                        if (eosAccountInfo.size() > 0) {
                            String scoreAmount = eosAccountInfo.get(0).toString().replace("EOS", "").replaceAll("\"", "");
                            scoreInt = Integer.parseInt(scoreAmount);
                            tvVotePledgeScoreNum.setText(scoreInt + "");
                        }
                    }
                });
    }
}

package com.wuyou.user.view.widget.panel;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.alibaba.security.rp.RPSDK;
import com.google.gson.JsonObject;
import com.gs.buluo.common.network.BaseResponse;
import com.gs.buluo.common.network.BaseSubscriber;
import com.gs.buluo.common.network.ErrorBody;
import com.gs.buluo.common.network.QueryMapBuilder;
import com.gs.buluo.common.utils.AppManager;
import com.gs.buluo.common.utils.ToastUtils;
import com.gs.buluo.common.widget.LoadingDialog;
import com.gs.buluo.common.widget.recyclerHelper.BaseQuickAdapter;
import com.wuyou.user.CarefreeDaoSession;
import com.wuyou.user.R;
import com.wuyou.user.adapter.VolunteerPositionChooseAdapter;
import com.wuyou.user.data.EoscDataManager;
import com.wuyou.user.data.api.AuthTokenResponse;
import com.wuyou.user.data.api.VolunteerProjectBean;
import com.wuyou.user.mvp.volunteer.ApplySuccessActivity;
import com.wuyou.user.mvp.volunteer.VolunteerProDetailActivity;
import com.wuyou.user.network.CarefreeRetrofit;
import com.wuyou.user.network.apis.UserApis;
import com.wuyou.user.util.RxUtil;

import butterknife.ButterKnife;

/**
 * Created by Solang on 2018/10/30.
 */

public class PositionChoosePanel extends Dialog {
    VolunteerProjectBean data;
    String posName;

    Button kycButton;

    public PositionChoosePanel(Context context, VolunteerProjectBean data) {
        super(context, R.style.bottom_dialog);
        this.data = data;
        initView();
    }

    private void initView() {
        for (VolunteerProjectBean.PositionsBean e : data.positions
                ) {
            e.isChosen = false;
        }
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.position_choose_board, null);
        setContentView(rootView);
        ButterKnife.bind(this);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);

        RecyclerView recyclerView = rootView.findViewById(R.id.rv_position_panel);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        VolunteerPositionChooseAdapter adapter = new VolunteerPositionChooseAdapter(R.layout.item_position_panel);
        recyclerView.setAdapter(adapter);
        adapter.setNewData(data.positions);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                for (VolunteerProjectBean.PositionsBean e : data.positions
                        ) {
                    e.isChosen = false;
                }
                data.positions.get(i).isChosen = true;
                posName = data.positions.get(i).name;
                adapter.notifyDataSetChanged();
            }
        });
        kycButton = findViewById(R.id.tv_position_auth);
        if (data.kyc == 0) {
            kycButton.setVisibility(View.GONE);
        }
        kycButton.setOnClickListener(v -> navigateToTrace(data.name));
        findViewById(R.id.tv_position_apply).setOnClickListener(v -> participateVolunteerProject());
    }

    private void navigateToTrace(String name) {
        LoadingDialog.getInstance().show(getContext(), "", false);
        CarefreeRetrofit.getInstance().createApi(UserApis.class).getAuthToken(CarefreeDaoSession.getInstance().getUserId(), QueryMapBuilder.getIns().buildGet())
                .compose(RxUtil.switchSchedulers())
                .subscribe(new BaseSubscriber<BaseResponse<AuthTokenResponse>>() {
                    @Override
                    public void onSuccess(BaseResponse<AuthTokenResponse> authTokenResponse) {
                        RPSDK.start(authTokenResponse.data.token, getContext(), audit -> setAuthResult(audit));
                    }
                });
    }

    private boolean isKyc = false;

    private void setAuthResult(RPSDK.AUDIT audit) {
        if (audit == RPSDK.AUDIT.AUDIT_PASS) {//认证通过
            isKyc = true;
            kycButton.setEnabled(false);
            kycButton.setText(R.string.already_auth);
        } else if (audit == RPSDK.AUDIT.AUDIT_FAIL || audit == RPSDK.AUDIT.AUDIT_EXCEPTION) { //认证不通过
            ToastUtils.ToastMessage(getContext(), getContext().getString(R.string.auth_fail));
        } else if (audit == RPSDK.AUDIT.AUDIT_IN_AUDIT) { //认证中，通常不会出现，只有在认证审核系统内部出现超时，未在限定时间内返回认证结果时出现。此时提示用户系统处理中，稍后查看认证结果即可。
            ToastUtils.ToastMessage(getContext(), getContext().getString(R.string.auth_ing));
        }
    }

    public void participateVolunteerProject() {
        if (!isKyc && data.kyc == 1) {
            ToastUtils.ToastMessage(getContext(), "请先认证");
            return;
        }
        if (TextUtils.isEmpty(posName)) {
            ToastUtils.ToastMessage(getContext(), "请先选择岗位");
            return;
        }
        LoadingDialog.getInstance().show(getContext(), "", false);
        String id = data.id + "";
        String org = data.creator;
        String proName = data.name;
        EoscDataManager.getIns().participateTimeBank(id, org, proName, posName)
                .compose(RxUtil.switchSchedulers())
                .subscribe(new BaseSubscriber<JsonObject>() {
                    @Override
                    public void onSuccess(JsonObject jsonObject) {
                        getContext().startActivity(new Intent(getContext(), ApplySuccessActivity.class));
                        AppManager.getAppManager().finishActivity(VolunteerProDetailActivity.class);
                        dismiss();
                    }

                    @Override
                    protected void onNodeFail(int code, ErrorBody.DetailErrorBean message) {
                        if (message.message.contains("You have enrolled")) {
                            ToastUtils.ToastMessage(getContext(), "您已经报过名了");
                        } else if (message.message.contains("enrolled complete")) {
                            ToastUtils.ToastMessage(getContext(), "报名人数已满");
                        } else {
                            ToastUtils.ToastMessage(getContext(), message.message);
                        }
                        dismiss();
                    }
                });
    }
}

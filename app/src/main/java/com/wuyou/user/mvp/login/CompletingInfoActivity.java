package com.wuyou.user.mvp.login;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gs.buluo.common.network.BaseResponse;
import com.gs.buluo.common.network.BaseSubscriber;
import com.gs.buluo.common.network.QueryMapBuilder;
import com.gs.buluo.common.utils.ToastUtils;
import com.wuyou.user.CarefreeDaoSession;
import com.wuyou.user.R;
import com.wuyou.user.data.local.db.UserInfo;
import com.wuyou.user.network.CarefreeRetrofit;
import com.wuyou.user.network.apis.UserApis;
import com.wuyou.user.util.CommonUtil;
import com.wuyou.user.view.activity.BaseActivity;
import com.wuyou.user.view.activity.MainActivity;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hjn91 on 2018/2/2.
 */

public class CompletingInfoActivity extends BaseActivity {
    @BindView(R.id.complete_info_nickname)
    EditText completeInfoNickname;
    @BindView(R.id.complete_info_pwd)
    EditText completeInfoPwd;
    @BindView(R.id.complete_info_pwd_again)
    EditText completeInfoPwdAgain;
    @BindView(R.id.talk_later)
    TextView talkLater;

    @Override
    protected void bindView(Bundle savedInstanceState) {
        setTitleText(R.string.completing_info);
        talkLater.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_completing_info;
    }

    //点击确定
    public void setPwdInfo(View view) {
        if (completeInfoPwd.length() < 6) {
            ToastUtils.ToastMessage(getCtx(), "密码长度至少6位");
            return;
        }
        String pwd = completeInfoPwd.getText().toString();
        String pwdAgain = completeInfoPwdAgain.getText().toString();
        if (!TextUtils.equals(pwd, pwdAgain)) {
            ToastUtils.ToastMessage(getCtx(), "两次密码输入不一致");
            return;
        }

        UserInfo userInfo = CarefreeDaoSession.getInstance().getUserInfo();
        CarefreeRetrofit.getInstance().createApi(UserApis.class)
                .updatePwd(userInfo.getUid(),
                        QueryMapBuilder.getIns().put("password", CommonUtil.getMD5Str(pwd)).put("confirm_password", CommonUtil.getMD5Str(pwdAgain)).buildPost())
                .subscribeOn(Schedulers.io())
                .doOnNext(baseResponse -> {
                    userInfo.setPassword(pwd);
                    CarefreeDaoSession.getInstance().updateUserInfo(userInfo);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse baseResponse) {
                        jumpToMain();
                    }
                });
    }

    private void jumpToMain() {
        Intent view = new Intent(getCtx(), MainActivity.class);
        startActivity(view);
    }


    @OnClick(R.id.talk_later)
    public void onViewClicked() {
        jumpToMain();
    }
}

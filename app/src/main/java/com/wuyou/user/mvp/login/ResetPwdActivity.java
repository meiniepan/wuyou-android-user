package com.wuyou.user.mvp.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.gs.buluo.common.utils.ToastUtils;
import com.wuyou.user.CarefreeDaoSession;
import com.wuyou.user.R;
import com.wuyou.user.data.local.db.UserInfo;
import com.wuyou.user.util.CommonUtil;
import com.wuyou.user.view.activity.BaseActivity;
import com.wuyou.user.view.activity.MainActivity;

import butterknife.BindView;

/**
 * Created by hjn91 on 2018/2/2.
 */

public class ResetPwdActivity extends BaseActivity {
    @BindView(R.id.reset_pwd)
    EditText resetPwd;
    @BindView(R.id.reset_pwd_again)
    EditText resetPwdAgain;

    @Override
    protected void bindView(Bundle savedInstanceState) {
        setTitleText(R.string.reset_pwd);
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_reset_pwd;
    }

    public void resetPwd(View view) {
        if (resetPwd.length() < 6) {
            ToastUtils.ToastMessage(getCtx(), "密码长度至少6位");
            return;
        }
        String pwd = resetPwd.getText().toString();
        String pwdAgain = resetPwdAgain.getText().toString();
        if (!TextUtils.equals(pwd, pwdAgain)) {
            ToastUtils.ToastMessage(getCtx(), "两次密码输入不一致");
            return;
        }
        UserInfo userInfo = CarefreeDaoSession.getInstance().getUserInfo();
        userInfo.setPassword(CommonUtil.getMD5Str(pwd));
        CarefreeDaoSession.getInstance().updateUserInfo(userInfo);
        ToastUtils.ToastMessage(getCtx(), R.string.reset_pwd_success);
        Intent intent = new Intent(getCtx(), MainActivity.class);
        startActivity(intent);
    }
}

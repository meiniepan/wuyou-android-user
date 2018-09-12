package com.wuyou.user.mvp.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wuyou.user.CarefreeDaoSession;
import com.wuyou.user.R;
import com.wuyou.user.view.activity.BaseActivity;
import com.wuyou.user.view.activity.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Solang on 2018/9/11.
 */

public class ScoreAccountActivity extends BaseActivity {
    @BindView(R.id.iv_account_avatar)
    ImageView ivAccountAvatar;
    @BindView(R.id.tv_account_name)
    TextView tvAccountName;
    @BindView(R.id.ll_backup_pk)
    LinearLayout llBackupPk;
    @BindView(R.id.tv_obtain)
    TextView tvObtain;
    @BindView(R.id.tv_exchange)
    TextView tvExchange;
    @BindView(R.id.drawerL)
    DrawerLayout drawerLayout;
    @BindView(R.id.ll_above)
    LinearLayout layout;

    @Override
    protected void bindView(Bundle savedInstanceState) {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        WindowManager wm = this.getWindowManager();//获取屏幕宽高
        int width1 = wm.getDefaultDisplay().getWidth();
        int height1 = wm.getDefaultDisplay().getHeight();
        ViewGroup.LayoutParams para = layout.getLayoutParams();//获取drawerlayout的布局
        para.width = width1*4 / 7;//修改宽度
        para.height = height1;//修改高度
        layout.setLayoutParams(para); //设置修改后的布局。
        tvAccountName.setText(CarefreeDaoSession.getInstance().getMainAccount().getName());
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_score_account;
    }

    @OnClick({R.id.iv_more, R.id.ll_backup_pk, R.id.tv_obtain, R.id.tv_exchange, R.id.back_1, R.id.back_2, R.id.ll_import, R.id.ll_manager, R.id.ll_score})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_more:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.ll_backup_pk:
                break;
            case R.id.tv_obtain:
                break;
            case R.id.tv_exchange:
                break;
            case R.id.back_1:
                finish();
                break;
            case R.id.back_2:
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.ll_import:
                break;
            case R.id.ll_manager:
                startActivity(new Intent(getCtx(), ManagerAccountActivity.class));
                break;
            case R.id.ll_score:
                break;
        }
    }
}

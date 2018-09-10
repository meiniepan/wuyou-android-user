package com.wuyou.user.mvp.wallet;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import com.gs.buluo.common.network.BaseSubscriber;
import com.gs.buluo.common.utils.ToastUtils;
import com.wuyou.user.CarefreeApplication;
import com.wuyou.user.CarefreeDaoSession;
import com.wuyou.user.R;
import com.wuyou.user.util.CommonUtil;
import com.wuyou.user.util.RxUtil;
import com.wuyou.user.view.activity.BaseActivity;
import com.wuyou.user.view.activity.MainActivity;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;

/**
 * Created by DELL on 2018/9/10.
 */

public class BackupPKeyActivity extends BaseActivity {
    @BindView(R.id.backup_pk_text)
    TextView backupPkText;
    private String privateKey;

    @Override
    protected void bindView(Bundle savedInstanceState) {
        setTitleText(getString(R.string.backup_pk));
        privateKey = CarefreeDaoSession.getInstance().getMainAccount().getPrivateKey();
        backupPkText.setText(privateKey);
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_backup_pk;
    }

    @OnClick({R.id.backup_pk, R.id.save_pk, R.id.back_main})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backup_pk:
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm != null) cm.setPrimaryClip(ClipData.newPlainText(null, privateKey));
                ToastUtils.ToastMessage(getCtx(), getString(R.string.copy_success));
                break;
            case R.id.save_pk:
                showLoadingDialog();
                Observable.fromCallable(() -> {
                    Bitmap screenshot = CommonUtil.getScreenshot(getCtx(), getRootView());
                    String s = MediaStore.Images.Media.insertImage(getCtx().getContentResolver(), screenshot, "备份", "钱包备份图片");
                    return Uri.parse(s);
                }).compose(RxUtil.switchSchedulers()).subscribeWith(new BaseSubscriber<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        CarefreeApplication.getInstance().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                        ToastUtils.ToastMessage(getCtx(), R.string.save_success);
                    }
                });
                break;
            case R.id.back_main:
                Intent intent = new Intent(getCtx(), MainActivity.class);
                startActivity(intent);
                break;
        }
    }
}
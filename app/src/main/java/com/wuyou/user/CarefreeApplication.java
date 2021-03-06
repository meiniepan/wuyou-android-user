package com.wuyou.user;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.alibaba.security.rp.RPSDK;
import com.gs.buluo.common.BaseApplication;
import com.gs.buluo.common.network.ApiException;
import com.gs.buluo.common.network.BaseResponse;
import com.gs.buluo.common.network.BaseSubscriber;
import com.gs.buluo.common.network.QueryMapBuilder;
import com.gs.buluo.common.utils.SharePreferenceManager;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tendcloud.tenddata.TCAgent;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.wuyou.user.data.remote.UpdateEntity;
import com.wuyou.user.mvp.login.LoginActivity;
import com.wuyou.user.network.CarefreeRetrofit;
import com.wuyou.user.network.apis.UserApis;
import com.wuyou.user.view.activity.MainActivity;
import com.wuyou.user.view.activity.SettingActivity;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hjn on 2016/11/1.
 */
public class CarefreeApplication extends BaseApplication {
    private static CarefreeApplication instance;
    public long lastSignTime;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initTalkingData();
        initUrl();
        initBuglyUpgrade();
        RPSDK.initialize(this);
        ZXingLibrary.initDisplayOpinion(this);
    }

    private void initTalkingData() {
        TCAgent.LOG_ON = true;
        TCAgent.init(this, "674BE02AC6D6485DB2C4CB05C47C7AFB", "android");
    }

    private void initUrl() {
        String baseUrl = SharePreferenceManager.getInstance(this).getStringValue(Constant.SP_BASE_URL);
        if (!TextUtils.isEmpty(baseUrl)) Constant.BASE_URL = baseUrl;
        String webUrl = SharePreferenceManager.getInstance(this).getStringValue(Constant.SP_WEB_URL);
        if (!TextUtils.isEmpty(webUrl)) Constant.WEB_URL = webUrl;
        String chainUrl = SharePreferenceManager.getInstance(this).getStringValue(Constant.SP_CHAIN_URL);
        if (!TextUtils.isEmpty(chainUrl)) Constant.CHAIN_URL = chainUrl;
        String mongoUrl = SharePreferenceManager.getInstance(this).getStringValue(Constant.SP_MONGO_URL);
        if (!TextUtils.isEmpty(mongoUrl)) Constant.EOS_MONGO_DB = mongoUrl;
        String ipfs = SharePreferenceManager.getInstance(this).getStringValue(Constant.SP_IPFS_URL);
        if (!TextUtils.isEmpty(ipfs))Constant.IPFS_URL = ipfs;

        if (TextUtils.equals(baseUrl, Constant.ONLINE_BASE_URL)) {
            TCAgent.setReportUncaughtExceptions(true);
        } else {
            TCAgent.setReportUncaughtExceptions(false);
        }
    }

    private void initBuglyUpgrade() {
        Beta.upgradeDialogLayoutId = R.layout.upgrade_dialog;
        Beta.canShowUpgradeActs.add(MainActivity.class);
        Beta.canShowUpgradeActs.add(LoginActivity.class);
        Beta.canShowUpgradeActs.add(SettingActivity.class);
        Beta.autoDownloadOnWifi = true;
        Bugly.init(getApplicationContext(), "ba97fc54df", false);
    }


    public static synchronized CarefreeApplication getInstance() {
        return instance;
    }

    @Override
    public void onInitialize() {

    }

    @Override
    public String getFilePath() {
        return getFilesDir().getAbsolutePath();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void ManualCheckOnForceUpdate() {
        CarefreeRetrofit.getInstance().createApi(UserApis.class)
                .checkUpdate(QueryMapBuilder.getIns().put("version", getVersionCode() + "")
                        .put("platform", "android")
                        .buildGet())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseResponse<UpdateEntity>>() {
                    @Override
                    public void onSuccess(BaseResponse<UpdateEntity> response) {
                        if (2 == response.data.update) {
                            Beta.checkUpgrade(false, true);
                        }
                    }

                    @Override
                    protected void onFail(ApiException e) {
                    }
                });
    }

    public int getVersionCode() {
        PackageManager manager;

        PackageInfo info = null;

        manager = this.getPackageManager();
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

}

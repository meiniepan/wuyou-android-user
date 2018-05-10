package com.wuyou.user.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gs.buluo.common.network.BaseResponse;
import com.gs.buluo.common.network.BaseSubscriber;
import com.gs.buluo.common.network.QueryMapBuilder;
import com.gs.buluo.common.utils.ToastUtils;
import com.gs.buluo.common.utils.TribeDateUtils;
import com.wuyou.user.CarefreeDaoSession;
import com.wuyou.user.Constant;
import com.wuyou.user.R;
import com.wuyou.user.bean.UserInfo;
import com.wuyou.user.network.CarefreeRetrofit;
import com.wuyou.user.network.apis.UserApis;
import com.wuyou.user.util.RxUtil;
import com.wuyou.user.util.glide.Glide4Engine;
import com.wuyou.user.util.glide.GlideUtils;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.util.ConvertUtils;

/**
 * Created by DELL on 2018/3/14.
 */

public class InfoActivity extends BaseActivity {
    @BindView(R.id.info_head)
    ImageView infoHead;
    @BindView(R.id.tv_account_area)
    TextView tvAccountArea;
    @BindView(R.id.tv_phone_area)
    TextView tvPhoneArea;
    @BindView(R.id.tv_email_area)
    TextView tvEmailArea;
    @BindView(R.id.tv_sex_area)
    TextView tvSexArea;
    @BindView(R.id.tv_birthday_area)
    TextView tvBirthdayArea;

    private Uri imagePath;

    @Override
    protected void bindView(Bundle savedInstanceState) {
        UserInfo userInfo = CarefreeDaoSession.getInstance().getUserInfo();
        GlideUtils.loadImage(this, userInfo.getAvatar(), infoHead, true);
        tvPhoneArea.setText(userInfo.getMobile());
        showLoadingDialog();
        CarefreeRetrofit.getInstance().createApi(UserApis.class)
                .getUserInfo(CarefreeDaoSession.getInstance().getUserId(), QueryMapBuilder.getIns().buildGet())
                .compose(RxUtil.switchSchedulers())
                .subscribe(new BaseSubscriber<BaseResponse<UserInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<UserInfo> userInfoBaseResponse) {
                        setUserData(userInfoBaseResponse.data);
                    }
                });
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_user_info;
    }


    @OnClick({R.id.info_account_area, R.id.info_phone_area, R.id.info_email_area, R.id.info_sex_area, R.id.info_birthday_area, R.id.info_head})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.info_head:
                ToastUtils.ToastMessage(getCtx(), R.string.no_function);
//                chosePhoto();
                break;
            case R.id.info_account_area:
                intent.setClass(getCtx(), ModifyNickActivity.class);
                startActivityForResult(intent.putExtra(Constant.FROM, Constant.NICK), Constant.Intent.REQUEST_NICK);
                break;
            case R.id.info_phone_area:
                intent.setClass(getCtx(), ModifyPhoneActivity.class);
                startActivityForResult(intent.putExtra(Constant.FROM, Constant.PHONE), Constant.Intent.REQUEST_PHONE);
                break;
            case R.id.info_email_area:
                intent.setClass(getCtx(), ModifyNickActivity.class);
                startActivityForResult(intent.putExtra(Constant.FROM, Constant.EMAIL), Constant.Intent.REQUEST_EMAIL);
                break;
            case R.id.info_sex_area:
                intent.setClass(getCtx(), ModifyGenderActivity.class);
                startActivityForResult(intent, Constant.Intent.REQUEST_GENDER);
                break;
            case R.id.info_birthday_area:
                chooseBirthday();
                break;
        }
    }

    private void chooseBirthday() {
        Calendar calendar = Calendar.getInstance();
        final DatePicker picker = new DatePicker(this);
        picker.setCanceledOnTouchOutside(true);
        picker.setUseWeight(true);
        picker.setTopPadding(ConvertUtils.toPx(this, 10));
        picker.setRangeStart(1940, 1, 1);
        picker.setSelectedItem(1980,1,1);
        calendar.setTime(new Date(System.currentTimeMillis() + 24 * 3600 * 1000));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        picker.setRangeEnd(year, month, day);
        picker.setResetWhileWheel(false);
        picker.setOnDatePickListener((DatePicker.OnYearMonthDayPickListener) (year1, month1, day1) -> updateBirthday(year1 + "-" + month1 + "-" + day1));
        picker.show();
    }

    private void updateBirthday(String birth) {
        showLoadingDialog();
        CarefreeRetrofit.getInstance().createApi(UserApis.class)
                .updateUserInfo(CarefreeDaoSession.getInstance().getUserId(), QueryMapBuilder.getIns()
                        .put("field", "birthday")
                        .put("value", birth)
                        .buildPost())
                .compose(RxUtil.switchSchedulers())
                .subscribe(new BaseSubscriber<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse baseResponse) {
                        tvBirthdayArea.setText(birth);
                    }
                });


    }

    private void chosePhoto() {
        Matisse.from(this)
                .choose(MimeType.ofImage())
                .capture(true)
                .captureStrategy(new CaptureStrategy(true, "com.wuyou.user.FileProvider"))
                .showSingleMediaType(true)
                .theme(R.style.Matisse_Dracula)
                .countable(false)
                .maxSelectable(1)
                .imageEngine(new Glide4Engine())
                .forResult(Constant.REQUEST_CODE_CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constant.REQUEST_CODE_CHOOSE_IMAGE) {
                imagePath = Matisse.obtainResult(data).get(0);
                GlideUtils.loadImage(getCtx(), Matisse.obtainResult(data).get(0).toString(), infoHead, true);
            } else if (requestCode == Constant.Intent.REQUEST_NICK) {
                tvAccountArea.setText(data.getStringExtra("info"));
            } else if (requestCode == Constant.Intent.REQUEST_PHONE) {
                tvPhoneArea.setText(data.getStringExtra("info"));
            } else if (requestCode == Constant.Intent.REQUEST_EMAIL) {
                tvEmailArea.setText(data.getStringExtra("info"));
            } else if (requestCode == Constant.Intent.REQUEST_GENDER) {
                int gender = data.getIntExtra("info", 0);
                tvSexArea.setText(getGenderString(gender));
            }
        }
    }

    private String getGenderString(int gender) {
        if (gender == 0) return "男";
        else if (gender == 1) return "女";
        else return "保密";
    }

    public void setUserData(UserInfo userInfo) {
        if (userInfo.getNickname() != null) tvAccountArea.setText(userInfo.getNickname());
        if (userInfo.getGender() != null)
            tvSexArea.setText(getGenderString(Integer.parseInt(userInfo.getGender())));
        if (userInfo.getBirthday() != null)
            tvBirthdayArea.setText(TribeDateUtils.dateFormat5(new Date(Long.parseLong(userInfo.getBirthday()) * 1000)));
        if (userInfo.getEmail() != null) tvEmailArea.setText(userInfo.getEmail());
        if (userInfo.getAvatar() != null)
            GlideUtils.loadRoundCornerImage(this, userInfo.getAvatar(), infoHead);
    }
}

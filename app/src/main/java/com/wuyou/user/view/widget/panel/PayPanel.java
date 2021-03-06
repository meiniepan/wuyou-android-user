package com.wuyou.user.view.widget.panel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.gs.buluo.common.network.ApiException;
import com.gs.buluo.common.network.BaseResponse;
import com.gs.buluo.common.network.BaseSubscriber;
import com.gs.buluo.common.network.QueryMapBuilder;
import com.gs.buluo.common.utils.DensityUtils;
import com.gs.buluo.common.widget.CustomAlertDialog;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.wuyou.user.CarefreeApplication;
import com.wuyou.user.CarefreeDaoSession;
import com.wuyou.user.Constant;
import com.wuyou.user.R;
import com.wuyou.user.data.remote.BankCard;
import com.wuyou.user.data.remote.PayChannel;
import com.wuyou.user.data.remote.WalletBalance;
import com.wuyou.user.data.remote.response.WxPayResponse;
import com.wuyou.user.event.WXPayEvent;
import com.wuyou.user.network.CarefreeRetrofit;
import com.wuyou.user.network.apis.MoneyApis;
import com.wuyou.user.network.apis.OrderApis;
import com.wuyou.user.util.RxUtil;
import com.wuyou.user.view.activity.PayFinishActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hjn on 2016/12/7.
 */
public class PayPanel extends Dialog implements View.OnClickListener, PayChoosePanel.onChooseFinish {
    private OnPayFinishListener onFinishListener;
    private Activity mCtx;
    @BindView(R.id.pay_way)
    TextView tvWay;
    @BindView(R.id.pay_money)
    TextView tvTotal;
    @BindView(R.id.pay_choose_area)
    View chooseArea;
    @BindView(R.id.pay_choose)
    View arrow;

    private String totalFee;
    private String targetId;

    private PayChannel payChannel = PayChannel.WECHAT;
    private String secondPay = "1";

    public PayPanel(Activity context, OnPayFinishListener onFinishListener) {
        super(context, R.style.my_dialog);
        mCtx = context;
        this.onFinishListener = onFinishListener;
        EventBus.getDefault().register(this);
        initView();
    }

    @Override
    public void dismiss() {
        if (onDismissListener != null) onDismissListener.onDismiss();
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
        super.dismiss();
    }

    public void setData(String price, String targetId, String type) {
        this.totalFee = price;
        tvTotal.setText(price);
        this.targetId = targetId;
        secondPay = type;
        getWalletInfo();
    }

    private void initView() {
        View rootView = LayoutInflater.from(mCtx).inflate(R.layout.pay_board, null);
        setContentView(rootView);
        ButterKnife.bind(this);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = DensityUtils.dip2px(mCtx, 400);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);

        rootView.findViewById(R.id.pay_ask).setOnClickListener(this);
        rootView.findViewById(R.id.pay_close).setOnClickListener(this);
        rootView.findViewById(R.id.pay_finish).setOnClickListener(this);
        chooseArea.setOnClickListener(this);
    }

    private float balance;

    private void getWalletInfo() {
        CarefreeRetrofit.getInstance().createApi(MoneyApis.class)
                .getWalletBalance(CarefreeDaoSession.getInstance().getUserId(), QueryMapBuilder.getIns().buildGet())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseResponse<WalletBalance>>() {
                    @Override
                    public void onSuccess(BaseResponse<WalletBalance> walletBalanceBaseResponse) {
                        balance = walletBalanceBaseResponse.data.balance;
                    }
                });
    }

    private void showNotEnough() {
        new CustomAlertDialog.Builder(mCtx).setTitle(R.string.prompt).setMessage(mCtx.getString(R.string.not_enough_balance))
                .setPositiveButton(mCtx.getString(R.string.ok), (dialog, which) -> dismiss()).create().show();
    }

    private void showAlert() {
        new CustomAlertDialog.Builder(getContext()).setTitle(R.string.prompt).setMessage(R.string.not_set_pwd)
                .setPositiveButton("去设置", (dialog, which) -> {
                }).setNegativeButton(mCtx.getString(R.string.cancel), null).create().show();
    }

//    private void showPasswordPanel(final String password) {
//        NewPasswordPanel passwordPanel = new NewPasswordPanel(mCtx, password, new NewPasswordPanel.OnPwdFinishListener() {
//            @Override
//            public void onPwdFinishListener(String strPassword) {
//                createPayment(strPassword);
//            }
//
//            @Override
//            public void onPwdPanelDismiss() {
//                dismiss();
//            }
//        });
//        passwordPanel.show();
//        TranslateAnimation animation = new TranslateAnimation(0, -CommonUtils.getScreenWidth(mCtx), 0, 0);
//        animation.setDuration(500);
//        animation.setFillAfter(true);
//        animation.start();
//        rootView.startAnimation(animation);
//    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_close:
                dismiss();
                break;
            case R.id.pay_finish:
                doPay();
                break;
            case R.id.pay_choose_area:
                PayChoosePanel payChoosePanel = new PayChoosePanel(mCtx, payChannel, this);
                payChoosePanel.show();
                break;
        }
    }

    private void doPay() {
        switch (payChannel) {
            case BALANCE:
                if (Float.valueOf(totalFee) > balance) {
                    showNotEnough();
                    return;
                }
                payInBalance();
                break;
            case ALIPAY:
                payInAli();
                break;
            case WECHAT:
                payInWX();
                break;
        }
    }

    private IWXAPI msgApi;

    private void payInWX() {
        msgApi = WXAPIFactory.createWXAPI(CarefreeApplication.getInstance().getApplicationContext(), null);
        msgApi.registerApp(Constant.WX_ID);
        CarefreeRetrofit.getInstance().createApi(MoneyApis.class).getWXPayOrderInfo(targetId, QueryMapBuilder.getIns()
                .put("uid", CarefreeDaoSession.getInstance().getUserId())
                .put("pay_type", "APP")
                .put("is_mini_program", "0")
                .put("stage", secondPay).buildGet())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseResponse<WxPayResponse>>() {
                    @Override
                    public void onSuccess(BaseResponse<WxPayResponse> baseResponseBaseResponse) {
                        doPay(baseResponseBaseResponse.data);
                    }
                });

    }

    private void doPay(WxPayResponse data) {
        PayReq request = new PayReq();
        request.appId = data.appid;
        request.partnerId = data.mch_id;
        request.prepayId = data.prepay_id;
        request.packageValue = "Sign=WXPay";
        request.nonceStr = data.nonce_str;
        request.timeStamp = data.timestamp;
        request.sign = data.sign;
        msgApi.sendReq(request);
    }


    private void payInAli() {
        CarefreeRetrofit.getInstance().createApi(MoneyApis.class)
                .getAliPayOrderInfo(targetId, QueryMapBuilder.getIns().put("uid", CarefreeDaoSession.getInstance().getUserId()).put("stage", secondPay).buildGet())
                .subscribeOn(Schedulers.io())
                .map(simpleResponse -> {
                    PayTask alipay = new PayTask(mCtx);
                    return alipay.payV2(simpleResponse.data.response, true);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> doNext());
    }

    private void doNext() {
        payFinish();
        onFinishListener.onPayFinish();
        dismiss();
    }

    private void payInBalance() {
        CarefreeRetrofit.getInstance().createApi(OrderApis.class)
                .payOrder(targetId, QueryMapBuilder.getIns().put("pay_type", "1").put("user_id", CarefreeDaoSession.getInstance().getUserId()).buildPost())
                .compose(RxUtil.switchSchedulers())
                .subscribe(new BaseSubscriber<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse baseResponse) {
                        doNext();
                    }

                    @Override
                    protected void onFail(ApiException e) {
                        onFinishListener.onPayFail(e);
                    }
                });
    }

    @Override
    public void onChoose(PayChannel payChannel, BankCard bankCard, String bankName) {
        this.payChannel = payChannel;
        tvWay.setText(this.payChannel.value);
    }

    public interface OnPayFinishListener {
        void onPayFinish();

        void onPayFail(ApiException e);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWXPayFinish(WXPayEvent event) {
        doNext();
    }

    private void payFinish() {
        onDismissListener = null;
        Intent intent = new Intent(mCtx, PayFinishActivity.class);
        intent.putExtra(Constant.ORDER_ID, targetId);
        mCtx.startActivity(intent);
    }

    private onDismissListener onDismissListener;

    public interface onDismissListener {
        void onDismiss();
    }

    public void setOnDismissListener(onDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }
}
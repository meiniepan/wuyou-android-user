package com.wuyou.user.mvp.order;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.gs.buluo.common.network.ApiException;
import com.gs.buluo.common.utils.ToastUtils;
import com.gs.buluo.common.utils.TribeDateUtils;
import com.gs.buluo.common.widget.CustomAlertDialog;
import com.wuyou.user.Constant;
import com.wuyou.user.R;
import com.wuyou.user.bean.OrderBeanDetail;
import com.wuyou.user.bean.response.OrderListResponse;
import com.wuyou.user.util.CommonUtil;
import com.wuyou.user.view.activity.BaseActivity;
import com.wuyou.user.view.activity.CommentActivity;
import com.wuyou.user.view.activity.RobotActivity;
import com.wuyou.user.view.widget.panel.PayPanel;

import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by hjn on 2018/2/6.
 */

public class OrderDetailActivity extends BaseActivity<OrderContract.View, OrderContract.Presenter> implements OrderContract.View {
    @BindView(R.id.order_detail_status)
    TextView orderDetailStatus;
    @BindView(R.id.order_detail_un_pay_warn)
    TextView orderDetailWarn;
    @BindView(R.id.order_detail_store)
    TextView orderDetailStore;
    @BindView(R.id.order_detail_title)
    TextView orderDetailTitle;
    @BindView(R.id.order_detail_count)
    TextView orderDetailCount;
    @BindView(R.id.order_detail_price)
    TextView orderDetailPrice;
    @BindView(R.id.order_detail_discount_name)
    TextView orderDetailDiscountName;
    @BindView(R.id.order_detail_discount)
    TextView orderDetailDiscount;
    @BindView(R.id.order_detail_price_final)
    TextView orderDetailPriceFinal;
    @BindView(R.id.order_detail_address)
    TextView orderDetailAddress;
    @BindView(R.id.order_detail_name)
    TextView orderDetailName;
    @BindView(R.id.order_detail_phone)
    TextView orderDetailPhone;
    @BindView(R.id.order_detail_create_time)
    TextView orderDetailCreateTime;
    @BindView(R.id.order_detail_number)
    TextView orderDetailNumber;
    @BindView(R.id.order_detail_pay_method)
    TextView orderDetailPayMethod;
    @BindView(R.id.order_detail_pay_status)
    TextView orderDetailBillStatus;
    @BindView(R.id.order_detail_action)
    TextView orderDetailAction;
    @BindView(R.id.order_detail_serve_way)
    TextView orderDetailServeWay;
    @BindView(R.id.order_detail_serve_time)
    TextView orderDetailServeTime;
    @BindView(R.id.order_detail_remark)
    TextView orderDetailRemark;
    @BindView(R.id.order_detail_pay_time)
    TextView orderDetailPayTime;
    @BindView(R.id.order_detail_contact_store)
    TextView orderDetailContactStore;
    private String orderId;
    private PayPanel payPanel;
    private OrderBeanDetail beanDetail;
    private String shopTel;

    @Override
    protected void bindView(Bundle savedInstanceState) {
        orderId = getIntent().getStringExtra(Constant.ORDER_ID);
        mPresenter.getOrderDetail(orderId);
        showLoadingDialog();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_order_detail;
    }

    @Override
    protected OrderContract.Presenter getPresenter() {
        return new OrderPresenter();
    }

    @Override
    public void getOrderSuccess(OrderListResponse list) {
    }

    @Override
    public void loadMore(OrderListResponse data) {
    }

    @Override
    public void loadMoreFail(String displayMessage, int code) {
    }

    @Override
    public void paySuccess() {

    }

    @Override
    public void finishOrderSuccess() {
        finish();
    }

    @Override
    public void cancelSuccess(int position) {
        ToastUtils.ToastMessage(getCtx(), R.string.cancel_success);
        finish();
    }

    @Override
    public void showError(String message, int res) {
        ToastUtils.ToastMessage(getCtx(), message);
    }

    @Override
    public void getOrderDetailSuccess(OrderBeanDetail bean) {
        setData(bean);
    }

    public void setData(OrderBeanDetail data) {
        beanDetail = data;
        if (beanDetail.status == 1) orderDetailWarn.setVisibility(View.VISIBLE);
        orderDetailStatus.setText(CommonUtil.getOrderStatusString(data.status));
        orderDetailStore.setText(data.shop.shop_name);
        orderDetailTitle.setText(data.service.service_name);
        orderDetailCount.setText(data.number + "");
        orderDetailPrice.setText(data.total_amount);
        orderDetailPriceFinal.setText(data.total_amount);
        orderDetailName.setText(data.address.name);
        orderDetailAddress.setText(data.address.city_name + data.address.district + data.address.address);
        orderDetailPhone.setText(data.address.mobile);

        orderDetailCreateTime.setText(TribeDateUtils.dateFormat(new Date(data.created_at * 1000)));
        orderDetailNumber.setText(data.order_number);
        orderDetailServeWay.setText(data.service_mode);
        orderDetailServeTime.setText(data.service_date + data.service_time);
        orderDetailRemark.setText(data.remark);

        orderDetailBillStatus.setText(CommonUtil.getOrderStatusString(data.status));
        orderDetailPayMethod.setText(data.pay_type);
        shopTel = data.shop.shop_tel;

        setActionStatus();
    }


    @OnClick({R.id.order_detail_action, R.id.order_detail_contact_store})
    public void onViewClicked(View view) {
        if (TextUtils.isEmpty(orderId)) return;
        switch (view.getId()) {
            case R.id.order_detail_contact_store:
                Intent intent = new Intent(getCtx(), RobotActivity.class);
                startActivity(intent);
                break;
            case R.id.order_detail_action:
                switch (beanDetail.status) {
                    case 3:
                        Intent intent1 = new Intent(getCtx(), CommentActivity.class);
                        intent1.putExtra(Constant.ORDER_BEAN, beanDetail);
                        startActivity(intent1);
                        break;
                    case 1:
                        new CustomAlertDialog.Builder(getCtx()).setTitle(R.string.prompt).setMessage("确认取消?")
                                .setPositiveButton(getCtx().getString(R.string.yes), (dialog, which) ->
                                        mPresenter.cancelOrder(0, orderId)).setNegativeButton(getCtx().getResources().getString(R.string.cancel), null).create().show();
                        break;
                    case 2:
                        if (beanDetail.second_payment == 0) {
                            mPresenter.finishOrder(orderId);
                        } else {
                            paySecond();
                        }
                        break;
                }
                break;
        }
    }

    private void paySecond() {
        payPanel = new PayPanel(this, new PayPanel.OnPayFinishListener() {
            @Override
            public void onPaying() {
                mPresenter.finishOrder(orderId);
                payPanel.dismiss();
            }

            @Override
            public void onPayFail(ApiException e) {

            }
        });
        payPanel.show();
    }

    public void setActionStatus() {
        switch (beanDetail.status) {
            case 1:
                orderDetailAction.setText(R.string.cancel);
                findViewById(R.id.order_detail_pay_area).setVisibility(View.GONE);
                break;
            case 2:
                if (beanDetail.can_finish == 1) {
                    orderDetailAction.setBackgroundResource(R.drawable.pay_selector);
                    orderDetailAction.setVisibility(View.VISIBLE);
                    orderDetailAction.setText(R.string.finish_serve);
                } else {
                    orderDetailAction.setVisibility(View.GONE);
                }

            case 3:
                orderDetailAction.setBackgroundResource(R.drawable.pay_selector);
                orderDetailAction.setText(R.string.comment);
                break;
            case 4:
                orderDetailAction.setVisibility(View.GONE);
                break;
        }

    }
}
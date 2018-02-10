package com.wuyou.user.mvp.serve;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gs.buluo.common.network.BaseResponse;
import com.gs.buluo.common.network.BaseSubscriber;
import com.gs.buluo.common.network.QueryMapBuilder;
import com.wuyou.user.Constant;
import com.wuyou.user.R;
import com.wuyou.user.bean.CommentBean;
import com.wuyou.user.bean.ServeDetailBean;
import com.wuyou.user.bean.response.CommentResponse;
import com.wuyou.user.bean.response.ServeDetailResponse;
import com.wuyou.user.network.CarefreeRetrofit;
import com.wuyou.user.network.apis.ServeApis;
import com.wuyou.user.util.GlideUtils;
import com.wuyou.user.view.activity.BaseActivity;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.techery.properratingbar.ProperRatingBar;

/**
 * Created by hjn on 2018/2/7.
 */

public class ServeDetailActivity extends BaseActivity {
    @BindView(R.id.serve_detail_picture)
    ImageView serveDetailPicture;
    @BindView(R.id.serve_detail_title)
    TextView serveDetailTitle;
    @BindView(R.id.serve_detail_count)
    TextView serveDetailCount;
    @BindView(R.id.serve_detail_price)
    TextView serveDetailPrice;
    @BindView(R.id.serve_detail_serve_time)
    TextView serveDetailServeTime;
    @BindView(R.id.serve_detail_description)
    TextView serveDetailDescription;
    @BindView(R.id.serve_detail_store)
    TextView serveDetailStore;
    @BindView(R.id.create_order_serve_point)
    TextView createOrderServePoint;
    @BindView(R.id.create_order_serve_comment_count)
    TextView createOrderServeCommentCount;
    @BindView(R.id.serve_detail_comment_phone)
    TextView serveDetailCommentPhone;
    @BindView(R.id.serve_detail_comment_star)
    ProperRatingBar serveDetailCommentStar;
    @BindView(R.id.serve_detail_comment_content)
    TextView serveDetailCommentContent;
    @BindView(R.id.serve_detail_known)
    ListView serveDetailKnown;
    private ServeDetailBean serviceDetail;

    @Override
    protected void bindView(Bundle savedInstanceState) {
        showLoadingDialog();
        String id = getIntent().getStringExtra(Constant.SERVE_ID);
        CarefreeRetrofit.getInstance().createApi(ServeApis.class)
                .getServeDetail(id, QueryMapBuilder.getIns().buildGet())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseResponse<ServeDetailResponse>>() {
                    @Override
                    public void onSuccess(BaseResponse<ServeDetailResponse> serveDetailBeanBaseResponse) {
                        setData(serveDetailBeanBaseResponse.data);
                    }
                });

        findViewById(R.id.serve_detail_comment_area).setOnClickListener(v -> {
            //jump to comment
        });
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_serve_detail;
    }


    public void buyNow(View view) {
        Intent intent = new Intent(getCtx(), NewOrderActivity.class);
        intent.putExtra(Constant.SERVE_BEAN, serviceDetail);
        startActivity(intent);

    }

    public void setData(ServeDetailResponse data) {
        serviceDetail = data.service_detail;
        serviceDetail.shop_id = data.shop.id;
        GlideUtils.loadImageNoHolder(this, serviceDetail.image, serveDetailPicture);
        serveDetailTitle.setText(serviceDetail.category_name);
        serveDetailServeTime.setText(serviceDetail.service_time);
        serveDetailCount.setText(serviceDetail.total_sell);
        serveDetailPrice.setText(serviceDetail.price);
        serveDetailDescription.setText(serviceDetail.description);
        serveDetailStore.setText(data.shop.name);
        CommentResponse comments = data.comments;
        createOrderServeCommentCount.setText(comments.total_comments);
        if (comments.list != null && comments.list.size() > 0) {
            CommentBean commentBean = comments.list.get(0);
            serveDetailCommentPhone.setText(commentBean.author);
            serveDetailCommentStar.setRating(commentBean.star / 2);
            serveDetailCommentContent.setText(commentBean.content);
        }
//        serveDetailKnown.setAdapter(new SimpleAdapter(this,serviceDetail.price));
    }
}

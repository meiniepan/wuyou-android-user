package com.wuyou.user.mvp.vote;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.gs.buluo.common.network.ApiException;
import com.gs.buluo.common.network.BaseSubscriber;
import com.gs.buluo.common.widget.recyclerHelper.BaseHolder;
import com.gs.buluo.common.widget.recyclerHelper.BaseQuickAdapter;
import com.wuyou.user.Constant;
import com.wuyou.user.R;
import com.wuyou.user.data.EoscDataManager;
import com.wuyou.user.data.api.EosVoteListBean;
import com.wuyou.user.util.RxUtil;
import com.wuyou.user.util.glide.GlideUtils;
import com.wuyou.user.view.fragment.BaseFragment;
import com.wuyou.user.view.widget.CarefreeRecyclerView;
import com.wuyou.user.view.widget.ConditionSelectView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by DELL on 2018/10/15.
 */

public class VoteListFragment extends BaseFragment {
    @BindView(R.id.vote_list)
    CarefreeRecyclerView voteList;
    @BindView(R.id.vote_list_time)
    ConditionSelectView voteListTime;
    @BindView(R.id.vote_list_recommend)
    TextView voteListRecommend;
    @BindView(R.id.vote_list_filter)
    ConditionSelectView voteListFilter;
    @BindView(R.id.alpha_view_1)
    View alphaView1;
    @BindView(R.id.vote_list_time_layout)
    LinearLayout voteListTimeLayout;
    @BindView(R.id.alpha_view_2)
    View alphaView2;
    @BindView(R.id.voteList_filter_layout)
    LinearLayout voteListFilterLayout;
    @BindView(R.id.vote_list_time_1)
    TextView voteListTime1;
    @BindView(R.id.vote_list_time_2)
    TextView voteListTime2;
    @BindView(R.id.vote_list_filter_1)
    TextView voteListFilter1;
    @BindView(R.id.vote_list_filter_2)
    TextView voteListFilter2;
    @BindView(R.id.vote_list_filter_3)
    TextView voteListFilter3;
    private VoteListAdapter listAdapter;
    private VoteActivity ownerActivity;

    @Override
    protected void bindView(Bundle savedInstanceState) {
        voteList.showProgressView();
        listAdapter = new VoteListAdapter();
        voteList.setAdapter(listAdapter);

        getAllVoteList();

        listAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                navigateToVoteDetail(i);
            }
        });
    }

    private void navigateToVoteDetail(int i) {
        //TODO
        EosVoteListBean.RowsBean rowsBean = listAdapter.getData().get(i);
        Intent intent = new Intent(getContext(), VoteActivity.class);
        intent.putExtra("", "");
        startActivity(intent);
    }


    public void getAllVoteList() {
        EoscDataManager.getIns().getTable("votevotevote", "votevotevote", "votelist")
                .compose(RxUtil.switchSchedulers()).subscribe(new BaseSubscriber<String>() {
            @Override
            public void onSuccess(String s) {
                voteList.showContentView();
                EosVoteListBean listBean = new GsonBuilder().create().fromJson(s, EosVoteListBean.class);
                listAdapter.setNewData(listBean.rows);
            }

            @Override
            protected void onFail(ApiException e) {
                voteList.showErrorView(e.getDisplayMessage());
            }
        });
    }


    @Override
    protected int getContentLayout() {
        return R.layout.activity_vote_list;
    }


    @OnClick({R.id.vote_list_recommend, R.id.vote_list_time, R.id.vote_list_filter, R.id.alpha_view_1, R.id.alpha_view_2
            , R.id.vote_list_time_1, R.id.vote_list_time_2, R.id.vote_list_filter_1, R.id.vote_list_filter_2, R.id.vote_list_filter_3, R.id.voteList_filter_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.vote_list_recommend:
                voteListRecommend.setTextColor(getResources().getColor(R.color.night_blue));
                voteListFilter.setSelected(false);
                voteListTime.setSelected(false);
                voteListTimeLayout.setVisibility(View.GONE);
                voteListFilterLayout.setVisibility(View.GONE);
                ownerActivity.setBottomAlpha(false);
                break;
            case R.id.vote_list_time:
                voteListTimeLayout.setVisibility(View.VISIBLE);
                voteListFilterLayout.setVisibility(View.GONE);
                ownerActivity.setBottomAlpha(true);
                break;
            case R.id.vote_list_filter:
                voteListTimeLayout.setVisibility(View.GONE);
                voteListFilterLayout.setVisibility(View.VISIBLE);
                ownerActivity.setBottomAlpha(true);
                break;
            case R.id.alpha_view_1:
                hideShadow();
                break;
            case R.id.alpha_view_2:
                hideShadow();
                break;
            case R.id.vote_list_time_1:
                voteListRecommend.setTextColor(getResources().getColor(R.color.common_gray));
                voteListTime.setSelected(true);
                voteListFilter.setSelected(false);

                voteListTime1.setTextColor(getResources().getColor(R.color.night_blue));
                voteListTime2.setTextColor(getResources().getColor(R.color.common_dark));
                hideShadow();
                break;
            case R.id.vote_list_time_2:
                voteListRecommend.setTextColor(getResources().getColor(R.color.common_gray));
                voteListTime.setSelected(true);
                voteListFilter.setSelected(false);

                voteListTime1.setTextColor(getResources().getColor(R.color.common_dark));
                voteListTime2.setTextColor(getResources().getColor(R.color.night_blue));
                hideShadow();
                break;
            case R.id.vote_list_filter_1:
                voteListRecommend.setTextColor(getResources().getColor(R.color.common_gray));
                voteListTime.setSelected(false);
                voteListFilter.setSelected(true);

                voteListFilter1.setBackgroundResource(R.drawable.blue_radius_button);
                voteListFilter1.setTextColor(getResources().getColor(R.color.white));
                voteListFilter2.setBackgroundResource(R.drawable.night_blue_border);
                voteListFilter2.setTextColor(getResources().getColor(R.color.night_blue));
                voteListFilter3.setBackgroundResource(R.drawable.night_blue_border);
                voteListFilter3.setTextColor(getResources().getColor(R.color.night_blue));
                hideShadow();
                break;
            case R.id.vote_list_filter_2:
                voteListRecommend.setTextColor(getResources().getColor(R.color.common_gray));
                voteListTime.setSelected(false);
                voteListFilter.setSelected(true);

                voteListFilter1.setBackgroundResource(R.drawable.night_blue_border);
                voteListFilter1.setTextColor(getResources().getColor(R.color.night_blue));
                voteListFilter2.setBackgroundResource(R.drawable.blue_radius_button);
                voteListFilter2.setTextColor(getResources().getColor(R.color.white));
                voteListFilter3.setBackgroundResource(R.drawable.night_blue_border);
                voteListFilter3.setTextColor(getResources().getColor(R.color.night_blue));
                hideShadow();
                break;
            case R.id.vote_list_filter_3:
                voteListRecommend.setTextColor(getResources().getColor(R.color.common_gray));
                voteListTime.setSelected(false);
                voteListFilter.setSelected(true);

                voteListFilter1.setBackgroundResource(R.drawable.night_blue_border);
                voteListFilter1.setTextColor(getResources().getColor(R.color.night_blue));
                voteListFilter2.setBackgroundResource(R.drawable.night_blue_border);
                voteListFilter2.setTextColor(getResources().getColor(R.color.night_blue));
                voteListFilter3.setBackgroundResource(R.drawable.blue_radius_button);
                voteListFilter3.setTextColor(getResources().getColor(R.color.white));
                hideShadow();
                break;
        }
    }

    private void hideShadow() {
        voteListTimeLayout.setVisibility(View.GONE);
        voteListFilterLayout.setVisibility(View.GONE);
        ownerActivity.setBottomAlpha(false);
    }

    public void setOwnerActivity(VoteActivity ownerActivity) {
        this.ownerActivity = ownerActivity;
    }

    private class VoteListAdapter extends BaseQuickAdapter<EosVoteListBean.RowsBean, BaseHolder> {
        VoteListAdapter() {
            super(R.layout.item_vote_list);
        }

        @Override
        protected void convert(BaseHolder baseHolder, EosVoteListBean.RowsBean rowsBean) {
            baseHolder.setText(R.id.item_vote_list_title, rowsBean.title)
                    .setText(R.id.item_vote_list_location, rowsBean.description)
                    .setText(R.id.item_vote_list_voter, rowsBean.voters.size() + "");
            GlideUtils.loadImage(mContext, Constant.IPFS_URL + rowsBean.logo, baseHolder.getView(R.id.item_vote_list_picture));
        }
    }
}

package com.wuyou.user.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.gs.buluo.common.network.ApiException;
import com.gs.buluo.common.network.BaseResponse;
import com.gs.buluo.common.network.BaseSubscriber;
import com.gs.buluo.common.network.QueryMapBuilder;
import com.gs.buluo.common.widget.CustomAlertDialog;
import com.gs.buluo.common.widget.StatusLayout;
import com.wuyou.user.CarefreeDaoSession;
import com.wuyou.user.Constant;
import com.wuyou.user.R;
import com.wuyou.user.data.local.db.SearchHistoryBean;
import com.wuyou.user.data.remote.ServeBean;
import com.wuyou.user.data.remote.response.ListResponse;
import com.wuyou.user.mvp.serve.ServeDetailActivity;
import com.wuyou.user.mvp.serve.ServeListAdapter;
import com.wuyou.user.network.CarefreeRetrofit;
import com.wuyou.user.network.apis.ServeApis;
import com.wuyou.user.util.CommonUtil;
import com.wuyou.user.util.KeyboardUtils;
import com.wuyou.user.util.RxUtil;
import com.wuyou.user.view.widget.search.SearchRecyclerViewAdapter;

import java.util.List;

import butterknife.BindView;

/**
 * Created by DELL on 2018/3/9.
 */

public class SearchActivity extends BaseActivity {
    @BindView(R.id.search_list)
    RecyclerView searchList;
    @BindView(R.id.search_status)
    StatusLayout searchStatus;
    @BindView(R.id.search_history_recycler)
    RecyclerView searchHistoryList;
    @BindView(R.id.search_history_edit)
    EditText searchEdit;
    @BindView(R.id.search_history_card)
    View cardView;
    private ServeListAdapter adapter;
    private String searchText;

    @Override
    protected void bindView(Bundle savedInstanceState) {
        setTitleText(R.string.search);
        searchEdit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (searchEdit.length() == 0) return false;
                searchText = searchEdit.getText().toString().trim();
                doSearch();
                return true;
            }
            return false;
        });
        searchList.setLayoutManager(new LinearLayoutManager(this));
        searchHistoryList.setLayoutManager(new LinearLayoutManager(this));
        searchHistoryList.addItemDecoration(CommonUtil.getRecyclerDivider(this));

        adapter = new ServeListAdapter(this, R.layout.item_serve_list);
        searchList.setAdapter(adapter);
        adapter.bindToRecyclerView(searchList);
        adapter.setOnLoadMoreListener(this::getMore, searchList);
        adapter.setOnItemClickListener((adapter, view, position) -> goDetail((ServeBean) adapter.getData().get(position)));

        SearchRecyclerViewAdapter historyAdapter = new SearchRecyclerViewAdapter(CarefreeDaoSession.getInstance().getHistoryRecords());
        searchHistoryList.setAdapter(historyAdapter);
        historyAdapter.bindToRecyclerView(searchHistoryList);
        if (historyAdapter.getData().size() == 0)
            findViewById(R.id.search_clear).setVisibility(View.GONE);
        historyAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            searchText = ((SearchHistoryBean) adapter.getData().get(position)).getTitle();
            KeyboardUtils.hideSoftInput(searchEdit, getCtx());
            doSearch();
        });

        searchEdit.setOnTouchListener((v, event) -> {
            historyAdapter.setNewData(CarefreeDaoSession.getInstance().getHistoryRecords());
            cardView.setVisibility(View.VISIBLE);
            return false;
        });

        findViewById(R.id.search_clear).setOnClickListener(v -> new CustomAlertDialog.Builder(getCtx()).setTitle("提示").setMessage("确定清空历史搜索吗?")
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    CarefreeDaoSession.getInstance().clearSearchHistory();
                    historyAdapter.clearData();
                }).setNegativeButton(getString(R.string.cancel), null).create().show());
    }

    private void goDetail(ServeBean serveBean) {
        Intent intent = new Intent(getCtx(), ServeDetailActivity.class);
        intent.putExtra(Constant.SERVE_ID, serveBean.service_id);
        startActivity(intent);
    }

    private String startId = "0";

    private void doSearch() {
        CarefreeDaoSession.getInstance().addHistoryRecord(new SearchHistoryBean(searchText));
        findViewById(R.id.search_clear).setVisibility(View.VISIBLE);
        searchStatus.showProgressView();
        cardView.setVisibility(View.GONE);
        CarefreeRetrofit.getInstance().createApi(ServeApis.class)
                .searchServe(QueryMapBuilder.getIns()
                        .put("keyword", searchText)
                        .put("start_id", "0")
                        .put("flag", "1")
                        .put("size", "20")
                        .buildGet())
                .compose(RxUtil.switchSchedulers())
                .subscribe(new BaseSubscriber<BaseResponse<ListResponse<ServeBean>>>() {
                    @Override
                    public void onSuccess(BaseResponse<ListResponse<ServeBean>> listBaseResponse) {
                        searchStatus.showContentView();
                        List<ServeBean> list = listBaseResponse.data.list;
                        if (list.size() == 0) {
                            searchStatus.showEmptyView("没有找到您要的服务，换个词试试吧");
                            return;
                        }
                        adapter.setNewData(list);
                        startId = list.get(list.size() - 1).service_id;
                        if (listBaseResponse.data.has_more == 0) {
                            adapter.loadMoreEnd(true);
                        }
                    }

                    @Override
                    protected void onFail(ApiException e) {
                        searchStatus.showErrorView(e.getDisplayMessage());
                    }
                });
    }


    @Override
    protected int getContentLayout() {
        return R.layout.activity_search;
    }

    public void getMore() {
        CarefreeRetrofit.getInstance().createApi(ServeApis.class)
                .searchServe(QueryMapBuilder.getIns()
                        .put("keyword", searchText)
                        .put("start_id", startId)
                        .put("flag", "2")
                        .buildGet())
                .compose(RxUtil.switchSchedulers())
                .subscribe(new BaseSubscriber<BaseResponse<ListResponse<ServeBean>>>() {
                    @Override
                    public void onSuccess(BaseResponse<ListResponse<ServeBean>> listBaseResponse) {
                        List<ServeBean> list = listBaseResponse.data.list;
                        adapter.addData(list);
                        startId = list.get(list.size() - 1).service_id;
                        if (listBaseResponse.data.has_more == 0) {
                            adapter.loadMoreEnd(true);
                        }
                    }

                    @Override
                    protected void onFail(ApiException e) {
                        adapter.loadMoreFail();
                    }
                });
    }
}

package com.wuyou.user.mvp.serve;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.gs.buluo.common.widget.StatusLayout;
import com.wuyou.user.R;
import com.wuyou.user.view.activity.BaseActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hjn on 2018/2/6.
 */

public class ServeCategoryListActivity extends BaseActivity {
    @BindView(R.id.serve_category)
    TextView serveCategory;
    @BindView(R.id.serve_category_list)
    RecyclerView serveList;
    @BindView(R.id.serve_category_status)
    StatusLayout serveCategoryStatus;

    @Override
    protected void bindView(Bundle savedInstanceState) {
        ArrayList list = new ArrayList();
        serveList.setAdapter(new ServeListAdapter(R.layout.item_serve_list));
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_serve_category_list;
    }

    public void fastCreate(View view) {
        Intent intent = new Intent(getCtx(), FastCreateActivity.class);
        startActivity(intent);
    }
}
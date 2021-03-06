package com.wuyou.user.mvp.home;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.gnway.bangwoba.view.DensityUtils;
import com.gs.buluo.common.widget.LoadingDialog;
import com.gs.buluo.common.widget.recyclerHelper.BaseHolder;
import com.gs.buluo.common.widget.recyclerHelper.BaseQuickAdapter;
import com.wuyou.user.Constant;
import com.wuyou.user.R;
import com.wuyou.user.data.remote.response.CategoryChild;
import com.wuyou.user.data.remote.response.CategoryParent;
import com.wuyou.user.mvp.serve.ServeCategoryListActivity;
import com.wuyou.user.util.CommonUtil;
import com.wuyou.user.util.glide.GlideUtils;
import com.wuyou.user.view.widget.GridLineSpacingItemDecoration;
import com.wuyou.user.view.widget.GridSpacingItemDecoration;
import com.wuyou.user.view.widget.panel.HomePictureDialog;

/**
 * Created by hjn on 2018/2/8.
 */

public class MainServeAdapter extends BaseQuickAdapter<CategoryParent, BaseHolder> {

    public MainServeAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseHolder helper, CategoryParent item) {
        RecyclerView recyclerView = helper.getView(R.id.main_serve_children);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        MainServeChildrenAdapter adapter = new MainServeChildrenAdapter(R.layout.item_main_serve_child, item.sub);
        recyclerView.setAdapter(adapter);
        helper.setText(R.id.main_serve_parent, item.name);
        ImageView tagFlag = helper.getView(R.id.flag_s);
        if (helper.getPosition() % 3 == 0) {
            tagFlag.setBackgroundResource(R.color.common_orange);
        } else if (helper.getPosition() % 3 == 1) {
            tagFlag.setBackgroundResource(R.color.common_green);
        } else {
            tagFlag.setBackgroundResource(R.color.night_blue);
        }
        ImageView imageView = helper.getView(R.id.main_serve_parent_picture);
        GlideUtils.loadImage(mContext, item.icon, imageView);
        adapter.setOnItemClickListener((adapter1, view, position) -> {
            CategoryChild categoryChild = item.sub.get(position);
            if (categoryChild.services == 0 && !TextUtils.isEmpty(categoryChild.remind_image)) {
                showNoServeDialog(categoryChild);
                return;
            }
            Intent intent = new Intent(mContext, ServeCategoryListActivity.class);
            intent.putExtra(Constant.CATEGORY_ID, categoryChild.id);
            intent.putExtra(Constant.CATEGORY_NAME, categoryChild.name);
            mContext.startActivity(intent);
        });
    }

    private void showNoServeDialog(CategoryChild item) {
        LoadingDialog.getInstance().show(mContext, "", true);
        HomePictureDialog dialog = new HomePictureDialog(mContext, item.remind_image);
        dialog.show();
    }
}

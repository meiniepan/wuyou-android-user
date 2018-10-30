package com.wuyou.user.mvp.volunteer;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.gs.buluo.common.network.ApiException;
import com.gs.buluo.common.network.BaseSubscriber;
import com.gs.buluo.common.network.ErrorBody;
import com.wuyou.user.CarefreeDaoSession;
import com.wuyou.user.Constant;
import com.wuyou.user.data.EoscDataManager;
import com.wuyou.user.data.api.ListRowResponse;
import com.wuyou.user.data.api.VolunteerProjectBean;
import com.wuyou.user.data.api.VolunteerRecordBean;
import com.wuyou.user.util.RxUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;

/**
 * Created by DELL on 2018/10/29.
 */

public class TimeBankPresenter extends TimeBankRecordContract.Presenter {


    public void attendVolunteerProject() {
        EoscDataManager.getIns().registTimeBank("", "", "")
                .compose(RxUtil.switchSchedulers())
                .subscribe(new BaseSubscriber<JsonObject>() {
                    @Override
                    public void onSuccess(JsonObject jsonObject) {
                        Log.e("Carefree", "onSuccess: " + jsonObject);
                    }
                });
    }


    @Override
    void registerProject(int position, VolunteerProjectBean bean) {
        EoscDataManager.getIns().registTimeBank(bean.id + "", bean.creator, bean.name)
                .compose(RxUtil.switchSchedulers())
                .subscribe(new BaseSubscriber<JsonObject>() {
                    @Override
                    public void onSuccess(JsonObject jsonObject) {
                        Log.e("Carefree", "onSuccess: " + jsonObject);
                        mView.registerSuccess(position);
                    }
                });
    }

    @Override
    void rewardProject(int position, VolunteerProjectBean bean) {
        EoscDataManager.getIns().registTimeBank(bean.id + "", bean.creator, bean.name)
                .compose(RxUtil.switchSchedulers())
                .subscribe(new BaseSubscriber<JsonObject>() {
                    @Override
                    public void onSuccess(JsonObject jsonObject) {
                        Log.e("Carefree", "onSuccess: " + jsonObject);
                        mView.registerSuccess(position);
                    }
                });
    }

    @Override
    void getRecordData() {
        Observable.zip(EoscDataManager.getIns().getTable(CarefreeDaoSession.getInstance().getMainAccount().getName(), Constant.EOS_TIME_BANK, "infos"),
                EoscDataManager.getIns().getTable("samkunnbanb1", Constant.EOS_TIME_BANK, "task"),
                (BiFunction<String, String, List<String>>) (recordData, all) -> {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(recordData);
                    list.add(all);
                    return list;
                })
                .compose(RxUtil.switchSchedulers())
                .subscribe(new BaseSubscriber<List<String>>() {
                    @Override
                    public void onSuccess(List<String> strings) {
                        ListRowResponse<VolunteerRecordBean> recordResponse = new Gson().fromJson(strings.get(0), new TypeToken<ListRowResponse<VolunteerRecordBean>>() {
                        }.getType());
                        ListRowResponse<VolunteerProjectBean> projectResponse = new Gson().fromJson(strings.get(1), new TypeToken<ListRowResponse<VolunteerProjectBean>>() {
                        }.getType());
                        if (recordResponse.rows.size() == 0) {
                            mView.getAttendingDataSuccess(Collections.emptyList());
                            mView.getFinishAttendDataSuccess(Collections.emptyList());
                        }
                        ArrayList<VolunteerProjectBean> attendingData = new ArrayList<>();
                        ArrayList<VolunteerProjectBean> attendedData = new ArrayList<>();
                        for (VolunteerRecordBean.Participated participated : recordResponse.rows.get(0).enrolled) {
                            if (participated.registered == 0 && participated.rewards == 0) {
                                attendingData.add(projectResponse.rows.get(participated.id));
                            } else if (participated.registered == 1 && participated.rewards == 0) {
                                attendedData.add(projectResponse.rows.get(participated.id));
                            }
                        }
                        mView.getAttendingDataSuccess(attendingData);
                        mView.getFinishAttendDataSuccess(attendedData);
                    }

                    @Override
                    protected void onFail(ApiException e) {
                        mView.showError(e.getDisplayMessage(), e.getCode());
                    }

                    @Override
                    protected void onNodeFail(int code, ErrorBody.DetailErrorBean message) {
                        mView.showError(message.message, code);
                    }
                });
    }
}

package com.wuyou.user.network.apis;

import android.support.v4.util.ArrayMap;

import com.gs.buluo.common.network.BaseResponse;
import com.gs.buluo.common.network.SortedTreeMap;
import com.wuyou.user.bean.ServeDetailBean;
import com.wuyou.user.bean.ServeLevelBean;
import com.wuyou.user.bean.response.CategoryListResponse;
import com.wuyou.user.bean.response.ListResponse;
import com.wuyou.user.bean.response.ServeListResponse;
import com.wuyou.user.bean.response.ServeTimeBean;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by hjn on 2018/2/6.
 */

public interface ServeApis {
    @GET("category/list/{community_id}")
    Observable<BaseResponse<CategoryListResponse>> getCategoryList(@Path("community_id") String communityId, @QueryMap SortedTreeMap<String, String> map);

    @GET("services")
    Observable<BaseResponse<ServeListResponse>> getServeList(@QueryMap SortedTreeMap<String, String> map);

    @GET("service/{service_id}")
    Observable<BaseResponse<ServeDetailBean>> getServeDetail(@Path("service_id") String id, @QueryMap SortedTreeMap<String, String> map);

    @GET("service/levels/{category_id}")
    Observable<BaseResponse<ListResponse<ServeLevelBean>>> getFastServeLevel(@Path("category_id") String id, @QueryMap SortedTreeMap<String, String> map);

    @GET("service_times")
    Observable<BaseResponse<ArrayMap<String, List<ServeTimeBean>>>> getAvailableServeTime(@QueryMap SortedTreeMap<String, String> map);
}
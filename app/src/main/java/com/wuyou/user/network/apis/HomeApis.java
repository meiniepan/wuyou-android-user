package com.wuyou.user.network.apis;

import com.gs.buluo.common.network.BaseResponse;
import com.gs.buluo.common.network.SortedTreeMap;
import com.wuyou.user.data.remote.HomeVideoBean;
import com.wuyou.user.data.remote.ServeSites;
import com.wuyou.user.data.remote.response.CommunityListResponse;
import com.wuyou.user.data.remote.response.ListResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by Administrator on 2018\1\26 0026.
 */

public interface HomeApis {
    @GET("v1/communities")
    Observable<BaseResponse<CommunityListResponse>> getCommunitiesList(@QueryMap SortedTreeMap<String, String> map);

    @GET("v2/publicity/video")
    Observable<BaseResponse<HomeVideoBean>> getVideos(@QueryMap SortedTreeMap<String, String> map);


    @GET("v1/sites")
    Observable<BaseResponse<ListResponse<ServeSites>>> getServeSites(@QueryMap SortedTreeMap<String, String> map);
}

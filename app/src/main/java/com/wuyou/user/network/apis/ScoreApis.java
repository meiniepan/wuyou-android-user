package com.wuyou.user.network.apis;

import com.gs.buluo.common.network.BaseResponse;
import com.gs.buluo.common.network.SortedTreeMap;
import com.wuyou.user.data.remote.ActivityRecordBean;
import com.wuyou.user.data.remote.PointBean;
import com.wuyou.user.data.remote.ScoreRecordBean;
import com.wuyou.user.data.remote.SignRecordBean;
import com.wuyou.user.data.remote.response.ListResponse;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by hjn on 2018/2/6.
 */

public interface ScoreApis {
    @GET("v1/received_points/{uid}")
    Observable<BaseResponse<ListResponse<ScoreRecordBean>>> getScoreRecordList(@Path("uid") String uid,
                                                                               @QueryMap SortedTreeMap<String, String> map);
    @FormUrlEncoded
    @POST("v1/sign")
    Observable<BaseResponse<PointBean>> signIn(
            @FieldMap SortedTreeMap<String, String> map);

    @GET("v1/sign/list/{uid}")
    Observable<BaseResponse<ListResponse<SignRecordBean>>> getSignInRecord(@Path("uid") String uid, @QueryMap SortedTreeMap<String, String> map);

    @GET("v1/activity_orders/{uid}")
    Observable<BaseResponse<ListResponse<ActivityRecordBean>>> getActivityRecord(@Path("uid") String uid, @QueryMap SortedTreeMap<String, String> mao);
}

package com.wuyou.user.network;

import android.text.TextUtils;
import android.util.Base64;

import com.gs.buluo.common.BaseApplication;
import com.gs.buluo.common.network.EncryptUtil;
import com.gs.buluo.common.utils.Utils;
import com.wuyou.user.CarefreeDaoSession;
import com.wuyou.user.Constant;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hjn on 2016/11/10.
 */
public class CarefreeHttpInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request req = chain.request();
        Request.Builder builder = req.newBuilder();
        HttpUrl url = req.url();
        HttpUrl newBaseUrl = HttpUrl.parse(Constant.BASE_URL);
        HttpUrl newFullUrl = url.newBuilder()
                .host(newBaseUrl.host())
                .port(newBaseUrl.port())
                .build();
        String query = newFullUrl.encodedQuery();
        if (!TextUtils.isEmpty(query) && !query.contains("sign=")) {
            HttpUrl.Builder newBuilder = newFullUrl.newBuilder();
            newBuilder.addQueryParameter("sign", EncryptUtil.getSha1(Base64.encode(query.getBytes(), Base64.NO_WRAP)).toUpperCase());
            newFullUrl = newBuilder.build();
        }
        if (CarefreeDaoSession.getInstance().getUserInfo() != null) {
            builder.addHeader("Authorization", CarefreeDaoSession.getInstance().getUserInfo().getToken());
        }
        Request request = builder.addHeader("Accept", "application/json").url(newFullUrl).addHeader("Content-Type", "application/json").
                addHeader("User-Agent", Utils.getDeviceInfo(BaseApplication.getInstance().getApplicationContext())).build();
        return chain.proceed(request);
    }
}

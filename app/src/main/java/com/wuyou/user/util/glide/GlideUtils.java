package com.wuyou.user.util.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.wuyou.user.R;

import java.security.MessageDigest;

import static com.bumptech.glide.load.Key.CHARSET;

/**
 * Created by hjn on 2016/11/1.
 */
public class GlideUtils {
    public static void loadImage(Context context, String url, final ImageView imageView) {
        if (url == null) return;
        Glide.with(context).load(url).apply(new RequestOptions().placeholder(R.mipmap.default_pic)).into(imageView);
    }

    public static void loadImageWithHolder(Context context, String url, final ImageView imageView, int holderRes) {
        if (url == null) return;
        Glide.with(context).load(url).apply(new RequestOptions().placeholder(holderRes)).into(imageView);
    }

    public static void loadImageNoHolder(Context context, String url, final ImageView imageView) {
        if (url == null) return;
        Glide.with(context).load(url).into(imageView);
    }

    public static void loadGifImage(Context context, String url, final ImageView imageView) {
        if (url == null) return;
        Glide.with(context).asGif().load(url).into(imageView);
    }

    public static void loadImageWithListener(Context context, String url, final ImageView imageView, OnLoadListener onLoadListener) {
        if (url == null) return;
        Glide.with(context).load(url).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                onLoadListener.onLoaded();
                return false;
            }
        }).into(imageView);
    }


    public static void loadImageNoHolder(Context context, String url, final ImageView imageView, boolean isCircle) {
        if (url == null) return;
        if (isCircle) {
            Glide.with(context).load(url).apply(new RequestOptions().apply(RequestOptions.circleCropTransform())).into(imageView);
        } else {
            loadImage(context, url, imageView);
        }
    }

    public static void loadImage(Context context, String url, ImageView imageView, boolean isCircle) {
        if (url == null) return;
        if (isCircle) {
            Glide.with(context).load(url).apply(new RequestOptions().placeholder(R.mipmap.default_pic).apply(RequestOptions.circleCropTransform())).into(imageView);
        } else {
            loadImage(context, url, imageView);
        }
    }


    public interface OnLoadListener {
        void onLoaded();
    }

    public static void loadImage(Context context, String url, ImageView imageView, int width, int height) {
        if (url == null) return;
        Glide.with(context).load(url).apply(RequestOptions.placeholderOf(R.mipmap.default_pic).override(width, height)).into(imageView);
    }


    public static byte[] loadRoundCornerImage(Context context, String url, ImageView imageView, int dp) {
        if (url == null) return null;
        GlideRoundTransform transformation = new GlideRoundTransform(context, dp, GlideRoundTransform.CornerType.ALL);
        RequestOptions options = new RequestOptions().optionalTransform(transformation);
        Glide.with(context).load(url).apply(options).into(imageView);
        return transformation.getBitmap();
    }

    //图片不是centerCrop 裁剪的加载方案
    public static void loadRoundCornerImageNotCenterCrop(Context context, String url, ImageView imageView) {
        if (url == null) return;
        RequestOptions options = new RequestOptions().optionalTransform(new GlideRoundTransform(context, 4, GlideRoundTransform.CornerType.ALL));
        Glide.with(context).load(url).apply(options).into(imageView);
    }


    //图片是centerCrop 裁剪的加载方案 ,此处因为centerCrop会覆盖掉 GlideTransform的效果
    public static void loadRoundCornerImage(Context context, String url, ImageView imageView) {
        if (url == null) return;
        RequestOptions options = new RequestOptions().placeholder(R.mipmap.default_pic).optionalTransform(new GlideCenterCropRoundTransform(context, 4, GlideCenterCropRoundTransform.CornerType.ALL));
        Glide.with(context).load(url).apply(options).into(imageView);
    }

    public static byte[] loadRoundCornerImageWithBitmap(Context context, String url, ImageView imageView) {
        if (url == null) return null;
        GlideRoundTransform transformation = new GlideRoundTransform(context, 4, GlideRoundTransform.CornerType.ALL);
        RequestOptions options = new RequestOptions().optionalTransform(transformation);
        Glide.with(context).load(url).apply(options).into(imageView);
        return transformation.getBitmap();
    }

    private static final int VERSION = 1;
    private static final String ID = "GlideRoundedCornersTransform." + VERSION;
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

    public static void loadWithBitmap(Context context, String url, ImageView imageView) {
        if (url == null) return;
        RequestOptions options = new RequestOptions();
        options.optionalTransform(new BitmapTransformation() {
            @Override
            protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap source, int outWidth, int outHeight) {
                if (source == null) {
                    return null;
                }
                int width = source.getWidth();
                int height = source.getHeight();
                Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);


                if (result == null) {
                    result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config
                            .ARGB_8888);
                }
                return result;
            }

            @Override
            public void updateDiskCacheKey(MessageDigest messageDigest) {
                messageDigest.update(ID_BYTES);
            }
        });
        Glide.with(context).load(url).apply(options).into(imageView);
    }
}

package com.financialcalculator.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;

public class GenericImageLoader {

    public static void loadImage(Context context, final ImageView imageView, String url,
                                 int ic_placeholder) {
        if (!TextUtils.isEmpty(url) && context != null) {
            try {
                Glide.with(context)
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).dontTransform()
                        .error(new ColorDrawable(Color.GRAY))
                        .into(imageView);
            } catch (Exception e) {

            }
        }
    }

    public static void loadImageAsBitmap(Context context, String url, CustomTarget<Bitmap> customTarget) {
        if (!TextUtils.isEmpty(url) && context != null) {
            try {
                Glide.with(context).asBitmap().load(url).skipMemoryCache(true).into(customTarget);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

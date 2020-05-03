package com.financialcalculator.utility;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.financialcalculator.R;

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
}

package com.financialcalculator.utility;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.financialcalculator.R;

public class GenericImageLoader {

    public static void loadImage(Context context, final ImageView imageView, String url,
                                 int ic_placeholder) {
        if (!TextUtils.isEmpty(url) && context != null) {
            try {
                Glide
                        .with(context)
                        .load(url)
                        .placeholder(R.mipmap.ic_launcher) // can also be a drawable
                        .error(R.mipmap.ic_launcher) // will be displayed if the image cannot be loaded
                        .into(imageView);
                /*GlideApp
                        .with(context)
                        .load("http://futurestud.io/non_existing_image.png")
                        .placeholder(R.mipmap.ic_launcher) // can also be a drawable
                        .error(R.mipmap.future_studio_launcher) // will be displayed if the image cannot be loaded
                        .into(imageViewError);
                GlideRequests glideRequests = GlideApp.with(context.getApplicationContext());
                RequestOptions requestOptions =
                        new RequestOptions().placeholder(ic_placeholder_bank)
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).dontTransform();
                glideRequests
                        .load(isAuth || CliqApplication.getInstance().environmentManager.getImageBasicAuthEnable() ? new GlideUrl(url, auth) : new GlideUrl(url))
                        .skipMemoryCache(true)
                        .apply(requestOptions)
                        .into((ImageView) imageView).clearOnDetach();*/
            } catch (Exception e) {

            }
        }
    }
}

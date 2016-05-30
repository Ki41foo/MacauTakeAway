package com.gvbyc.ki41foo.delivery;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.gvbyc.ki41foo.delivery.utils.UIUtils;

import java.lang.ref.WeakReference;


/**
 * Created by ki on 7/25/15.
 */
public class ImageLoader {
    private static final float multiSize = 1f;

    final static int defaultImg = R.drawable.logo_light;

    public static void loadCenterCorp(ImageView iv, String imgUrl) {
        Glide.with(UIUtils.getContext()).load(imgUrl).asBitmap()
                .placeholder(defaultImg)
                .centerCrop().sizeMultiplier(multiSize).animate(android.support.v7.appcompat.R.anim.abc_fade_in).into(iv);
    }

    public static void cancel(ImageView iv) {
        Glide.clear(iv);
    }
}

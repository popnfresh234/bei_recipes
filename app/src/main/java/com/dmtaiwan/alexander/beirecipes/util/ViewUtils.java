package com.dmtaiwan.alexander.beirecipes.util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ActionMenuView;
import android.widget.TextView;

/**
 * Created by Alexander on 10/28/2016.
 */

public class ViewUtils {

    public static int getActionBarSize(@NonNull Context context) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.actionBarSize, value, true);
        int actionBarSize = TypedValue.complexToDimensionPixelSize(
                value.data, context.getResources().getDisplayMetrics());
        return actionBarSize;
    }

    public static void animateToolbar(Toolbar toolbar, Activity activity) {
        // this is gross but toolbar doesn't expose it's children to animate them :(
        View t = toolbar.getChildAt(0);
        if (t != null && t instanceof TextView) {
            TextView title = (TextView) t;

            // fade in and space out the title.  Animating the letterSpacing performs horribly so
            // fake it by setting the desired letterSpacing then animating the scaleX ¯\_(ツ)_/¯
            title.setAlpha(0f);
            title.setScaleX(0.8f);

            title.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .setStartDelay(300)
                    .setDuration(900)
                    .setInterpolator(AnimUtils.getFastOutSlowInInterpolator(activity));
        }
        View amv = toolbar.getChildAt(1);
        if (amv != null & amv instanceof ActionMenuView) {
            ActionMenuView actions = (ActionMenuView) amv;
            popAnim(actions.getChildAt(0), 500, 200, activity); // filter
            popAnim(actions.getChildAt(1), 700, 200, activity); // overflow
        }
    }


    private static void popAnim(View v, int startDelay, int duration, Activity activity) {
        if (v != null) {
            v.setAlpha(0f);
            v.setScaleX(0f);
            v.setScaleY(0f);

            v.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setStartDelay(startDelay)
                    .setDuration(duration)
                    .setInterpolator(AnimationUtils.loadInterpolator(activity,
                            android.R.interpolator.overshoot));
        }
    }
}

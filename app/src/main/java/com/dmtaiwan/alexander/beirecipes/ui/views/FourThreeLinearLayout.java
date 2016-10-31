package com.dmtaiwan.alexander.beirecipes.ui.views;

import android.content.Context;
import android.support.design.internal.ForegroundLinearLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Alexander on 10/28/2016.
 */

public class FourThreeLinearLayout extends ForegroundLinearLayout {

    public FourThreeLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int fourThreeHeight = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthSpec) * 3 / 4,
                View.MeasureSpec.EXACTLY);
        super.onMeasure(widthSpec, fourThreeHeight);
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }
}


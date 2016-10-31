package com.dmtaiwan.alexander.beirecipes.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by Alexander on 10/29/2016.
 */

public class Utils
{

    public static final String EXTRA_RECIPES = "com.dmtaiwan.alexander.extra.recipe";
    public static final String EXTRA_RECIPE_POSITION = "com.dmtaiwan.alexander.extra.position";
    public static final String EXTRA_NEW_RECIPE = "com.dmtaiwan.alexander.extra.newrecipe";


    public static String formatNumber(double number) {
        DecimalFormat decimalFormat = new DecimalFormat("0");
        decimalFormat.setRoundingMode(RoundingMode.UP);
        return decimalFormat.format(number);

    }
}

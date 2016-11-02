package com.dmtaiwan.alexander.beirecipes.util;

import com.dmtaiwan.alexander.beirecipes.data.Recipe;

import java.util.Comparator;



/**
 * Created by Alexander on 3/29/2016.
 */
public class RecipeComparator implements Comparator<Recipe> {
    @Override
    public int compare(Recipe lhs, Recipe rhs) {
       return lhs.getName().compareTo(rhs.getName());
    }


}


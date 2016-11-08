package com.dmtaiwan.alexander.beirecipes.data;

import android.content.Context;

import com.dmtaiwan.alexander.beirecipes.util.Utils;

import java.util.ArrayList;

/**
 * Created by Alexander on 11/1/2016.
 */

public class Cookbook {
    private static Cookbook cookbook;
    private ArrayList<Recipe> recipes;

    private Cookbook(Context context) {
        recipes = getRecipesFromStorage(context);
    }

    public ArrayList<Recipe> getRecipes() {
        return recipes;
    }

    public static Cookbook get(Context context) {
        if (cookbook == null) {
            cookbook = new Cookbook(context);
        }
        return cookbook;
    }


    private ArrayList<Recipe> getRecipesFromStorage(Context context) {
        if (Utils.doesRecipeFileExist(context)) {
            //get data from SD card
            String json = Utils.readRecipesFromFile(context);
            return Utils.recipesFromJson(json);

        } else return new ArrayList<>();
    }

    public void updateRecipes(ArrayList<Recipe> recipes) {
        this.recipes = recipes;
    }
}

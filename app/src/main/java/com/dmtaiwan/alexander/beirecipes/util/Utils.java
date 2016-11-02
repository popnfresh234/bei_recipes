package com.dmtaiwan.alexander.beirecipes.util;

import android.content.Context;

import com.dmtaiwan.alexander.beirecipes.data.Recipe;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Alexander on 10/29/2016.
 */

public class Utils
{

    public static final String EXTRA_RECIPE = "com.dmtaiwan.alexander.extra.recipe";
    public static final String EXTRA_RECIPES ="com.dmtaiwan.alexander.extra.recipes";
    public static final String EXTRA_RECIPE_POSITION = "com.dmtaiwan.alexander.extra.position";
    public static final String EXTRA_NEW_RECIPE = "com.dmtaiwan.alexander.extra.newrecipe";

    public static final String FILE_NAME_RECIPES = "recipes.json";

    //formats numbers for display
    public static String formatNumber(double number) {
        DecimalFormat decimalFormat = new DecimalFormat("0");
        decimalFormat.setRoundingMode(RoundingMode.UP);
        return decimalFormat.format(number);

    }

    //checks if file exists
    static public boolean doesRecipeFileExist(Context context) {
        File file = context.getFileStreamPath(FILE_NAME_RECIPES);
        return file.exists();
    }

    //Gets json from file
    public static String readRecipesFromFile(Context context) {
        String json = "";
        try {
            InputStream inputStream = context.openFileInput(FILE_NAME_RECIPES);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                json = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    //Writes recipes to file
    public static void writeRecipesToFile(String json,  Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(FILE_NAME_RECIPES, Context.MODE_PRIVATE));
            outputStreamWriter.write(json);
            outputStreamWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Get recipes from JSON
    public static ArrayList<Recipe> recipesFromJson(String json) {
        Type type = new TypeToken<ArrayList<Recipe>>(){}.getType();
        ArrayList<Recipe> recipeList = new Gson().fromJson(json, type);
        return recipeList;
    }
}

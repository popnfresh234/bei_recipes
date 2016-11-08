package com.dmtaiwan.alexander.beirecipes.util;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.dmtaiwan.alexander.beirecipes.data.Cookbook;
import com.dmtaiwan.alexander.beirecipes.data.Ingredient;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Alexander on 10/29/2016.
 */

public class Utils {

    public static final String EXTRA_RECIPE = "com.dmtaiwan.alexander.extra.recipe";
    public static final String EXTRA_RECIPES = "com.dmtaiwan.alexander.extra.recipes";
    public static final String EXTRA_RECIPE_POSITION = "com.dmtaiwan.alexander.extra.position";
    public static final String EXTRA_NEW_RECIPE = "com.dmtaiwan.alexander.extra.newrecipe";

    public static final int REQUEST_IMAGE_CAPTURE = 1;

    public static final String FILE_NAME_RECIPES = "recipes.json";

    //formats numbers for display
    public static String formatNumber(double number) {
        if (number != 0) {
            DecimalFormat decimalFormat = new DecimalFormat("#");
            return decimalFormat.format(number);
        } else return "";


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
    public static void writeRecipesToFile(String json, Context context) {
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
        Type type = new TypeToken<ArrayList<Recipe>>() {
        }.getType();
        ArrayList<Recipe> recipeList = new Gson().fromJson(json, type);
        return recipeList;
    }

    public static void writeDataUpdateCookbook(ArrayList<Recipe> recipes, String jsonList, Context context) {
        Utils.writeRecipesToFile(jsonList, context);
        Cookbook cookbook = Cookbook.get(context);
        cookbook.updateRecipes(recipes);
    }

    public static double getRatio(double baseValue, Ingredient ingredient) {

        return 0;
    }

    public static double fractionToDouble(String fraction) {
        double value;
        switch (fraction) {
            case "⅛":
                value = 1d / 8d;
                break;
            case "¼":
                value = 1d / 4d;
                break;
            case "⅓":
                value = 1d / 3d;
                break;
            case "½":
                value = 1d / 2d;
                break;
            case "⅔":
                value = 2d / 3d;
                break;
            case "¾":
                value = 3d / 4d;
                break;
            default:
                value = 0;
        }
        return value;
    }

    public static String doubleToFraction(double value) {
        String fraction = "";
        DecimalFormat format = new DecimalFormat(".00");
        String formattedValue = format.format(value);
        switch (formattedValue) {
            case ".00":
                fraction = "";
                break;
            case ".12":
                fraction = "⅛";
                break;
            case ".25":
               fraction = "¼";
                break;
            case ".33":
                fraction = "⅓";
                break;
            case ".50":
                fraction = "½";
                break;
            case ".67":
                fraction = "⅔";
                break;
            case ".75":
                fraction = "¾";
                break;
            default:
                fraction = formattedValue;
                QuickLog.i("FORMATTED VALUE" + formattedValue);
                break;

        }

        return fraction;
    }

    public static double getWholeNumber(double count) {
        double fraction = count % 1;
        double wholeNumber = count - fraction;
        return wholeNumber;
    }

    public static double getFraction(double count) {
        double fraction = count % 1;
        return fraction;
    }

    public static File getOutputMediaFile(UUID uuid){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Bei Recipes");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ uuid.toString() + ".jpg");
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}

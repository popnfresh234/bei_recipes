package com.dmtaiwan.alexander.beirecipes.data;

import android.content.Context;

import com.dmtaiwan.alexander.beirecipes.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Alexander on 11/1/2016.
 */

public class Cookbook {
    private static Cookbook cookbook;
    private List<Recipe> recipes;

    private Cookbook(Context context) {
        recipes = getRecipesFromStorage();
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public static Cookbook get(Context context) {
        if (cookbook == null) {
            cookbook = new Cookbook(context);
        }
        return cookbook;
    }

    private List<Recipe> getRecipesFromStorage() {
        //Dummy recipe data
        List<Recipe> recipeList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Recipe recipe = Recipe.newRecipe("This is recipe #" + String.valueOf(i));

            //Dummy ingredient data
            List<Ingredient> ingredients = new ArrayList<>();
            for (int j = 0; j < 6; j++) {
                Ingredient ingredient = new Ingredient();
                ingredient.setName("Ingredient #" + String.valueOf(j));
                double start = 0;
                double end = 100;
                double random = new Random().nextDouble();
                double result = start + (random * (end - start));
                ingredient.setCount(result);
                ingredient.setUnit("mg");
                ingredient.setProportionalCount(0);
                ingredients.add(ingredient);
            }
            recipe.setIngredients(ingredients);
            //End dummy ingredient data

            recipeList.add(recipe);
            if (i % 3 == 0) {
                recipe.setDrawableId(R.drawable.food);
            }
        }
        //End dummy recipe data
        return recipeList;
    }
}

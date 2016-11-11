package com.dmtaiwan.alexander.beirecipes.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Alexander on 10/28/2016.
 */

public class Recipe implements Parcelable {

    public Recipe() {
    }

    public static Recipe newRecipe(String name) {
        Recipe recipe = new Recipe();
        recipe.name = name;
        recipe.imageUri = null;
        recipe.id = UUID.randomUUID();
        return recipe;
    }

    public static Recipe newRecipeWithoutData() {
        Recipe recipe = new Recipe();
        recipe.imageUri = null;
        recipe.id = UUID.randomUUID();
        return recipe;
    }

    public static Recipe newRecipe(String name, List<Ingredient> ingredients) {
        Recipe recipe = new Recipe();
        recipe.ingredients = ingredients;
        recipe.name = name;
        recipe.imageUri = null;
        recipe.id = UUID.randomUUID();
        return recipe;
    }

    private String name;
    private UUID id;
    private String imageUri;
    private List<Ingredient> ingredients;
    private List<Direction> directions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public List<Ingredient> getIngredients() {
        if (ingredients != null) {
            return ingredients;
        } else return new ArrayList<>();

    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Direction> getDirections() {
        if (directions != null) {
            return directions;
        } else return new ArrayList<>();
    }

    public void setDirections(List<Direction> directions) {
        this.directions = directions;
    }

    public UUID getId() {
        return id;
    }



    protected Recipe(Parcel in) {
        name = in.readString();
        id = (UUID) in.readValue(UUID.class.getClassLoader());
        imageUri = in.readString();
        if (in.readByte() == 0x01) {
            ingredients = new ArrayList<Ingredient>();
            in.readList(ingredients, Ingredient.class.getClassLoader());
        } else {
            ingredients = null;
        }
        if (in.readByte() == 0x01) {
            directions = new ArrayList<Direction>();
            in.readList(directions, Direction.class.getClassLoader());
        } else {
            directions = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeValue(id);
        dest.writeString(imageUri);
        if (ingredients == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(ingredients);
        }
        if (directions == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(directions);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
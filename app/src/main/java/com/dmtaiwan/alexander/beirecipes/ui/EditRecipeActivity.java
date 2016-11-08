package com.dmtaiwan.alexander.beirecipes.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dmtaiwan.alexander.beirecipes.R;
import com.dmtaiwan.alexander.beirecipes.data.Cookbook;
import com.dmtaiwan.alexander.beirecipes.data.Ingredient;
import com.dmtaiwan.alexander.beirecipes.data.Recipe;
import com.dmtaiwan.alexander.beirecipes.ui.adapters.EditIngredientAdapter;
import com.dmtaiwan.alexander.beirecipes.ui.views.FontEditText;
import com.dmtaiwan.alexander.beirecipes.util.PermissionUtil;
import com.dmtaiwan.alexander.beirecipes.util.QuickLog;
import com.dmtaiwan.alexander.beirecipes.util.RecipeComparator;
import com.dmtaiwan.alexander.beirecipes.util.Utils;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Alexander on 10/29/2016.
 */

public class EditRecipeActivity extends AppCompatActivity implements EditIngredientAdapter.AdapterListener, View.OnClickListener {


    private Context context = this;
    private Recipe recipe;
    private Boolean newIngredient = false;
    private Ingredient ingredient;
    private EditIngredientAdapter adapter;
    private ArrayList<Recipe> recipes;
    private int recipePosition;
    private Boolean newRecipe;
    private Uri currentPhotoUri = null;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.button_add_ingredient)
    Button addIngredientButton;

    @BindView(R.id.button_add_image)
    Button addImageButton;

    @BindView(R.id.button_save)
    Button saveButton;

    @BindView(R.id.edit_text_title)
    FontEditText titleEditText;

    @BindView(R.id.recycler_view_ingredients)
    RecyclerView recyclerView;


    //Ingredient Dialog Views
    private EditText dialogCountEditText;
    private EditText dialogIngredientNameEditText;
    private Spinner dialogUnitSpinner;
    private Spinner dialogFractionSpinner;
    private Button dialogAddIngredientButton;
    private Button dialogCancelIngredientButton;
    private Button dialogRemoveIngredientButton;


    private InputMethodManager imm;

    //Setup ArrayList of Ingredients and Direction
    private List<Ingredient> ingredients = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);
        ButterKnife.bind(this);

        //Get recipes
        Cookbook cookbook = Cookbook.get(this);
        recipes = cookbook.getRecipes();


        //Setup Recycler Views
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        adapter = new EditIngredientAdapter(this, this, null);
        recyclerView.setAdapter(adapter);

        if (getIntent() != null) {
            recipePosition = getIntent().getIntExtra(Utils.EXTRA_RECIPE_POSITION, -1);
            newRecipe = getIntent().getBooleanExtra(Utils.EXTRA_NEW_RECIPE, true);

            //If position passed with intent, populate recipe
            if (recipePosition != -1) {
                recipe = recipes.get(recipePosition);
                populateRecipe();
            } else {
                //create a new recipe
                recipe = Recipe.newRecipeWithoutData();
                ingredients = recipe.getIngredients();
                adapter.setData(ingredients);
            }
        }

        //Get input method manager
        imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);


        //Setup listeners
        addIngredientButton.setOnClickListener(this);
        addImageButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);


    }


    private void populateRecipe() {
        //Load Ingredients
        ingredients = recipe.getIngredients();
        recyclerView.setVisibility(View.VISIBLE);
        adapter.setData(ingredients);
        //Set Title
        titleEditText.setText(recipe.getName());
    }


    private void CreateIngredientDialog(Ingredient ingredient, final int position) {

        if (ingredient == null) {
            this.ingredient = new Ingredient();
            newIngredient = true;
        } else {
            this.ingredient = ingredient;
            newIngredient = false;
        }

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_ingredient);

        //Setup EditTexts
        dialogCountEditText = (EditText) dialog.findViewById(R.id.dialog_edit_text_quantity);
        if (!newIngredient && this.ingredient.getCount() != 0) {
            String stringQuantity = Utils.formatNumber(Utils.getWholeNumber(this.ingredient.getCount()));
            dialogCountEditText.setText(stringQuantity);
        }

        dialogIngredientNameEditText = (EditText) dialog.findViewById(R.id.dialog_edit_text_ingredient_name);
        if (!newIngredient) {
            dialogIngredientNameEditText.setText(this.ingredient.getName());
        }

        //Setup fraction spinner
        dialogFractionSpinner = (Spinner) dialog.findViewById(R.id.dialog_spinner_fractions);
        ArrayAdapter<CharSequence> fractionSpinnerAdapter = ArrayAdapter.createFromResource(context, R.array.dialog_spinner_fractions, R.layout.dialog_spinner_item);
        fractionSpinnerAdapter.setDropDownViewResource(R.layout.dialog_spinner_layout);
        dialogFractionSpinner.setAdapter(fractionSpinnerAdapter);
        if (!newIngredient) {


            String compareValue = Utils.doubleToFraction(Utils.getFraction(this.ingredient.getCount()));
            if (!compareValue.equals(null)) {
                int spinnerPosition = fractionSpinnerAdapter.getPosition(compareValue);
                dialogFractionSpinner.setSelection(spinnerPosition);
                spinnerPosition = 0;
            }
        }

        //Setup unit spinner
        dialogUnitSpinner = (Spinner) dialog.findViewById(R.id.dialog_spinner_units);
        ArrayAdapter<CharSequence> unitSpinnerAdapter = ArrayAdapter.createFromResource(context, R.array.dialog_spinner_units, R.layout.dialog_spinner_item);
        unitSpinnerAdapter.setDropDownViewResource(R.layout.dialog_spinner_layout);
        dialogUnitSpinner.setAdapter(unitSpinnerAdapter);
        if (!newIngredient && this.ingredient.getUnit() != null) {
            String compareValue = this.ingredient.getUnit();
            if (!compareValue.equals(null)) {
                int spinnerPosition = unitSpinnerAdapter.getPosition(compareValue);
                dialogUnitSpinner.setSelection(spinnerPosition);
                spinnerPosition = 0;
            }
        }

        //Add ingredient button
        dialogAddIngredientButton = (Button) dialog.findViewById(R.id.dialog_button_add_ingredient);
        if (!newIngredient) dialogAddIngredientButton.setText("Update");

        dialogAddIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newIngredient = false;
                double count = 0;
                imm.hideSoftInputFromWindow(dialogCountEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(dialogIngredientNameEditText.getWindowToken(), 0);
                if (!dialogIngredientNameEditText.getText().toString().equals("")) {

                    if (!dialogCountEditText.getText().toString().equals("")) {
                        count = Double.valueOf(dialogCountEditText.getText().toString().trim());
                    }
                    //Fraction Spinner

                    if (!dialogFractionSpinner.getSelectedItem().toString().equals(getResources().getStringArray(R.array.dialog_spinner_fractions)[0])) {
                        double fraction = Utils.fractionToDouble(dialogFractionSpinner.getSelectedItem().toString());
                        count = count + fraction;
                    }
                    EditRecipeActivity.this.ingredient.setCount(count);

                    //Unit Spinner
                    if (dialogUnitSpinner.getSelectedItemPosition() == 0) {
                        EditRecipeActivity.this.ingredient.setUnit(null);
                    }
                    if (!dialogUnitSpinner.getSelectedItem().toString().equals(getResources().getStringArray(R.array.dialog_spinner_units)[0])) {
                        EditRecipeActivity.this.ingredient.setUnit(dialogUnitSpinner.getSelectedItem().toString());
                    }


                    if (!dialogIngredientNameEditText.getText().toString().equals("")) {
                        EditRecipeActivity.this.ingredient.setName(dialogIngredientNameEditText.getText().toString().toLowerCase().trim());
                    }

                    dialog.dismiss();

                    if (position == -1) {
                        Log.i("ADDING", "adding");
                        ingredients.add(EditRecipeActivity.this.ingredient);
                    } else {
                        ingredients.set(position, EditRecipeActivity.this.ingredient);
                    }
                    if (ingredients.size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);

                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Please enter an ingredient", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Remove Ingredient Button
        dialogRemoveIngredientButton = (Button) dialog.findViewById(R.id.dialog_button_remove);
        if (!newIngredient) dialogRemoveIngredientButton.setVisibility(View.VISIBLE);
        dialogRemoveIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingredients.remove(position);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });


        //Setup cancel button
        dialogCancelIngredientButton = (Button) dialog.findViewById(R.id.dialog_button_cancel);
        dialogCancelIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_save:
                saveRecipes();
                break;
            case R.id.button_add_image:
                //Check for permissions
                if (PermissionUtil.checkPermissions(this)) {
                    //Permissions have not been granted
                    PermissionUtil.requestExternalStoragePermissions(EditRecipeActivity.this, coordinatorLayout);
                } else {
                    launchCamera();
                }
                break;
            case R.id.button_add_ingredient:
                CreateIngredientDialog(null, -1);
                break;
            default:
                break;
        }
    }


    private void saveRecipes() {
        Gson gson = new Gson();
        String jsonList = "";

        //Editing Recipe
        if (!newRecipe) {
            recipe.setName(titleEditText.getText().toString());
            recipe.setIngredients(ingredients);
            if (currentPhotoUri != null) {
                recipe.setImageUri(currentPhotoUri.toString());
            }
            recipes.set(recipePosition, recipe);
            Collections.sort(recipes, new RecipeComparator());
            jsonList = gson.toJson(recipes);
            Utils.writeDataUpdateCookbook(recipes, jsonList, this);
        } else {
            //New recipe
            recipe.setName(titleEditText.getText().toString());
            recipe.setIngredients(ingredients);
            if (currentPhotoUri != null) {
                recipe.setImageUri(currentPhotoUri.toString());
            }
            recipes.add(recipe);
            Collections.sort(recipes, new RecipeComparator());
            jsonList = gson.toJson(recipes);
            Utils.writeDataUpdateCookbook(recipes, jsonList, this);
        }
        finish();
    }


    @Override
    public void onMoveIngredientUpClicked(int position) {
        int newPosition = position - 1;
        if (newPosition < 0) return;
        Ingredient ingredient = ingredients.get(position);
        ingredients.remove(position);
        adapter.notifyItemChanged(position);
        ingredients.add(newPosition, ingredient);
        adapter.notifyItemChanged(newPosition);
        adapter.notifyItemMoved(position, newPosition);
    }

    @Override
    public void onIngredientCardViewClicked(int position) {
        Ingredient ingredient = ingredients.get(position);
        CreateIngredientDialog(ingredient, position);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtil.REQUEST_STORAGE) {
            QuickLog.i("Received respnse for Storage permission request");
            //Check if all the permissions have been granted
            if (PermissionUtil.verifyPermissions(grantResults)) {
                launchCamera();
            }
        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //Create file to store photo
            currentPhotoUri = Uri.fromFile(Utils.getOutputMediaFile(recipe.getId()));
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);
            startActivityForResult(takePictureIntent, Utils.REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Utils.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //scale down image
            try {
                Bitmap bitmap = rotateBitmapOrientation(Utils.getOutputMediaFile(recipe.getId()).toString());
                bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 3, bitmap.getHeight() / 3, false);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                File f = Utils.getOutputMediaFile(recipe.getId());
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            galleryAddPic();

        }
    }

    public Bitmap rotateBitmapOrientation(String photoFilePath) throws IOException {

        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = new ExifInterface(photoFilePath);
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(currentPhotoUri);
        this.sendBroadcast(mediaScanIntent);
    }

}

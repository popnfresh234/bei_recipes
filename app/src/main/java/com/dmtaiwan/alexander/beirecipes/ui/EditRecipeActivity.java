package com.dmtaiwan.alexander.beirecipes.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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
import com.dmtaiwan.alexander.beirecipes.data.Ingredient;
import com.dmtaiwan.alexander.beirecipes.data.Recipe;
import com.dmtaiwan.alexander.beirecipes.ui.adapters.EditIngredientAdapter;
import com.dmtaiwan.alexander.beirecipes.util.RecipeComparator;
import com.dmtaiwan.alexander.beirecipes.util.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Alexander on 10/29/2016.
 */

public class EditRecipeActivity extends AppCompatActivity implements EditIngredientAdapter.AdapterListener, View.OnClickListener {



    private Context mContext = this;
    private Recipe mRecipe;
    private Boolean mNewIngredient = false;
    private Ingredient mIngredient;
    private EditIngredientAdapter mIngredientAdapter;
    private ArrayList<Recipe> mRecipeList;
    private int mRecipePosition;
    private Boolean mNewRecipe;


    @BindView(R.id.button_add_ingredient)
    Button mAddIngredientButton;

    @BindView(R.id.button_save)
    Button mSaveButton;

    @BindView(R.id.edit_text_title)
    EditText mTitleEditText;

    @BindView(R.id.recycler_view_ingredients)
    RecyclerView mIngredientsRecyclerView;


    //Ingredient Dialog Views
    private EditText mDialogQuantityEditText;
    private EditText mDialogIngredientNameEditText;
    private Spinner mDialogUnitSpinner;
    private Button mDialogAddIngredientButton;
    private Button mDialogCancelIngredientButton;
    private Button mDialogRemoveIngredientButton;


    private InputMethodManager mImm;

    //Setup ArrayList of Ingredients and Direction
    private ArrayList<Ingredient> mIngredientList = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);
        ButterKnife.bind(this);


        mIngredientList = new ArrayList<Ingredient>();

        if (getIntent() != null) {
            mRecipeList = getIntent().getParcelableArrayListExtra(Utils.EXTRA_RECIPES);
            mRecipePosition = getIntent().getIntExtra(Utils.EXTRA_RECIPE_POSITION, -1);
            mNewRecipe = getIntent().getBooleanExtra(Utils.EXTRA_NEW_RECIPE, true);
        }

        //Get input method manager
        mImm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);

        //Setup Recycler Views

        mIngredientsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mIngredientsRecyclerView.setLayoutManager(llm);
        mIngredientAdapter = new EditIngredientAdapter(this, this, mIngredientList);
        mIngredientsRecyclerView.setAdapter(mIngredientAdapter);


        mAddIngredientButton.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);

        //If position passed with intent, populate recipe
        if (mRecipePosition != -1) {
            Recipe recipe = mRecipeList.get(mRecipePosition);
            populateRecipe(recipe);
        }
    }


    private void populateRecipe(Recipe recipe) {
        mRecipe = recipe;
        //Load Ingredients
        List<Ingredient> tempIngredientList = recipe.getIngredients();
        if (tempIngredientList.size() > 0) {
            for (int i = 0; i < tempIngredientList.size(); i++) {
                mIngredientList.add(tempIngredientList.get(i));
            }
            mIngredientsRecyclerView.setVisibility(View.VISIBLE);
            mIngredientAdapter.notifyDataSetChanged();
        }
        //Set Title
        mTitleEditText.setText(recipe.getName());
    }


    private void CreateIngredientDialog(Ingredient ingredient, final int position) {

        if (ingredient == null) {
            mIngredient = new Ingredient();
            mNewIngredient = true;
        } else {
            mIngredient = ingredient;
        }

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_ingredient);

        //Setup EditTexts
        mDialogQuantityEditText = (EditText) dialog.findViewById(R.id.dialog_edit_text_quantity);
        if (!mNewIngredient && mIngredient.getCount() != 0) {
            double quantity = ingredient.getCount();
            String stringQuantity = Utils.formatNumber(quantity);
            mDialogQuantityEditText.setText(stringQuantity);
        }

        mDialogIngredientNameEditText = (EditText) dialog.findViewById(R.id.dialog_edit_text_ingredient_name);
        if (!mNewIngredient) {
            mDialogIngredientNameEditText.setText(mIngredient.getName());
        }

        //Setup unit spinner
        mDialogUnitSpinner = (Spinner) dialog.findViewById(R.id.dialog_spinner_units);
        ArrayAdapter<CharSequence> unitSpinnerAdapter = ArrayAdapter.createFromResource(mContext, R.array.dialog_spinner_units, R.layout.dialog_spinner_item);
        unitSpinnerAdapter.setDropDownViewResource(R.layout.dialog_spinner_layout);
        mDialogUnitSpinner.setAdapter(unitSpinnerAdapter);
        if (!mNewIngredient && mIngredient.getUnit() != null) {
            String compareValue = mIngredient.getUnit();
            if (!compareValue.equals(null)) {
                int spinnerPosition = unitSpinnerAdapter.getPosition(compareValue);
                mDialogUnitSpinner.setSelection(spinnerPosition);
                spinnerPosition = 0;
            }
        }

        //Add ingredient button
        mDialogAddIngredientButton = (Button) dialog.findViewById(R.id.dialog_button_add_ingredient);
        if (!mNewIngredient) mDialogAddIngredientButton.setText("Update");

        mDialogAddIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNewIngredient = false;
                mImm.hideSoftInputFromWindow(mDialogQuantityEditText.getWindowToken(), 0);
                mImm.hideSoftInputFromWindow(mDialogIngredientNameEditText.getWindowToken(), 0);
                if (!mDialogIngredientNameEditText.getText().toString().equals("")) {

                    if (!mDialogQuantityEditText.getText().toString().equals("")) {
                        mIngredient.setCount(Double.valueOf(mDialogQuantityEditText.getText().toString().trim()));
                    }

                    if (mDialogUnitSpinner.getSelectedItemPosition() == 0) {
                        mIngredient.setUnit(null);
                    }
                    if (!mDialogUnitSpinner.getSelectedItem().toString().equals("Units")) {
                        mIngredient.setUnit(mDialogUnitSpinner.getSelectedItem().toString());
                    }
                    if (!mDialogIngredientNameEditText.getText().toString().equals("")) {
                        mIngredient.setName(mDialogIngredientNameEditText.getText().toString().toLowerCase().trim());
                    }

                    dialog.dismiss();

                    if (position == -1) {
                        Log.i("ADDING", "adding");
                        mIngredientList.add(mIngredient);
                    } else {
                        mIngredientList.set(position, mIngredient);
                    }
                    if (mIngredientList.size() > 0) {
                        mIngredientsRecyclerView.setVisibility(View.VISIBLE);

                    }
                    mIngredientAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(mContext, "Please enter an ingredient", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Remove Ingredient Button
        mDialogRemoveIngredientButton = (Button) dialog.findViewById(R.id.dialog_button_remove);
        if (!mNewIngredient) mDialogRemoveIngredientButton.setVisibility(View.VISIBLE);
        mDialogRemoveIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIngredientList.remove(position);
                mIngredientAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });


        //Setup cancel button
        mDialogCancelIngredientButton = (Button) dialog.findViewById(R.id.dialog_button_cancel);
        mDialogCancelIngredientButton.setOnClickListener(new View.OnClickListener() {
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
        //Save recipe here
        //Editing Recipe
        if (!mNewRecipe) {
            mRecipe.setName(mTitleEditText.getText().toString());
            mRecipe.setIngredients(mIngredientList);
            mRecipeList.set(mRecipePosition, mRecipe);
            Collections.sort(mRecipeList, new RecipeComparator());
            jsonList = gson.toJson(mRecipeList);
            Utils.writeDataUpdateCookbook(mRecipeList, jsonList, this);
        } else if (mRecipeList != null) {
            mRecipe = Recipe.newRecipe(mTitleEditText.getText().toString(), mIngredientList);
            mRecipeList.add(mRecipe);
            Collections.sort(mRecipeList, new RecipeComparator());
            jsonList = gson.toJson(mRecipeList);
            Utils.writeDataUpdateCookbook(mRecipeList, jsonList, this);
        } else {
            //New Recipe
            mRecipeList = new ArrayList<Recipe>();
            mRecipe = Recipe.newRecipe(mTitleEditText.getText().toString(), mIngredientList);
            mRecipeList.add(mRecipe);
            Collections.sort(mRecipeList, new RecipeComparator());
            jsonList = gson.toJson(mRecipeList);
            Utils.writeDataUpdateCookbook(mRecipeList, jsonList, this);
        }
        finish();
    }



    @Override
    public void onMoveIngredientUpClicked(int position) {
        int newPosition = position - 1;
        if (newPosition < 0) return;
        Ingredient ingredient = mIngredientList.get(position);
        mIngredientList.remove(position);
        mIngredientAdapter.notifyItemChanged(position);
        mIngredientList.add(newPosition, ingredient);
        mIngredientAdapter.notifyItemChanged(newPosition);
        mIngredientAdapter.notifyItemMoved(position, newPosition);
    }

    @Override
    public void onIngredientCardViewClicked(int position) {
        Ingredient ingredient = mIngredientList.get(position);
        CreateIngredientDialog(ingredient, position);
    }
}

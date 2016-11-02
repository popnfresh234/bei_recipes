package com.dmtaiwan.alexander.beirecipes.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.dmtaiwan.alexander.beirecipes.util.QuickLog;
import com.dmtaiwan.alexander.beirecipes.util.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Alexander on 10/29/2016.
 */

public class EditRecipeActivity extends AppCompatActivity implements EditIngredientAdapter.AdapterListener, View.OnClickListener {

    private QuickLog quickLog;

    @BindView(R.id.button_add_ingredient)
    Button addIngredientButton;

    @BindView(R.id.button_save)
    Button saveButton;

    @BindView(R.id.edit_text_title)
    EditText titleEditText;

    @BindView(R.id.recycler_view_ingredients)
    RecyclerView ingredientsRecyclerView;

    private InputMethodManager imm;

    private Recipe recipe;
    private Boolean newIngredient = false;
    private Ingredient ingredient;
    private EditIngredientAdapter adapter;
    private Boolean newRecipe;
    private LinearLayoutManager llm;

    //Ingredient Dialog Views
    private EditText dialogCountEditText;
    private EditText dialogIngredientNameEditText;
    private Spinner dialogUnitSpinner;
    private Button dialogAddIngredientButton;
    private Button dialogCancelIngredientButton;
    private Button dialogRemoveIngredientButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);
        ButterKnife.bind(this);
        //Set listeners
        addIngredientButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);

        quickLog = QuickLog.newInstance("EditRecipeActivity");

        //Check if this is a new recipe, if not then get the recipe attached to the intent
        if (getIntent() != null) {
            newRecipe = getIntent().getBooleanExtra(Utils.EXTRA_NEW_RECIPE, true);
            if (!newRecipe) {
                recipe = getIntent().getParcelableExtra(Utils.EXTRA_RECIPE);
            }
        }

        //Get input method manager
        imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);

        //Setup Recycler Views

        ingredientsRecyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        ingredientsRecyclerView.setLayoutManager(llm);
        adapter = new EditIngredientAdapter(EditRecipeActivity.this, this);
        ingredientsRecyclerView.setAdapter(adapter);
        if (!newRecipe) {
            populateRecipe();
        }


    }

    private void populateRecipe() {
        ingredientsRecyclerView.setVisibility(View.VISIBLE);
        adapter.setData(recipe.getIngredients());
        titleEditText.setText(recipe.getName());
    }


    @Override
    public void onClick(View view) {
        QuickLog.i("CLICK");
        switch (view.getId()) {
            case R.id.button_add_ingredient:
                createIngredientDialog(null, -1);
                break;
            case R.id.button_save:
                QuickLog.i("SAVE");
                break;
        }
    }

    //TODO Clean up this method
    private void createIngredientDialog(final Ingredient modifiedIngredient, final int position) {
        if (modifiedIngredient == null) {
            ingredient = new Ingredient();
            newIngredient = true;
        } else {
            ingredient = modifiedIngredient;
        }

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_ingredient);

        //Setup unit spinner
        dialogUnitSpinner = (Spinner) dialog.findViewById(R.id.dialog_spinner_units);
        ArrayAdapter<CharSequence> unitSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.dialog_spinner_units, R.layout.dialog_spinner_item);
        unitSpinnerAdapter.setDropDownViewResource(R.layout.dialog_spinner_layout);
        dialogUnitSpinner.setAdapter(unitSpinnerAdapter);
        if (!newIngredient && ingredient.getUnit() != null) {
            String compareValue = ingredient.getUnit();
            if (!compareValue.equals(null)) {
                int spinnerPosition = unitSpinnerAdapter.getPosition(compareValue);
                dialogUnitSpinner.setSelection(spinnerPosition);
                spinnerPosition = 0;
            }
        }

        //Setup cancel button
        dialogCancelIngredientButton = (Button) dialog.findViewById(R.id.dialog_button_cancel);
        dialogCancelIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogCountEditText = (EditText) dialog.findViewById(R.id.dialog_edit_text_quantity);
        if (!newIngredient && ingredient.getCount() != 0) {
            double quantity = ingredient.getCount();
            String stringQuantity = Utils.formatNumber(quantity);
            dialogCountEditText.setText(stringQuantity);
        }

        dialogIngredientNameEditText = (EditText) dialog.findViewById(R.id.dialog_edit_text_ingredient_name);
        if (!newIngredient) {
            dialogIngredientNameEditText.setText(ingredient.getName());
        }

        //Add ingredient button
        dialogAddIngredientButton = (Button) dialog.findViewById(R.id.dialog_button_add_ingredient);
        if (!newIngredient) dialogAddIngredientButton.setText(getString(R.string.button_dialog_update));

        dialogAddIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newIngredient = false;
                imm.hideSoftInputFromWindow(dialogCountEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(dialogIngredientNameEditText.getWindowToken(), 0);
                if (!dialogIngredientNameEditText.getText().toString().equals("")) {

                    if (!dialogCountEditText.getText().toString().equals("")) {
                        ingredient.setCount(Double.valueOf(dialogCountEditText.getText().toString().trim()));
                    }

                    if (dialogUnitSpinner.getSelectedItemPosition() == 0) {
                        ingredient.setUnit(null);
                    }
                    if (!dialogUnitSpinner.getSelectedItem().toString().equals("Units")) {
                        ingredient.setUnit(dialogUnitSpinner.getSelectedItem().toString());
                    }
                    if (!dialogIngredientNameEditText.getText().toString().equals("")) {
                        ingredient.setName(dialogIngredientNameEditText.getText().toString().toLowerCase().trim());
                    }

                    dialog.dismiss();

                    List<Ingredient> ingredients = adapter.getData();
                    if (position == -1) {
                        ingredients.add(ingredient);
                    } else {
                        ingredients.set(position,ingredient);
                    }
                    if (ingredients.size() > 0) {
                        ingredientsRecyclerView.setVisibility(View.VISIBLE);

                    }
                    adapter.setData(ingredients);
                } else {
                    Toast.makeText(EditRecipeActivity.this, getString(R.string.error_dialog_enter_name), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Remove Ingredient Button
        dialogRemoveIngredientButton = (Button) dialog.findViewById(R.id.dialog_button_remove);
        if (!newIngredient) dialogRemoveIngredientButton.setVisibility(View.VISIBLE);
        dialogRemoveIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Ingredient> ingredients = adapter.getData();
                ingredients.remove(position);
                adapter.setData(ingredients);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onMoveIngredientUpClicked(int position, Ingredient ingredient) {
        quickLog.i(position);
        int newPosition = position - 1;
        if (newPosition < 0) return;
        adapter.removeIngredient(position);
        adapter.addIngredient(newPosition, ingredient);
        adapter.notifyItemMoved(position, newPosition);
    }

    @Override
    public void onIngredientCardViewClicked(Ingredient ingredient, int position) {
        createIngredientDialog(ingredient,position);
    }
}

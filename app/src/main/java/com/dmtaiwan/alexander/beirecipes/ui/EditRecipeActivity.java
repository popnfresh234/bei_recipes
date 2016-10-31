package com.dmtaiwan.alexander.beirecipes.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.dmtaiwan.alexander.beirecipes.R;
import com.dmtaiwan.alexander.beirecipes.data.Ingredient;
import com.dmtaiwan.alexander.beirecipes.data.Recipe;
import com.dmtaiwan.alexander.beirecipes.ui.adapters.EditIngredientAdapter;
import com.dmtaiwan.alexander.beirecipes.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Alexander on 10/29/2016.
 */

public class EditRecipeActivity extends AppCompatActivity implements EditIngredientAdapter.AdapterListener, View.OnClickListener{
    
    @BindView(R.id.dialog_button_add_ingredient)
    Button mAddIngredientButton;

    @BindView(R.id.button_save)
    Button mSaveButton;

    @BindView(R.id.edit_text_title)
    EditText mTitleEditText;

    @BindView(R.id.recycler_view_ingredients)
    RecyclerView mIngredientsRecyclerView;

    private InputMethodManager mImm;

    private Recipe recipe;
    private Boolean newIngredient = false;
    private Ingredient ingredient;
    private EditIngredientAdapter adapter;
    private ArrayList<Recipe> recipeList;
    private int mRecipePosition;
    private Boolean mNewRecipe;

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

        if (getIntent() != null) {
            recipeList = getIntent().getParcelableArrayListExtra(Utils.EXTRA_RECIPES);
            mRecipePosition = getIntent().getIntExtra(Utils.EXTRA_RECIPE_POSITION, -1);
            mNewRecipe = getIntent().getBooleanExtra(Utils.EXTRA_NEW_RECIPE, true);
        }
    }


    @Override
    public void onClick(View view) {
        
    }

    @Override
    public void onMoveIngredientUpClicked(int position) {

    }

    @Override
    public void onIngredientCardViewClicked(int position) {

    }
}

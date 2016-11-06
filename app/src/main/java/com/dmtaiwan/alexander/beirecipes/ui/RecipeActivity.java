package com.dmtaiwan.alexander.beirecipes.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.dmtaiwan.alexander.beirecipes.R;
import com.dmtaiwan.alexander.beirecipes.data.Ingredient;
import com.dmtaiwan.alexander.beirecipes.data.Recipe;
import com.dmtaiwan.alexander.beirecipes.ui.adapters.RecipeAdapter;
import com.dmtaiwan.alexander.beirecipes.util.QuickLog;
import com.dmtaiwan.alexander.beirecipes.util.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeActivity extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener, RecipeAdapter.RecyclerTextChangedListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;
    private LinearLayoutManager llm;

    @BindView(R.id.main_linearlayout_title)
    LinearLayout titleContainer;

    @BindView(R.id.main_textview_title)
    TextView title;

    @BindView(R.id.sub_text_title)
    TextView subTitle;

    @BindView(R.id.main_appbar)
    AppBarLayout appBarLayout;

    @BindView(R.id.main_toolbar)
    Toolbar toolbar;

    @BindView(R.id.ingredient_recycler)
    RecyclerView recycler;

    @BindView(R.id.empty_view)
    TextView emptyView;

    private RecipeAdapter adapter;
    private ArrayList<Recipe> recipes;
    private int recipePosition;
    Spinner dialogFractionSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Stop keyboard from messing up coordinator layout
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        setContentView(R.layout.activity_recipe);
        //Butterknife and QuickLog
        ButterKnife.bind(this);
        final QuickLog quickLog = QuickLog.newInstance("RecipeActivity");

        //Set up the RecyclerView and Adapter
        adapter = new RecipeAdapter(RecipeActivity.this, this, emptyView, recycler);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(llm);
        recycler.setAdapter(adapter);
        recycler.setHasFixedSize(true);

        //Get recipe this Activity was created with
        if (getIntent() != null) {
            recipes = getIntent().getParcelableArrayListExtra(Utils.EXTRA_RECIPES);
            recipePosition = getIntent().getIntExtra(Utils.EXTRA_RECIPE_POSITION, 0);
            Recipe recipe = recipes.get(recipePosition);
            //Set the name of the recipe
            title.setText(recipe.getName());
            subTitle.setText(recipe.getName());
            List<Ingredient> ingredients = recipe.getIngredients();
            adapter.setData(ingredients);
        }

        appBarLayout.addOnOffsetChangedListener(this);
        startAlphaAnimation(title, 0, View.INVISIBLE);

        toolbar.inflateMenu(R.menu.menu_recipe);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_edit:
                        Intent intent = new Intent(RecipeActivity.this, EditRecipeActivity.class);
                        intent.putExtra(Utils.EXTRA_RECIPES, recipes);
                        intent.putExtra(Utils.EXTRA_RECIPE_POSITION, recipePosition);
                        intent.putExtra(Utils.EXTRA_NEW_RECIPE, false);
                        startActivity(intent);
                        finish();
                        quickLog.i("EDIT");
                        break;
                    case R.id.action_delete:
                        quickLog.i("DELETE");
                        displayDeleteAlert(RecipeActivity.this);
                        break;
                }
                return true;
            }
        });
    }

    private void displayDeleteAlert(final Context context) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Write deleted recipe to file
                        Recipe recipe = recipes.get(recipePosition);
                        ArrayList<Recipe> deletedRecipes = new ArrayList<>();
                        recipes.remove(recipePosition);
                        String jsonList = new Gson().toJson(recipes);
                        Utils.writeDataUpdateCookbook(recipes, jsonList, RecipeActivity.this);
                        finish();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.dialog_confirm))
                .setPositiveButton(getString(R.string.dialog_positive), dialogClickListener)
                .setNegativeButton(getString(R.string.dialog_negative), dialogClickListener).show();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(title, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(title, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(titleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(titleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    @Override
    public void onRecyclerTextChanged(int position) {
        //Get the list of ingredients
        List<Ingredient> ingredients = adapter.getData();
        //Get the ingredient being modified
        Ingredient testIngredient = ingredients.get(position);
        createCountDialog(testIngredient, ingredients, position);
//        //Calculate the ratio between the entered value and the orignal value
//        double ratio = Utils.getRatio(enteredValue, testIngredient);
//        //Apply ratio to rest of values
//        for (int i = 0; i < ingredients.size(); i++) {
//            Ingredient ingredient = ingredients.get(i);
//            if (i == position) {
//                //If this is the positon the user set a vlaue for, no need to caluclate anything
//                ingredient.setProportionalCount(enteredValue);
//                ingredients.set(i, ingredient);
//            } else {
//                //Otherwise calculate ratio and set value
//                ingredient.setProportionalCount(ingredient.getCount() * ratio);
//                ingredients.set(i, ingredient);
//            }
//        }
        //Finally, upd¼ate the adapter with new data

    }

    private void createCountDialog(final Ingredient testIngredient, final List<Ingredient> ingredients, final int position) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_proportion);

        //Setup edittext
        final EditText dialogProportionEditText = (EditText) dialog.findViewById(R.id.dialog_proportion_edit_text_quantity);

        //Setup fraction spinner
        dialogFractionSpinner = (Spinner) dialog.findViewById(R.id.dialog_proportion_spinner_fractions);
        ArrayAdapter<CharSequence> fractionSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.dialog_spinner_fractions, R.layout.dialog_spinner_item);
        fractionSpinnerAdapter.setDropDownViewResource(R.layout.dialog_spinner_layout);
        dialogFractionSpinner.setAdapter(fractionSpinnerAdapter);

        //Setup cancel button
        Button dialogCancelIngredientButton = (Button) dialog.findViewById(R.id.dialog_button_cancel);
        dialogCancelIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //Setup OK button
        Button dialogOkButton = (Button) dialog.findViewById(R.id.dialog_button_ok);
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double inputAmmount = 0;
                if (!dialogProportionEditText.getText().toString().equals("")) {
                    inputAmmount = Double.parseDouble(dialogProportionEditText.getText().toString());
                }

                if (!dialogFractionSpinner.getSelectedItem().toString().equals(getResources().getStringArray(R.array.dialog_spinner_fractions)[0])) {
                    double fraction = Utils.fractionToDouble(dialogFractionSpinner.getSelectedItem().toString());
                    inputAmmount = inputAmmount + fraction;
                }

                //Calculate the ratio between the entered value and the orignal value
                double ratio = inputAmmount / testIngredient.getCount();
                for(int i = 0; i< ingredients.size(); i++) {
                    Ingredient ingredient = ingredients.get(i);
                    if (i != position) {
                        ingredient.setProportionalCount(ingredient.getCount() * ratio);
                        ingredients.set(i, ingredient);
                    }else{
                        ingredient.setProportionalCount(inputAmmount);
                        ingredients.set(i, ingredient);
                    }
                }


                dialog.dismiss();
                adapter.setData(ingredients);
            }


        });

        dialog.show();
    }
}

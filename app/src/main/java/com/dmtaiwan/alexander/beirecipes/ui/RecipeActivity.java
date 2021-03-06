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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.dmtaiwan.alexander.beirecipes.R;
import com.dmtaiwan.alexander.beirecipes.data.Cookbook;
import com.dmtaiwan.alexander.beirecipes.data.Direction;
import com.dmtaiwan.alexander.beirecipes.data.Ingredient;
import com.dmtaiwan.alexander.beirecipes.data.Recipe;
import com.dmtaiwan.alexander.beirecipes.ui.adapters.DirectionAdapter;
import com.dmtaiwan.alexander.beirecipes.ui.adapters.IngredientAdapter;
import com.dmtaiwan.alexander.beirecipes.util.QuickLog;
import com.dmtaiwan.alexander.beirecipes.util.Utils;
import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecipeActivity extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener, IngredientAdapter.RecyclerTextChangedListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;
    private LinearLayoutManager ingredientLlm;
    private LinearLayoutManager directionLlm;

    @BindView(R.id.main_linearlayout_title)
    LinearLayout titleContainer;

    @BindView(R.id.main_textview_title)
    TextView title;

    @BindView(R.id.sub_text_title)
    TextView subTitle;

    @BindView(R.id.main_imageview_placeholder)
    ImageView backgroundImage;

    @BindView(R.id.imageview_circle)
    CircleImageView circleImageView;

    @BindView(R.id.main_appbar)
    AppBarLayout appBarLayout;

    @BindView(R.id.main_toolbar)
    Toolbar toolbar;

    @BindView(R.id.ingredient_recycler)
    RecyclerView ingredientRecycler;

    @BindView(R.id.direction_recycler)
    RecyclerView directionRecycler;

    @BindView(R.id.empty_view)
    TextView emptyView;

    private IngredientAdapter ingredientAdapter;
    private DirectionAdapter directionAdapter;
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

        //Set up the ingredient RecyclerView and Adapter
        ingredientAdapter = new IngredientAdapter(RecipeActivity.this, this, emptyView, ingredientRecycler);
        ingredientLlm = new LinearLayoutManager(this);
        ingredientLlm.setOrientation(LinearLayoutManager.VERTICAL);
        ingredientRecycler.setLayoutManager(ingredientLlm);
        ingredientRecycler.setAdapter(ingredientAdapter);
        ingredientRecycler.setHasFixedSize(true);

        //Set up the direction RecyclerVie and Adapter
        directionAdapter = new DirectionAdapter(this, null, directionRecycler);
        directionLlm = new LinearLayoutManager(this);
        directionLlm.setOrientation(LinearLayoutManager.VERTICAL);
        directionRecycler.setLayoutManager(directionLlm);
        directionRecycler.setAdapter(directionAdapter);
        directionRecycler.setHasFixedSize(true);
        //set dummy data
        List<Direction> directions = new ArrayList<>();
        for(int i = 0; i<5; i++) {
            Direction direction = Direction.newDirection("THIS IS DIRECTION " + String.valueOf(i));
            directions.add(direction);
        }
        directionAdapter.setData(directions);
        //END DUMMY DATA

        //Get the cookbook
        Cookbook cookbook = Cookbook.get(this);
        recipes = cookbook.getRecipes();

        //Get recipe this Activity was created with
        if (getIntent() != null) {
            recipePosition = getIntent().getIntExtra(Utils.EXTRA_RECIPE_POSITION, 0);
            Recipe recipe = recipes.get(recipePosition);
            //Set the name of the recipe
            title.setText(recipe.getName());
            if (recipe.getImageUri() != null) {
                Picasso.with(this).load(recipe.getImageUri()).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.food).into(backgroundImage);
                Picasso.with(this).load(recipe.getImageUri()).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.food
                ).into(circleImageView);
            }
            subTitle.setText(recipe.getName());
            List<Ingredient> ingredients = recipe.getIngredients();
            ingredientAdapter.setData(ingredients);
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
        List<Ingredient> ingredients = ingredientAdapter.getData();
        //Get the ingredient being modified
        Ingredient testIngredient = ingredients.get(position);
        createCountDialog(testIngredient, ingredients, position);
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
                ingredientAdapter.setData(ingredients);
            }


        });

        dialog.show();
    }
}

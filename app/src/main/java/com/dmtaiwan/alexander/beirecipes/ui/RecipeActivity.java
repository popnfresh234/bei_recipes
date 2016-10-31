package com.dmtaiwan.alexander.beirecipes.ui;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dmtaiwan.alexander.beirecipes.R;
import com.dmtaiwan.alexander.beirecipes.data.Ingredient;
import com.dmtaiwan.alexander.beirecipes.ui.adapters.RecipeAdapter;
import com.dmtaiwan.alexander.beirecipes.util.QuickLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    @BindView(R.id.main_appbar)
    AppBarLayout appBarLayout;

    @BindView(R.id.main_toolbar)
    Toolbar toolbar;

    @BindView(R.id.ingredient_recycler)
    RecyclerView recycler;

    private RecipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        //Butterknife and QuickLog
        ButterKnife.bind(this);
        final QuickLog quickLog = QuickLog.newInstance("RecipeActivity");


        appBarLayout.addOnOffsetChangedListener(this);
        startAlphaAnimation(title, 0, View.INVISIBLE);

        toolbar.inflateMenu(R.menu.menu_recipe);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_edit:
                        quickLog.i("EDIT");
                        break;
                    case R.id.action_delete:
                        quickLog.i("DELETE");
                        break;
                }
                return true;
            }
        });

        //Set up the RecyclerView and Adapter
        adapter = new RecipeAdapter(RecipeActivity.this, this);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(llm);
        recycler.setAdapter(adapter);
        recycler.setHasFixedSize(true);

        //Dummy data

        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Ingredient ingredient = new Ingredient();
            ingredient.setName("Ingredient #" + String.valueOf(i));
            double start = 0;
            double end = 100;
            double random = new Random().nextDouble();
            double result = start + (random * (end - start));
            ingredient.setCount(result);
            ingredient.setUnit("mg");
            ingredient.setProportionalCount(0);
            ingredients.add(ingredient);
        }
        adapter.setData(ingredients);
        //End dummy data
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
    public void onRecyclerTextChanged(double enteredValue, int position) {
        //Get the list of ingredients
        List<Ingredient> ingredients = adapter.getData();
        //Get the ingredient being modified
        Ingredient testIngredient = ingredients.get(position);
        //Calculate the ratio between the entered value and the orignal value
        double ratio = enteredValue / testIngredient.getCount();
        //Apply ratio to rest of values
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            if (i == position) {
                //If this is the positon the user set a vlaue for, no need to caluclate anything
                ingredient.setProportionalCount(enteredValue);
                ingredients.set(i, ingredient);
            } else {
                //Otherwise calculate ratio and set value
                ingredient.setProportionalCount(ingredient.getCount() * ratio);
                ingredients.set(i, ingredient);
            }
        }
        //Finally, update the adapter with new data
        adapter.setData(ingredients);
    }
}

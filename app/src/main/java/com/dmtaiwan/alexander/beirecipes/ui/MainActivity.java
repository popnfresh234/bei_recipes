package com.dmtaiwan.alexander.beirecipes.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import com.dmtaiwan.alexander.beirecipes.R;
import com.dmtaiwan.alexander.beirecipes.data.Recipe;
import com.dmtaiwan.alexander.beirecipes.ui.adapters.MainAdapter;
import com.dmtaiwan.alexander.beirecipes.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainAdapter.RecyclerClickListener{

    @BindView(R.id.grid)
    RecyclerView grid;
    private GridLayoutManager layoutManager;
    @BindInt(R.integer.num_columns)
    int columns;

    @BindView(R.id.root_layout)
    FrameLayout frameLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    private MainAdapter recipeAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            ViewUtils.animateToolbar(toolbar,this);
        }

        //Deal with insets
        frameLayout.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View view, WindowInsets insets) {

                // inset the toolbar down by the status bar height
                ViewGroup.MarginLayoutParams lpToolbar = (ViewGroup.MarginLayoutParams) toolbar
                        .getLayoutParams();
                lpToolbar.topMargin += insets.getSystemWindowInsetTop();
                lpToolbar.leftMargin += insets.getSystemWindowInsetLeft();
                lpToolbar.rightMargin += insets.getSystemWindowInsetRight();
                toolbar.setLayoutParams(lpToolbar);

                // inset the grid top by statusbar+toolbar & the bottom by the navbar (don't clip)
                grid.setPadding(
                        grid.getPaddingLeft() + insets.getSystemWindowInsetLeft(), // landscape
                        insets.getSystemWindowInsetTop()
                                + ViewUtils.getActionBarSize(MainActivity.this),
                        grid.getPaddingRight() + insets.getSystemWindowInsetRight(), // landscape
                        grid.getPaddingBottom() + insets.getSystemWindowInsetBottom());

                // we place a background behind the status bar to combine with it's semi-transparent
                // color to get the desired appearance.  Set it's height to the status bar height
                View statusBarBackground = findViewById(R.id.status_bar_background);
                FrameLayout.LayoutParams lpStatus = (FrameLayout.LayoutParams)
                        statusBarBackground.getLayoutParams();
                lpStatus.height = insets.getSystemWindowInsetTop();
                statusBarBackground.setLayoutParams(lpStatus);

                return insets.consumeSystemWindowInsets();
            }
        });


        List<Recipe> recipeList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Recipe recipe = Recipe.newRecipe("This is recipe #" + String.valueOf(i));
            recipeList.add(recipe);
            if (i % 3 == 0) {
                recipe.setDrawableId(R.drawable.food);
            }
        }

        recipeAdapter = new MainAdapter(this, this);
        recipeAdapter.setData(recipeList);
        grid.setAdapter(recipeAdapter);
        layoutManager = new GridLayoutManager(this, columns);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return recipeAdapter.getItemColumnSpan(position);
            }
        });
        grid.setLayoutManager(layoutManager);
        grid.setHasFixedSize(true);
        grid.addOnScrollListener(toolbarElevation);
    }

    private RecyclerView.OnScrollListener toolbarElevation = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            //Lower toolbar on scroll
            if (newState == RecyclerView.SCROLL_STATE_IDLE
                    && layoutManager.findFirstVisibleItemPosition() == 0
                    && layoutManager.findViewByPosition(0).getTop() == grid.getPaddingTop()
                    && toolbar.getTranslationZ() != 0) {
                // at top, reset elevation
                toolbar.setTranslationZ(0f);
            } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING
                    && toolbar.getTranslationZ() != -1f) {
                // grid scrolled, lower toolbar to allow content to pass in front
                toolbar.setTranslationZ(-1f);
            }
        }
    };


    @Override
    public void onClick() {
        Intent intent = new Intent(this, RecipeActivity.class);
        startActivity(intent);
    }
}

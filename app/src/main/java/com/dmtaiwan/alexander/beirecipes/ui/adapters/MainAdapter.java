package com.dmtaiwan.alexander.beirecipes.ui.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dmtaiwan.alexander.beirecipes.R;
import com.dmtaiwan.alexander.beirecipes.data.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Alexander on 10/28/2016.
 */

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_SPACER = 0;
    private static final int VIEW_TYPE_REGULAR = 1;

    private List<Recipe> recipes;
    private final LayoutInflater layoutInflater;
    private Activity hostActivity;
    private RecyclerClickListener listener;

    public MainAdapter(Activity hostActivity, RecyclerClickListener listener) {
        this.layoutInflater = hostActivity.getLayoutInflater();
        this.hostActivity = hostActivity;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return createMainHolder(parent);
    }

    private RecyclerView.ViewHolder createMainHolder(ViewGroup parent) {
        final MainHolder holder = new MainHolder(layoutInflater.inflate(R.layout.list_item_recipe, parent, false));
        return holder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindRecipeItem((MainHolder) holder, position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_SPACER;
        } else return VIEW_TYPE_REGULAR;
    }

    private void bindRecipeItem(MainHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.recipeName.setText(recipe.getName());
        if (recipe.getDrawableId() != 0) {
            holder.recipeName.setVisibility(View.GONE);
            holder.recipeThumb.setVisibility(View.VISIBLE);
            Picasso.with(hostActivity).load(recipe.getDrawableId()).fit().into(holder.recipeThumb);
        } else {
            holder.recipeName.setVisibility(View.VISIBLE);
            holder.recipeThumb.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return getDataCount();
    }


    public class MainHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.holder_recipe_name)
        TextView recipeName;
        @BindView(R.id.holder_recipe_thumb)
        ImageView recipeThumb;

        MainHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(recipes.get(getAdapterPosition()));
        }
    }


    public int getItemColumnSpan(int position) {
        return 1;
    }

    private int getDataCount() {
        if (recipes == null) {
            return 0;
        } else return recipes.size();
    }

    public void setData(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    public interface RecyclerClickListener{
        void onClick(Recipe recipe);
    }
}

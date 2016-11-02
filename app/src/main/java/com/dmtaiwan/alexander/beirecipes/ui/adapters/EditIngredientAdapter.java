package com.dmtaiwan.alexander.beirecipes.ui.adapters;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dmtaiwan.alexander.beirecipes.R;
import com.dmtaiwan.alexander.beirecipes.data.Ingredient;
import com.dmtaiwan.alexander.beirecipes.util.QuickLog;
import com.dmtaiwan.alexander.beirecipes.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Alexander on 10/29/2016.
 */

public class EditIngredientAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Ingredient> ingredients;
    private Activity hostActivity;
    private AdapterListener listener;
    private LayoutInflater layoutInflater;

    public EditIngredientAdapter(Activity hostActivity, AdapterListener listener) {
        this.hostActivity = hostActivity;
        this.listener = listener;
        this.layoutInflater = hostActivity.getLayoutInflater();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final IngredientHolder holder = new IngredientHolder(layoutInflater.inflate(R.layout.list_item_edit_ingredient, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindIngredientHolder((IngredientHolder) holder, position);
    }


    @Override
    public int getItemCount() {
        if (ingredients != null) {
            return ingredients.size();
        } else return 0;
    }

    private void bindIngredientHolder(IngredientHolder holder, final int position) {
        final Ingredient ingredient = ingredients.get(position);

        //Move an ingredient up the list
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMoveIngredientUpClicked(position, ingredient);
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIngredientCardViewClicked(ingredients.get(position),position);
            }
        });

        //Clear out the view
        holder.count.setText("");
        holder.unit.setText("");
        holder.unit.setText("");

        //Load values if not new ingredient
        if (ingredient.getCount() != 0) {
            double count = ingredient.getCount();
            holder.count.setText(Utils.formatNumber(count));
        }

        if (ingredient.getUnit() != null) {
            holder.unit.setText(ingredient.getUnit() + " ");
        }

        holder.name.setText(ingredient.getName());

    }


    public class IngredientHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_view_ingredient_list_edit_quantity)
        TextView count;

        @BindView(R.id.text_view_ingredient_list_edit_unit)
        TextView unit;

        @BindView(R.id.text_view_ingredient_list_edit_name)
        TextView name;

        @BindView(R.id.button_move_up)
        Button button;

        @BindView(R.id.card_view)
        CardView cardView;

        IngredientHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setData(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        notifyDataSetChanged();
    }

    public List<Ingredient> getData() {
        if (ingredients != null) {
            return ingredients;
        } else return new ArrayList<>();

    }

    public void removeIngredient(int position) {
        ingredients.remove(position);
        notifyItemChanged(position);
    }

    public void addIngredient(int position, Ingredient ingredient) {
        QuickLog.i("Adding");
        QuickLog.i(position);
        ingredients.add(position, ingredient);
        notifyItemChanged(position);
    }


    public interface AdapterListener {

        public void onMoveIngredientUpClicked(int position, Ingredient ingredient);

        public void onIngredientCardViewClicked(Ingredient ingredient, int position);

    }

}

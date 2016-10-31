package com.dmtaiwan.alexander.beirecipes.ui.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dmtaiwan.alexander.beirecipes.R;
import com.dmtaiwan.alexander.beirecipes.data.Ingredient;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Alexander on 10/29/2016.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_REGULAR = 1;

    private List<Ingredient> ingredients;
    private Activity hostActivity;
    private LayoutInflater layoutInflater;
    private DecimalFormat mDecimalFormat = new DecimalFormat("0");
    private RecyclerTextChangedListener listener;
    private boolean onBind;

    public RecipeAdapter(Activity hostActivity, RecyclerTextChangedListener listener) {
        this.layoutInflater = hostActivity.getLayoutInflater();
        this.hostActivity = hostActivity;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            return createHeaderHolder(parent);
        }
        return createIngredientHolder(parent);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_REGULAR) {
            bindIngredientItem((RecipeHolder) holder, position);
        }

    }


    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        } else return VIEW_TYPE_REGULAR;
    }

    private RecyclerView.ViewHolder createIngredientHolder(ViewGroup parent) {
        final RecipeHolder holder = new RecipeHolder(layoutInflater.inflate(R.layout.list_item_ingredient, parent, false));
        return holder;
    }

    private RecyclerView.ViewHolder createHeaderHolder(ViewGroup parent) {
        final HeaderHolder holder = new HeaderHolder(layoutInflater.inflate(R.layout.list_item_ingredient_header, parent, false));
        return holder;
    }

    private void bindIngredientItem(RecipeHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.title.setText(ingredient.getName());
        String formattedCount = mDecimalFormat.format(ingredient.getCount());
        holder.count.setText(formattedCount);
        holder.units.setText(ingredient.getUnit());
        String formattedProportialCount = mDecimalFormat.format(ingredient.getProportionalCount());
        onBind = true;
        holder.proportionalCount.setText(formattedProportialCount);
        onBind =false;
    }

    public class RecipeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.holder_ingredient_title)
        TextView title;

        @BindView(R.id.holder_ingredient_count)
        TextView count;

        @BindView(R.id.holder_units)
        TextView units;

        @BindView(R.id.holder_proportional_count)
        EditText proportionalCount;

        @BindView(R.id.holder_button_ok)
        Button okButton;

        RecipeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            okButton.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.holder_button_ok) {
                String input = proportionalCount.getText().toString();
                if (input.equals("")) {
                    input = "0";
                }
                listener.onRecyclerTextChanged(Double.parseDouble(input), getAdapterPosition());
            }
        }
    }

    public class HeaderHolder extends RecyclerView.ViewHolder{
        HeaderHolder(View itemView) {
            super(itemView);
        }


    }

    public void setData(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        notifyDataSetChanged();
    }

    public List<Ingredient> getData() {
        return ingredients;
    }

    public interface RecyclerTextChangedListener {
        void onRecyclerTextChanged(double enteredValue, int position);
    }

}

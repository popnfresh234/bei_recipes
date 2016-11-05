package com.dmtaiwan.alexander.beirecipes.ui.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.dmtaiwan.alexander.beirecipes.R;
import com.dmtaiwan.alexander.beirecipes.data.Ingredient;
import com.dmtaiwan.alexander.beirecipes.util.QuickLog;
import com.dmtaiwan.alexander.beirecipes.util.Utils;

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

    private RecyclerTextChangedListener listener;
    private boolean onBind;
    private View emptyView;
    private View recycler;

    public RecipeAdapter(Activity hostActivity, RecyclerTextChangedListener listener, View emptyView, View recycler) {
        this.layoutInflater = hostActivity.getLayoutInflater();
        this.hostActivity = hostActivity;
        this.listener = listener;
        this.emptyView = emptyView;
        this.recycler = recycler;
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
            //Offset by one to account for header row
            bindIngredientItem((RecipeHolder) holder, position-1);
        }

    }


    @Override
    public int getItemCount() {
        if (ingredients.size() > 0) {
            //Offset by one to account for header row
            return ingredients.size()+1;
        }
        //This handles the case of no ingredients, still want to display the header row
        else return 1;

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
        QuickLog.i(position);
        Ingredient ingredient = ingredients.get(position);
        holder.title.setText(ingredient.getName());


        //Get the count
        String finalCount = "";
        double count = ingredient.getCount();
        double wholeNumber = Utils.getWholeNumber(ingredient);
        double fraction = Utils.getFractiun(ingredient);
        finalCount = Utils.formatNumber(wholeNumber) + Utils.doubleToFraction(fraction);



        holder.count.setText(finalCount);

        holder.units.setText(ingredient.getUnit());
        String formattedProportialCount = Utils.formatNumber(ingredient.getProportionalCount());
        onBind = true;
        holder.proportionalCount.setText(formattedProportialCount);
        onBind = false;
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


        RecipeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            proportionalCount.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.holder_proportional_count) {

                //getAdapterPosition needs to be offset again to account for header row
                listener.onRecyclerTextChanged(getAdapterPosition()-1);
            }
        }
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {
        HeaderHolder(View itemView) {
            super(itemView);
        }


    }

    public void setData(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        notifyDataSetChanged();
        emptyView.setVisibility(ingredients.size() == 0 ? View.VISIBLE : View.GONE);

        recycler.setVisibility(ingredients.size() == 0 ? View.GONE : View.VISIBLE);
    }

    public List<Ingredient> getData() {
        return ingredients;
    }

    public interface RecyclerTextChangedListener {
        void onRecyclerTextChanged(int position);
    }

}

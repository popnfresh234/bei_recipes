package com.dmtaiwan.alexander.beirecipes.ui.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dmtaiwan.alexander.beirecipes.R;
import com.dmtaiwan.alexander.beirecipes.data.Direction;
import com.dmtaiwan.alexander.beirecipes.util.QuickLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Alexander on 10/29/2016.
 */

public class DirectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<Direction> directions;
    private LayoutInflater layoutInflater;

    private View emptyView;
    private View recycler;

    public DirectionAdapter(Activity hostActivity, View emptyView, View recycler) {
        this.layoutInflater = hostActivity.getLayoutInflater();
        this.emptyView = emptyView;
        this.recycler = recycler;
        directions = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return createDirectionHolder(parent);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        bindDirectionItem((RecipeHolder) holder, position);


    }


    @Override
    public int getItemCount() {

        return directions.size();

    }


    private RecyclerView.ViewHolder createDirectionHolder(ViewGroup parent) {
        final RecipeHolder holder = new RecipeHolder(layoutInflater.inflate(R.layout.list_item_direction, parent, false));
        return holder;
    }


    private void bindDirectionItem(RecipeHolder holder, int position) {
        QuickLog.i(position);
        Direction direction = directions.get(position);
        holder.description.setText(direction.getDescription());


    }

    public class RecipeHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.holder_direction_text_view)
        TextView description;


        RecipeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setData(List<Direction> directions) {
        this.directions = directions;
        notifyDataSetChanged();

        //TODO Implement empty view
//        emptyView.setVisibility(directions == null || directions.size() == 0 ? View.VISIBLE : View.GONE);
        recycler.setVisibility(directions == null || directions.size() == 0 ? View.GONE : View.VISIBLE);
    }

    public List<Direction> getData() {
        return directions;
    }

}

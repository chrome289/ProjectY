package com.project.notifuzz;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;

class GridViewAdapter extends RecyclerView.Adapter<GridViewHolder> implements ItemTouchHelperAdapter {
    @Override
    public void onItemDismiss(int position) {
        AppSelectionActivity.tileText.remove(position);
        AppSelectionActivity.tileColor.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(AppSelectionActivity.tileText, i, i + 1);
                Collections.swap(AppSelectionActivity.tileColor,i,i+1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(AppSelectionActivity.tileText, i, i - 1);
                Collections.swap(AppSelectionActivity.tileColor,i,i-1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //create viewholder for an entry
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.grid_view_tile, parent, false);
        return new GridViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, int position) {

        //set data for the viewholder of any entry

        holder.textView.setText(AppSelectionActivity.tileText.get(position));
        holder.relativeLayout.setBackgroundColor(Integer.parseInt(AppSelectionActivity.tileColor.get(position)));
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        if (AppSelectionActivity.tileText != null)
            return AppSelectionActivity.tileText.size();
        else
            return 0;
    }
}

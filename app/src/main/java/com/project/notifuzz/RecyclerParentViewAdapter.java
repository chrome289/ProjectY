package com.project.notifuzz;

import android.app.PendingIntent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class RecyclerParentViewAdapter extends RecyclerView.Adapter<RecyclerParentViewHolder> {


    ArrayList<ArrayList<NotificationView>> notificationView = new ArrayList<>();
    ArrayList<String> categories = new ArrayList<>();
    ArrayList<RecyclerViewAdapter> recyclerViewAdapters = new ArrayList<>();

    public RecyclerParentViewAdapter(ArrayList<String> categories, ArrayList<RecyclerViewAdapter> recyclerViewAdapters, ArrayList<ArrayList<NotificationView>> notificationView) {
        this.categories = categories;
        this.recyclerViewAdapters = recyclerViewAdapters;
        this.notificationView = notificationView;
    }

    @Override
    public RecyclerParentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //create viewholder for an entry
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.recyclerview_parent, parent, false);
        return new RecyclerParentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerParentViewHolder holder, final int position) {
        holder.textView.setText(categories.get(position));
        //layoutmanager for recyclerview

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ScrollingActivity.context);
        holder.recyclerView.setLayoutManager(mLayoutManager);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //params.height = 300 * NotificationService.notificationView.size(); //height recycleviewer
        holder.recyclerView.setLayoutParams(params);

        //onClick listener
        holder.recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(ScrollingActivity.context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int pos) {
                        try {
                            if (notificationView.get(position).get(pos).pendingIntents != null) {
                                //pendingIntent used
                                notificationView.get(position).get(pos).pendingIntents.send();
                            }
                            //remove entry from recyclerview
                            notificationView.get(position).remove(pos);
                            recyclerViewAdapters.get(position).notifyDataSetChanged();
                            ScrollingActivity.update();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                })
        );
        //ItemTouchHelper callback for swipe gesture
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            //draw colored rectangle during swipe gesture
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;

                    Paint p = new Paint();
                    p.setColor(Color.rgb(224, 160, 150));
                    if (dX > 0)
                        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                                (float) itemView.getBottom(), p);
                    else
                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                (float) itemView.getRight(), (float) itemView.getBottom(), p);

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }

            //clear entry from recyclerview on swipeCompleted
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                notificationView.get(position).remove(viewHolder.getAdapterPosition());
                recyclerViewAdapters.get(position).notifyDataSetChanged();
                ScrollingActivity.update();
            }
        };

        //assign ItemTouchCallback to recyclerview
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(holder.recyclerView);

        holder.recyclerView.setAdapter(recyclerViewAdapters.get(position));
        holder.imageButton.setRotation(180);
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.recyclerView.getVisibility()==View.GONE)
                    holder.recyclerView.setVisibility(View.VISIBLE);
                else
                    holder.recyclerView.setVisibility(View.GONE);
                holder.imageButton.setRotation(holder.imageButton.getRotation()+180);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (this.categories != null)
            return this.categories.size();
        else
            return 0;

    }
}

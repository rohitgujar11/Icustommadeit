package com.nanostuffs.myapplication.myapplication.Adapters;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nanostuffs.myapplication.R;
import com.nanostuffs.myapplication.myapplication.Models.Drawer_ItemBean;

import java.util.ArrayList;

/**
 * Created by acer on 3/21/2016.
 */


/**
 * Created by Nanostuffs on 22-01-2016.
 */
public class Drawer_ItemAdapter extends RecyclerView.Adapter<Drawer_ItemAdapter.ViewHolder> {

    private ArrayList<Drawer_ItemBean> itemsData;
    private Activity mContext;
    OnItemClickListener mItemClickListener;
    private int selected_position;
    public Drawer_ItemAdapter(Activity mContext, ArrayList<Drawer_ItemBean> itemsData, int selected_position) {
        this.itemsData = itemsData;
        this.mContext = mContext;
        this.selected_position = selected_position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.each_drawer_item, parent, false);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txt_name.setText(itemsData.get(position).getName());
        if(selected_position == position){
            holder.main_layout.setBackgroundColor(Color.parseColor("#d3d3d3"));
        }
    }


    @Override
    public int getItemCount() {
        return itemsData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_name;
        LinearLayout main_layout;
        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txt_name = (TextView) itemLayoutView.findViewById(R.id.txt_name);
            main_layout= (LinearLayout) itemLayoutView.findViewById(R.id.main_layout);
            itemLayoutView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

}

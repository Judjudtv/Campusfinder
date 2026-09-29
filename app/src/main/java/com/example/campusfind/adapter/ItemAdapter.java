package com.example.campusfind.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusfind.R;
import com.example.campusfind.models.Item;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    // Interface para malaman ng MainActivity kapag na-tap ang isang item
    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    private List<Item> itemList;
    private OnItemClickListener listener;

    public ItemAdapter(List<Item> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    // Tinatawag isang beses lang per "type" ng row - dito natin "ini-inflate"
    // (ginagawang totoong View object) yung item_row.xml template
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);
        return new ItemViewHolder(view);
    }

    // Tinatawag PARA SA BAWAT item sa listahan - dito natin "pinupuno" ng
    // actual data (mula sa database) yung mga TextView sa row
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);

        holder.tvItemName.setText(item.getItemName());
        holder.tvLocation.setText(item.getLocation());
        holder.tvDate.setText(item.getDate());
        holder.tvStatus.setText(item.getStatus());

        // Iba't ibang kulay ng badge depende kung Lost o Found
        if (item.getStatus().equalsIgnoreCase("Lost")) {
            holder.tvStatus.setBackgroundColor(Color.parseColor("#E53935")); // red
        } else {
            holder.tvStatus.setBackgroundColor(Color.parseColor("#43A047")); // green
        }

        // Kapag tinap yung buong row, i-report papunta sa MainActivity
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    // Sinasabi ni RecyclerView kung ilang beses dapat "umulit" ang binding
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // Ginagamit ng MainActivity para i-refresh yung listahan (hal. pagkatapos
    // mag-add/edit/delete ng item)
    public void updateList(List<Item> newList) {
        this.itemList = newList;
        notifyDataSetChanged();
    }

    // "Container" na humahawak sa mga View (TextViews) ng isang row, para
    // hindi na kailangan paulit-ulit na maghanap (findViewById) habang nag-scroll
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvStatus, tvLocation, tvDate;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
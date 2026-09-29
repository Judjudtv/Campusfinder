package com.example.campusfind.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusfind.R;
import com.example.campusfind.models.ClaimInfo;

import java.util.List;

public class ClaimAdapter extends RecyclerView.Adapter<ClaimAdapter.ClaimViewHolder> {

    public interface OnClaimClickListener {
        void onClaimClick(ClaimInfo claim);
    }

    private List<ClaimInfo> claimList;
    private OnClaimClickListener listener;

    public ClaimAdapter(List<ClaimInfo> claimList, OnClaimClickListener listener) {
        this.claimList = claimList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClaimViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_claim, parent, false);
        return new ClaimViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClaimViewHolder holder, int position) {
        ClaimInfo claim = claimList.get(position);

        // Kung na-delete na ang item o user, may "deleted" na label imbes na blangko
        String itemName = claim.getItemName() != null ? claim.getItemName() : "(deleted item)";
        String userName = claim.getUserName() != null ? claim.getUserName() : "(deleted user)";

        holder.tvClaimItemName.setText(itemName);
        holder.tvClaimUserName.setText("Claimed by: " + userName);
        holder.tvClaimStatus.setText(claim.getClaimStatus());

        // Kulay ng badge depende sa status
        String status = claim.getClaimStatus();
        if ("Approved".equalsIgnoreCase(status)) {
            holder.tvClaimStatus.setBackgroundColor(Color.parseColor("#43A047")); // green
        } else if ("Rejected".equalsIgnoreCase(status)) {
            holder.tvClaimStatus.setBackgroundColor(Color.parseColor("#E53935")); // red
        } else {
            holder.tvClaimStatus.setBackgroundColor(Color.parseColor("#FB8C00")); // orange (Pending)
        }

        holder.itemView.setOnClickListener(v -> listener.onClaimClick(claim));
    }

    @Override
    public int getItemCount() {
        return claimList.size();
    }

    public void updateList(List<ClaimInfo> newList) {
        this.claimList = newList;
        notifyDataSetChanged();
    }

    static class ClaimViewHolder extends RecyclerView.ViewHolder {
        TextView tvClaimItemName, tvClaimStatus, tvClaimUserName;

        public ClaimViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClaimItemName = itemView.findViewById(R.id.tvClaimItemName);
            tvClaimStatus = itemView.findViewById(R.id.tvClaimStatus);
            tvClaimUserName = itemView.findViewById(R.id.tvClaimUserName);
        }
    }
}
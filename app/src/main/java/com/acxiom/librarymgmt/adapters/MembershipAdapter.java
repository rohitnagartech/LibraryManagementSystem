package com.acxiom.librarymgmt.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.acxiom.librarymgmt.R;
import com.acxiom.librarymgmt.models.Membership;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MembershipAdapter extends RecyclerView.Adapter<MembershipAdapter.ViewHolder> {

    private List<Membership> membershipList;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public MembershipAdapter(List<Membership> membershipList) {
        this.membershipList = membershipList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_report_membership, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Membership membership = membershipList.get(position);
        holder.tvMemId.setText(membership.getMembershipId());
        holder.tvName.setText(membership.getFirstName() + " " + membership.getLastName());
        holder.tvContact.setText(membership.getContactNumber());
        holder.tvAddress.setText(membership.getContactAddress());
        holder.tvAadhar.setText(membership.getAadharCardNo());
        holder.tvStartDate.setText(membership.getStartDate() != null ? sdf.format(membership.getStartDate()) : "");
        holder.tvEndDate.setText(membership.getEndDate() != null ? sdf.format(membership.getEndDate()) : "");
        holder.tvStatus.setText(membership.getStatus());
        holder.tvFine.setText(String.format(Locale.getDefault(), "%.2f", membership.getAmountPendingFine()));

        if ("inactive".equalsIgnoreCase(membership.getStatus())) {
            holder.tvMemId.setTextColor(Color.GRAY);
            holder.tvName.setTextColor(Color.GRAY);
            holder.tvContact.setTextColor(Color.GRAY);
            holder.tvAddress.setTextColor(Color.GRAY);
            holder.tvAadhar.setTextColor(Color.GRAY);
            holder.tvStartDate.setTextColor(Color.GRAY);
            holder.tvEndDate.setTextColor(Color.GRAY);
            holder.tvStatus.setTextColor(Color.GRAY);
            holder.tvFine.setTextColor(Color.GRAY);
        } else {
            int black = Color.BLACK;
            holder.tvMemId.setTextColor(black);
            holder.tvName.setTextColor(black);
            holder.tvContact.setTextColor(black);
            holder.tvAddress.setTextColor(black);
            holder.tvAadhar.setTextColor(black);
            holder.tvStartDate.setTextColor(black);
            holder.tvEndDate.setTextColor(black);
            holder.tvStatus.setTextColor(black);
            holder.tvFine.setTextColor(black);
        }
    }

    @Override
    public int getItemCount() {
        return membershipList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMemId, tvName, tvContact, tvAddress, tvAadhar, tvStartDate, tvEndDate, tvStatus, tvFine;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMemId = itemView.findViewById(R.id.tv_mem_id);
            tvName = itemView.findViewById(R.id.tv_name);
            tvContact = itemView.findViewById(R.id.tv_contact);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvAadhar = itemView.findViewById(R.id.tv_aadhar);
            tvStartDate = itemView.findViewById(R.id.tv_start_date);
            tvEndDate = itemView.findViewById(R.id.tv_end_date);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvFine = itemView.findViewById(R.id.tv_fine);
        }
    }
}

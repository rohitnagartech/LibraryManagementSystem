package com.acxiom.librarymgmt.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.acxiom.librarymgmt.R;
import com.acxiom.librarymgmt.models.IssueRequest;

import java.util.List;

public class IssueRequestAdapter extends RecyclerView.Adapter<IssueRequestAdapter.ViewHolder> {

    private List<IssueRequest> requestList;

    public IssueRequestAdapter(List<IssueRequest> requestList) {
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_report_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IssueRequest request = requestList.get(position);
        holder.tvMemId.setText(request.getMembershipId());
        holder.tvBookName.setText(request.getBookName());
        holder.tvReqDate.setText(request.getRequestedDate());
        holder.tvFulDate.setText(request.getFulfilledDate() != null ? request.getFulfilledDate() : "-");
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMemId, tvBookName, tvReqDate, tvFulDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMemId = itemView.findViewById(R.id.tv_mem_id);
            tvBookName = itemView.findViewById(R.id.tv_book_name);
            tvReqDate = itemView.findViewById(R.id.tv_req_date);
            tvFulDate = itemView.findViewById(R.id.tv_ful_date);
        }
    }
}

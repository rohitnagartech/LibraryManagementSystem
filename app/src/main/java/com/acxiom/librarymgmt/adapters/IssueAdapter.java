package com.acxiom.librarymgmt.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.acxiom.librarymgmt.R;
import com.acxiom.librarymgmt.models.Issue;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.ViewHolder> {

    private List<Issue> issueList;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public IssueAdapter(List<Issue> issueList) {
        this.issueList = issueList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_report_issue, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Issue issue = issueList.get(position);
        holder.tvSerial.setText(issue.getSerialNo());
        holder.tvName.setText(issue.getBookName());
        holder.tvMemId.setText(issue.getMembershipId());
        holder.tvIssueDate.setText(sdf.format(issue.getIssueDate()));
        holder.tvReturnDate.setText(sdf.format(issue.getReturnDate()));
        holder.tvFine.setText(String.format(Locale.getDefault(), "%.2f", issue.getFineCalculated()));

        if ("overdue".equalsIgnoreCase(issue.getStatus())) {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFCDD2"));
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return issueList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSerial, tvName, tvMemId, tvIssueDate, tvReturnDate, tvFine;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSerial = itemView.findViewById(R.id.tv_serial);
            tvName = itemView.findViewById(R.id.tv_name);
            tvMemId = itemView.findViewById(R.id.tv_mem_id);
            tvIssueDate = itemView.findViewById(R.id.tv_issue_date);
            tvReturnDate = itemView.findViewById(R.id.tv_return_date);
            tvFine = itemView.findViewById(R.id.tv_fine);
        }
    }
}

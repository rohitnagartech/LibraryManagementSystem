package com.acxiom.librarymgmt.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.acxiom.librarymgmt.R;
import com.acxiom.librarymgmt.models.Book;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReportBookAdapter extends RecyclerView.Adapter<ReportBookAdapter.ViewHolder> {

    private List<Book> bookList;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public ReportBookAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_report_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.tvSerial.setText(book.getSerialNo());
        holder.tvName.setText(book.getName());
        holder.tvAuthor.setText(book.getAuthorName());
        holder.tvCategory.setText(book.getCategory());
        holder.tvStatus.setText(book.getStatus());
        holder.tvCost.setText(String.valueOf(book.getCost()));
        holder.tvDate.setText(book.getProcurementDate() != null ? sdf.format(book.getProcurementDate()) : "");

        if (position % 2 == 1) {
            holder.itemView.setBackgroundColor(Color.parseColor("#F5F5F5"));
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSerial, tvName, tvAuthor, tvCategory, tvStatus, tvCost, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSerial = itemView.findViewById(R.id.tv_serial);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvCost = itemView.findViewById(R.id.tv_cost);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }
}

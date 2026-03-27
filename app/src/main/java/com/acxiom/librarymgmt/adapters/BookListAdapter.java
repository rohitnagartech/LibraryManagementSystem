package com.acxiom.librarymgmt.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.acxiom.librarymgmt.R;
import com.acxiom.librarymgmt.models.Book;

import java.util.List;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.BookViewHolder> {

    private List<Book> bookList;
    private OnBookSelectedListener listener;
    private int selectedPosition = -1;

    public interface OnBookSelectedListener {
        void onBookSelected(String serialNo);
    }

    public BookListAdapter(List<Book> bookList, OnBookSelectedListener listener) {
        this.bookList = bookList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_book_item, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.tvBookName.setText(book.getName());
        holder.tvAuthor.setText(book.getAuthorName());
        holder.tvSerialNo.setText(book.getSerialNo());
        holder.tvAvailable.setText(book.getStatus() != null && book.getStatus().equalsIgnoreCase("available") ? "Y" : "N");

        holder.rbSelect.setEnabled(book.getStatus() != null && book.getStatus().equalsIgnoreCase("available"));
        holder.rbSelect.setChecked(position == selectedPosition);

        holder.rbSelect.setOnClickListener(v -> {
            selectedPosition = holder.getAbsoluteAdapterPosition();
            notifyDataSetChanged();
            if (listener != null) {
                listener.onBookSelected(book.getSerialNo());
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList != null ? bookList.size() : 0;
    }

    public String getSelectedSerialNo() {
        if (selectedPosition != -1 && bookList != null && selectedPosition < bookList.size()) {
            return bookList.get(selectedPosition).getSerialNo();
        }
        return null;
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookName, tvAuthor, tvSerialNo, tvAvailable;
        RadioButton rbSelect;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookName = itemView.findViewById(R.id.tv_book_name);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvSerialNo = itemView.findViewById(R.id.tv_serial_no);
            tvAvailable = itemView.findViewById(R.id.tv_available);
            rbSelect = itemView.findViewById(R.id.rb_select);
        }
    }
}

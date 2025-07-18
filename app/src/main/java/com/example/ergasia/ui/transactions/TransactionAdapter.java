package com.example.ergasia.ui.transactions;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ergasia.MainActivity;
import com.example.ergasia.R;
import com.example.ergasia.database.Category;
import com.example.ergasia.database.Transactions;

import java.util.Locale;
import java.util.Objects;

public class TransactionAdapter extends ListAdapter<Transactions, RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_INCOME = 0;
    private static final int VIEW_TYPE_OUTCOME = 1;

    public TransactionAdapter(Context context) {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Transactions> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Transactions>() {
                @Override
                public boolean areItemsTheSame(@NonNull Transactions oldItem, @NonNull Transactions newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Transactions oldItem, @NonNull Transactions newItem) {
                    return Objects.equals(oldItem.getType(), newItem.getType()) &&
                            oldItem.getValue() == newItem.getValue() &&
                            Objects.equals(oldItem.getDate(), newItem.getDate()) &&
                            oldItem.getCatId() == newItem.getCatId();
                }
            };

    @Override
    public int getItemViewType(int position) {
        Transactions transaction = getItem(position);
        if (transaction != null && "ΕΣΟΔΑ".equalsIgnoreCase(transaction.getType())) {
            return VIEW_TYPE_INCOME;
        } else {
            return VIEW_TYPE_OUTCOME;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_INCOME) {
            View itemView = inflater.inflate(R.layout.recycler_row_income, parent, false);
            return new IncomeViewHolder(itemView);
        } else {
            View itemView = inflater.inflate(R.layout.recycler_row_outcome, parent, false);
            return new OutcomeViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Transactions currentTransaction = getItem(position);
        if (currentTransaction == null) {
            return;
        }

        if (holder.getItemViewType() == VIEW_TYPE_INCOME) {
            ((IncomeViewHolder) holder).bind(currentTransaction);
        } else if (holder.getItemViewType() == VIEW_TYPE_OUTCOME) {
            ((OutcomeViewHolder) holder).bind(currentTransaction);
        }
    }

    static class IncomeViewHolder extends RecyclerView.ViewHolder {
        private final ImageView rerowImage;
        private final TextView rerowType;
        private final TextView rerowId;
        private final TextView rerowAmount;
        private final TextView rerowDate;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            rerowImage = itemView.findViewById(R.id.rerowImage);
            rerowType = itemView.findViewById(R.id.rerowType);
            rerowId = itemView.findViewById(R.id.rerowId);
            rerowAmount = itemView.findViewById(R.id.rerowAmount);
            rerowDate = itemView.findViewById(R.id.rerowDate);
        }

        public void bind(final Transactions transaction) {
            rerowId.setText("Κωδικός: " + transaction.getId());
            rerowType.setText("Τύπος: " + transaction.getType());
            rerowAmount.setText(String.format(Locale.getDefault(), "Ποσό: %.2f€", (double)transaction.getValue()));
            rerowDate.setText("Ημερομηνία: " + transaction.getDate());
            rerowAmount.setTextColor(Color.parseColor("#4CAF50")); // Green
        }
    }

    static class OutcomeViewHolder extends RecyclerView.ViewHolder {
        private final ImageView rerowImage;
        private final TextView rerowType;
        private final TextView rerowId;
        private final TextView rerowAmount;
        private final TextView rerowDate;
        private final TextView rerowCat;

        public OutcomeViewHolder(@NonNull View itemView) {
            super(itemView);
            rerowImage = itemView.findViewById(R.id.rerowImage);
            rerowType = itemView.findViewById(R.id.rerowType);
            rerowId = itemView.findViewById(R.id.rerowId);
            rerowAmount = itemView.findViewById(R.id.rerowAmount);
            rerowDate = itemView.findViewById(R.id.rerowDate);
            rerowCat = itemView.findViewById(R.id.rerowCat);
        }

        public void bind(final Transactions transaction) {
            rerowId.setText("Κωδικός: " + transaction.getId());
            rerowType.setText("Τύπος: " + transaction.getType());
            rerowAmount.setText(String.format(Locale.getDefault(), "Ποσό: %.2f€", (double)transaction.getValue()));
            rerowDate.setText("Ημερομηνία: " + transaction.getDate());

            String categoryName = "";
            if (transaction.getCatId() > 0) {
                Category category = MainActivity.myDatabase.myDao().getCategoryById(transaction.getCatId());
                if (category != null) {
                    categoryName = category.getCname();
                }
            }
            rerowCat.setText("Κατηγορία: " + categoryName);
            rerowAmount.setTextColor(Color.parseColor("#F44336")); // Red
        }
    }
}

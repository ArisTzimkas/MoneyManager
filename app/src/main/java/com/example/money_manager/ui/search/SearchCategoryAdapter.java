package com.example.money_manager.ui.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.money_manager.R;
import com.example.money_manager.database.CategoryTransactionCount;

import java.util.Objects;

public class SearchCategoryAdapter extends ListAdapter<CategoryTransactionCount, SearchCategoryAdapter.CategorySumViewHolder> {


    public SearchCategoryAdapter(Context context) {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<CategoryTransactionCount> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<CategoryTransactionCount>() {
                @Override
                public boolean areItemsTheSame(@NonNull CategoryTransactionCount oldItem, @NonNull CategoryTransactionCount newItem) {
                    return Objects.equals(oldItem.getCategoryName(), newItem.getCategoryName());
                }

                @Override
                public boolean areContentsTheSame(@NonNull CategoryTransactionCount oldItem, @NonNull CategoryTransactionCount newItem) {
                    return Objects.equals(oldItem.getCategoryName(), newItem.getCategoryName()) && oldItem.getTransactionCount() == newItem.getTransactionCount() ;
                }
            };


    @NonNull
    @Override
    public CategorySumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.recycler_row_outcome, parent, false);
        return new CategorySumViewHolder(itemView) {
        };
    }


    @Override // Add @Override annotation
    public void onBindViewHolder(@NonNull CategorySumViewHolder holder, int position) {
        CategoryTransactionCount item = getItem(position);
        if (item != null) { // Check for not null before binding
            holder.bind(item);
        }
    }

    static class CategorySumViewHolder extends RecyclerView.ViewHolder {
        private final TextView rerowType;
        private final TextView rerowId;
        private final TextView rerowAmount;
        private final TextView rerowDate;
        private final TextView rerowCat;

        public CategorySumViewHolder(@NonNull View itemView) {
            super(itemView);
            rerowType = itemView.findViewById(R.id.rerowType);
            rerowId = itemView.findViewById(R.id.rerowId);
            rerowAmount = itemView.findViewById(R.id.rerowAmount);
            rerowDate = itemView.findViewById(R.id.rerowDate);
            rerowCat = itemView.findViewById(R.id.rerowCat);
        }

        public void bind(final CategoryTransactionCount catCount) {
            rerowAmount.setVisibility(View.GONE);
            rerowCat.setVisibility(View.GONE);
            rerowType.setVisibility(View.GONE);
            rerowDate.setText("Κατηγορία: " + catCount.getCategoryName());
            rerowId.setText("Πλήθος συναλλαγών:"+catCount.getTransactionCount());



        }
    }
}

package com.example.ergasia.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ergasia.MainActivity;
import com.example.ergasia.R;
import com.example.ergasia.database.Category;
import com.example.ergasia.database.CategoryTransactionCount;
import com.example.ergasia.database.Transactions;
import com.example.ergasia.databinding.FragmentSearchBinding;
import com.example.ergasia.ui.transactions.TransactionAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    Button searchB;
    Button totalSearch;
    TextInputEditText datefrom;
    TextInputEditText dateto;
    TextView display;
    FirebaseAuth mAuth;
    String user;


    private static class DateMaskTextWatcher implements TextWatcher {
        private String current = "";
        private final TextInputEditText editText;
        private boolean isUpdating = false;

        DateMaskTextWatcher(TextInputEditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (isUpdating || s.toString().equals(current)) {
                return;
            }
            isUpdating = true;
            String clean = s.toString().replaceAll("[^\\d]", "");
            String originalClean = clean;

            if (clean.length() > 8) {
                clean = clean.substring(0, 8);
            }

            StringBuilder formatted = new StringBuilder();
            int selectionPos = editText.getSelectionStart();

            if (!clean.isEmpty()) {
                if (clean.length() >= 4) {
                    formatted.append(clean.substring(0, 4));
                    if (clean.length() > 4 || s.length() > 4) {
                        formatted.append("-");
                    }
                } else {
                    formatted.append(clean);
                }

                if (clean.length() >= 6 && formatted.length() == 5) { // If we have YYYY- and more digits for MM
                    formatted.append(clean.substring(4, 6));
                    if (clean.length() > 6 || s.length() > 7) { // User typed past MM or a hyphen was just added
                        formatted.append("-");
                    }
                } else if (clean.length() > 4 && formatted.length() > 4 && formatted.charAt(formatted.length()-1) == '-') {
                    // YYYY- and some digits for MM but less than 2
                    formatted.append(clean.substring(4, Math.min(4 + (clean.length() - 4), 6) ));
                }


                if (clean.length() == 8 && formatted.length() == 8) { // If we have YYYY-MM- and more digits for DD
                    formatted.append(clean.substring(6, 8));
                } else if (clean.length() > 6 && formatted.length() > 7 && formatted.charAt(formatted.length()-1) == '-') {
                    // YYYY-MM- and some digits for DD but less than 2
                    if (clean.length() - 6 > 0) formatted.append(clean.substring(6, Math.min(6 + (clean.length() - 6), 8) ));
                }
            }


            current = formatted.toString();
            editText.setText(current);

            // Cursor positioning logic (can be complex)
            // A simpler approach for this Java version: try to place it at the end of the input
            // or where it was if no hyphens were added/removed at the cursor's previous position.
            int newSelection = selectionPos + (current.length() - s.length());
            if (newSelection < 0) newSelection = 0;
            if (newSelection > current.length()) newSelection = current.length();

            // More specific cursor adjustment if a hyphen was just programmatically added
            if ( (originalClean.length() == 4 && clean.length() == 4 && current.length() == 5 && current.charAt(4) == '-') ||
                    (originalClean.length() == 6 && clean.length() == 6 && current.length() == 8 && current.charAt(7) == '-') ) {
                if (selectionPos == formatted.length() -1 ) { // If cursor was right before the new hyphen
                    newSelection = formatted.length();
                }
            }


            editText.setSelection(Math.min(newSelection, current.length()));
            isUpdating = false;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentSearchBinding binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        datefrom=root.findViewById(R.id.dateFrom);
        dateto=root.findViewById(R.id.dateTo);

        datefrom.addTextChangedListener(new DateMaskTextWatcher(datefrom));
        dateto.addTextChangedListener(new DateMaskTextWatcher(dateto));

        List<String> spinArray4 = new ArrayList<>();
        List<Category> categoryNames = MainActivity.myDatabase.myDao().getCategory();
        for (Category category : categoryNames) {
            spinArray4.add(category.getCname());
        }
        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(requireContext(), R.layout.dropdown_menu, spinArray4);

        AutoCompleteTextView autoText=root.findViewById(R.id.spinner4);
        autoText.setAdapter(adapter4);


        mAuth= FirebaseAuth.getInstance();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        assert currentUser != null;
        user=currentUser.getUid();


        TransactionAdapter adapter = new TransactionAdapter(getContext());


        assert binding.Recycler != null;
        binding.Recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.Recycler.setAdapter(adapter);
        binding.Recycler.setVisibility(View.VISIBLE);

        searchB=root.findViewById(R.id.searchButton);
        searchB.setOnClickListener(v -> {
            String selectedCategory = autoText.getText().toString();
            String startDate = datefrom.getText().toString();
            String endDate = dateto.getText().toString();

            String dateFormatPattern = "\\d{4}-\\d{2}-\\d{2}";

            if (startDate.length()==10 && endDate.length()==10) {
                if (startDate.matches(dateFormatPattern) && endDate.matches(dateFormatPattern)) {
                    List<Transactions> transactions = MainActivity.myDatabase.myDao().getTransactionsByCategoryAndDateRangeAndUser(selectedCategory, startDate, endDate, user);
                    Log.d("SearchFragment", "DAO returned, transaction count: " + (transactions != null ? transactions.size() : "null list"));
                    adapter.submitList(transactions);
                    Log.d("AdapterCount", String.valueOf(adapter.getItemCount()));

                }
            } else {
                Toast.makeText(requireContext(), "Εισάγετε και τις δύο ημερομηνίες", Toast.LENGTH_SHORT).show();
            }
        });

        totalSearch=root.findViewById(R.id.totalSearch);
        totalSearch.setOnClickListener(v -> {
            List<CategoryTransactionCount> totalS = MainActivity.myDatabase.myDao().getTransactionCountByCategoryAndUser(user);
            StringBuilder transactionText = new StringBuilder();
            for (CategoryTransactionCount category : totalS) {
                String transactionInfo = "\nΚατηγορία: " + category.getCategoryName() + "\nΠλήθος Συναλλαγών: " + category.getTransactionCount() + "\n_____________________________";
                transactionText.append(transactionInfo);
            }
        });
        return root;
    }
}

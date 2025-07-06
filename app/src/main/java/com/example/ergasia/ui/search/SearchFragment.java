package com.example.ergasia.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.fragment.app.Fragment;

import com.example.ergasia.MainActivity;
import com.example.ergasia.R;
import com.example.ergasia.database.Category;
import com.example.ergasia.database.CategoryTransactionCount;
import com.example.ergasia.database.Transactions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    Button searchB;

    Button totalSearch;
    EditText datefrom;
    EditText dateto;
    Spinner spinner4;

    TextView display;

    FirebaseAuth mAuth;

    String user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        datefrom=root.findViewById(R.id.dateFrom);
        dateto=root.findViewById(R.id.dateTo);


        List<String> spinArray4 = new ArrayList<>();
        List<Category> categoryNames = MainActivity.myDatabase.myDao().getCategory();
        for (Category category : categoryNames) {
            spinArray4.add(category.getCname());
        }
        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, spinArray4);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner4 = (Spinner) root.findViewById(R.id.spinner4);
        spinner4.setAdapter(adapter4);

        mAuth= FirebaseAuth.getInstance();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        user=currentUser.getUid();

        display=root.findViewById(R.id.displaySearchtext);



        searchB=root.findViewById(R.id.searchButton);
        searchB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedCategory = spinner4.getSelectedItem().toString();
                String startDate = datefrom.getText().toString();
                String endDate = dateto.getText().toString();

                String dateFormatPattern = "\\d{4}-\\d{2}-\\d{2}";

                if (!startDate.isEmpty() && !endDate.isEmpty()) {
                    if (startDate.matches(dateFormatPattern) && endDate.matches(dateFormatPattern)) {
                        List<Transactions> transactions = MainActivity.myDatabase.myDao().getTransactionsByCategoryAndDateRangeAndUser(selectedCategory, startDate, endDate, user);
                        StringBuilder transactionText = new StringBuilder();

                        for (Transactions transaction : transactions) {
                            String transactionInfo = "\nΚωδικός Συναλλαγής: " + transaction.getId() + "\nΤύπος: " + transaction.getType() + "\nΠοσό: " + transaction.getValue() + "\n_____________________________";
                            transactionText.insert(0, transactionInfo);
                        }
                        display.setText(transactionText.toString());
                    } else {
                        Toast.makeText(requireContext(), "Ημερομηνία πχ 2024-05-13", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Εισάγετε και τις δύο ημερομηνίες", Toast.LENGTH_SHORT).show();
                }
            }
        });

        totalSearch=root.findViewById(R.id.totalSearch);
        totalSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<CategoryTransactionCount> totalS = MainActivity.myDatabase.myDao().getTransactionCountByCategoryAndUser(user);
                StringBuilder transactionText = new StringBuilder();
                for (CategoryTransactionCount category : totalS) {
                    String transactionInfo = "\nΚατηγορία: " + category.getCategoryName() + "\nΠλήθος Συναλλαγών: " + category.getTransactionCount() + "\n_____________________________";
                    transactionText.append(transactionInfo);
                }
                display.setText(transactionText.toString());
            }
        });




        return root;
    }


}

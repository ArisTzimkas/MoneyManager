package com.example.ergasia.ui.transactions;

import android.annotation.SuppressLint;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProvider;

import com.example.ergasia.MainActivity;
import com.example.ergasia.R;
import com.example.ergasia.database.Category;
import com.example.ergasia.database.Transactions;
import com.example.ergasia.databinding.FragmentTransactionsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class TransactionsFragment extends Fragment {

    private FragmentTransactionsBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String transactionText;


    public TransactionsFragment() {
        //empty public constructor
    }

    public static TransactionsFragment newInstance(String param1, String param2) {
        TransactionsFragment fragment = new TransactionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    FirebaseAuth mAuth;
    @SuppressLint("StaticFieldLeak")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTransactionsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser currentUser=mAuth.getCurrentUser();

        TextView textView = root.findViewById(R.id.text_transactions);
        ImageView imageView = root.findViewById(R.id.imageNoData);
        TextView textNoData = root.findViewById(R.id.textNoData);

        assert currentUser != null;
        List<Transactions> transactions = MainActivity.myDatabase.myDao().getTransactionsByUserId(currentUser.getUid());
        StringBuilder result = new StringBuilder();
        for (Transactions transaction : transactions) {
            int id = transaction.getId();
            String type = transaction.getType();
            int value = transaction.getValue();
            String date=transaction.getDate();
            int categoryid=transaction.getCatId();

            String categoryName = "";
            Category category = MainActivity.myDatabase.myDao().getCategoryById(categoryid);
            if (category != null) {
                categoryName = category.getCname();
            }

            if ("ΕΣΟΔΑ".equals(type)) {
                transactionText = "\n\nΚωδικός Συναλλαγής: " + id + "\nΤύπος: " + type + "\nΠοσό: " + value + "\nΗμερομηνία:" + date + "\n_____________________________";
            }
            else if("ΕΞΟΔΑ".equals(type)){
                transactionText = "\n\nΚωδικός Συναλλαγής: " + id + "\nΤύπος: " + type + "\nΠοσό: " + value + "\nΗμερομηνία:" + date + "\nΚατηγορία:" + categoryName+"\n_____________________________";
            }
            result.insert(0, transactionText);
        }
        textView.setText(result.toString());

        //Display
        if (transactions.isEmpty()) {
            textView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            textNoData.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            textNoData.setVisibility(View.GONE);
            textView.setText(result.toString());
        }
        return root;
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

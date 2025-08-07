package com.example.money_manager.ui.transactions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.money_manager.MainActivity;
import com.example.money_manager.R;
import com.example.money_manager.database.Transactions;
import com.example.money_manager.databinding.FragmentTransactionBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TransactionFragment extends Fragment {
    private FragmentTransactionBinding binding;
    FirebaseAuth mAuth;
    private TransactionAdapter adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        adapter=new TransactionAdapter(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTransactionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.Recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.Recycler.setAdapter(adapter);

        loadTransactions();
    }

    private void loadTransactions(){
        List<Transactions> transactions= MainActivity.myDatabase.myDao().getTransactionsByUserId(mAuth.getCurrentUser().getUid());
        Collections.reverse(transactions);
        adapter.submitList(transactions);


        // if list is empty
        View root = binding.getRoot();
        ImageView imageView = root.findViewById(R.id.imageNoData);
        TextView textNoData = root.findViewById(R.id.textNoData);
        if (transactions.isEmpty()) {
            imageView.setVisibility(View.VISIBLE);
            textNoData.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
            textNoData.setVisibility(View.GONE);
        }


        adapter.submitList(new ArrayList<>(transactions));

    }
}

package com.example.money_manager.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.money_manager.MainActivity;
import com.example.money_manager.R;
import com.example.money_manager.database.Category;
import com.example.money_manager.databinding.FragmentAddCategoryBinding;
import com.google.android.material.textfield.TextInputLayout;

public class AddCategoryFragment extends Fragment {
    private FragmentAddCategoryBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText input=view.findViewById(R.id.addcategoryinput);
        TextInputLayout addlayout = binding.addCategoryInputLayout;

        binding.addcategorybutton.setOnClickListener(v -> {
            String categoryName = input.getText().toString().trim();
            if (categoryName.isEmpty()|| categoryName.length()>15) {
                addlayout.setError("Απαιτήται *");
                addlayout.requestFocus();
                return;
            } else {
                addlayout.setError(null);
            }
            int lastCategoryId = MainActivity.myDatabase.myDao().getLastCategoryId();
            Category category = new Category();
            category.setCname(categoryName);
            category.setCid(lastCategoryId + 1);

            MainActivity.myDatabase.myDao().addCategory(category);
            input.setText("");
            Toast.makeText(getContext(), "Η κατηγορία προσθέθηκε", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding=null;
    }
}

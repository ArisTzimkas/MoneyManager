package com.example.ergasia.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ergasia.MainActivity;
import com.example.ergasia.R;
import com.example.ergasia.database.Category;
import com.example.ergasia.databinding.FragmentAddCategoryBinding;

public class AddCategoryFragment extends Fragment {
    private FragmentAddCategoryBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_category, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button add=view.findViewById(R.id.addcategorybutton);
        EditText input=view.findViewById(R.id.addcategoryinput);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryName = input.getText().toString().trim();
                if (!categoryName.isEmpty()) {

                    int lastCategoryId = MainActivity.myDatabase.myDao().getLastCategoryId();
                    Category category = new Category();
                    category.setCname(categoryName);
                    category.setCid(lastCategoryId + 1);

                    MainActivity.myDatabase.myDao().addCategory(category);
                    input.setText("");
                    Toast.makeText(getContext(), "Η κατηγορία προσθέθηκε", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Εισάγετε νέα κατηγορία", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

package com.example.ergasia.ui.user;

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
import com.example.ergasia.database.UserGoal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GoalFragment extends Fragment {
    EditText goalInput;
    Button save;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_goal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        goalInput=view.findViewById(R.id.inputgoal);
        save=view.findViewById(R.id.goalsave);

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        String id=currentUser.getUid();


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String g = goalInput.getText().toString();
                if (!g.isEmpty()) {
                    int goal = Integer.parseInt(g);
                    UserGoal userGoal = new UserGoal();
                    userGoal.setUid(id);
                    userGoal.setGoal(goal);
                    MainActivity.db.collection("UserGoal").document("" + id).set(userGoal).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getContext(), "Έγινε αποθήκευση", Toast.LENGTH_SHORT).show();
                                goalInput.setText("");
                            } else {
                                Toast.makeText(getContext(), "Failed to save goal", Toast.LENGTH_SHORT).show();
                                goalInput.setText("");
                            }
                        }
                    });
                }
            }
        });

    }
}

package com.example.money_manager.ui.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.money_manager.MainActivity;
import com.example.money_manager.R;
import com.example.money_manager.database.UserGoal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
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
        assert currentUser != null;
        String id=currentUser.getUid();

        TextInputLayout valueLayout= view.findViewById(R.id.goalLayout);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String g = goalInput.getText().toString();
                if(TextUtils.isEmpty(g)||g.equals("0")) {
                    valueLayout.setError("Απαιτείται *");
                    valueLayout.requestFocus();
                    return;
                }else {
                    valueLayout.setError(null);
                }
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
                        }
                    }
                });
            }
        });

    }
}

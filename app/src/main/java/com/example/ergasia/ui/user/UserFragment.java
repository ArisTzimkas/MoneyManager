package com.example.ergasia.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.ergasia.LoginActivity;
import com.example.ergasia.MainActivity;
import com.example.ergasia.R;
import com.example.ergasia.database.UserGoal;
import com.example.ergasia.databinding.FragmentUserBinding;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;



public class UserFragment extends Fragment {

    private FragmentUserBinding binding;
    Button logout;
    Button goal;
    FirebaseAuth mAuth;

    TextView email;
    TextView id;

    TextView goalText;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /////////
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        binding = FragmentUserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        /////////


        mAuth=FirebaseAuth.getInstance();

        FirebaseUser currentUser=mAuth.getCurrentUser();

        email=root.findViewById(R.id.emailUser);
        id=root.findViewById(R.id.textid);
        logout=root.findViewById(R.id.connectbutton);
        goal=root.findViewById(R.id.buttonGoal);
        goalText=root.findViewById(R.id.goalText);

        if (currentUser != null) {
            // Set the email to the TextView
            email.setText(currentUser.getEmail());
            id.setText(currentUser.getUid());
        }


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(requireActivity(), LoginActivity.class));
            }
        });


        goal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_to_goal);
            }
        });

        MainActivity.db.collection("UserGoal").document(""+currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        UserGoal userGoal = document.toObject(UserGoal.class);
                        if (userGoal != null) {
                            int goal = userGoal.getGoal();
                            goalText.setText(String.valueOf(goal));
                        }
                    }
                } else {
                    // Task failed with an exception
                    Exception exception = task.getException();
                    // Handle the exception
                }
            }
        });




        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
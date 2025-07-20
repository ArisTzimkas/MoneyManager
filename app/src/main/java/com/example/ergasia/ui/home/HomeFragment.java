package com.example.ergasia.ui.home;
import static com.example.ergasia.MainActivity.db;


import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ergasia.MainActivity;
import com.example.ergasia.R;
import com.example.ergasia.database.Transactions;
import com.example.ergasia.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;



public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    TextView textLast;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    int total;

    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        textLast = root.findViewById(R.id.textLastTr);



        mAuth=FirebaseAuth.getInstance();
        try {
            // Attempt to retrieve the current user
            currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                Log.e("HomeFragment", "Current user is null");

                Toast.makeText(getContext(), "Παρακαλώ πραγματοποιήστε σύνδεση με λογαριασμό", Toast.LENGTH_SHORT).show();

                return root;
            }

            Transactions lastTransaction = MainActivity.myDatabase.myDao().getLastTransaction(currentUser.getUid());

        } catch (Exception e) {
            Log.e("HomeFragment", "Error retrieving last transaction: " + e.getMessage());
        }
        Transactions lastTransaction = MainActivity.myDatabase.myDao().getLastTransaction(currentUser.getUid());

        if (lastTransaction == null) {
            Log.d("HomeFragment", "Last transaction is null. Setting text to default message.");
            textLast.setText("Δεν υπάρχουν συναλλαγές");
        } else {
            String stringLast = "Κατηγορία: " + lastTransaction.getType() + "\nΠοσό: " + lastTransaction.getValue();
            Log.d("HomeFragment", "Last transaction retrieved successfully: " + stringLast);
            textLast.setText(stringLast);
        }


        TextView dTotal=root.findViewById(R.id.displayTotal);

        MainActivity.db.collection("UserTotal").document("" + currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    DocumentSnapshot document = task.getResult();
                    Object fieldValue = document.get("total");
                    long totalLong = (long) fieldValue;
                    total = (int) totalLong;
                    dTotal.setText(String.valueOf(total));

                db.collection("UserGoal").document("" + currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    TextView textpro2=root.findViewById(R.id.textpro2);
                    ProgressBar p2 = root.findViewById(R.id.progressBar2);
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document != null && document.exists()) {
                                Object fieldValue = document.get("goal");
                                if (fieldValue != null) {
                                    long totalGoal = (long) fieldValue;
                                    int goal = (int) totalGoal;
                                    int percentage = (int) ((total * 100) / goal);

                                    p2.setProgress(percentage);
                                    p2.setMax(100);

                                    MainActivity.db.collection("UserGoal").document(""+currentUser.getUid()).update("percent", percentage);

                                    textpro2.setText(percentage+"%");
                                }
                            }else {
                                p2.setVisibility(View.GONE);
                                textpro2.setText("Δεν έχει οριστεί στόχος, μεταβείτε στο Λογαριασμός.");
                            }
                        }
                    }

                });


            }
        });

        TextView dEmail=root.findViewById(R.id.displayUser);
        dEmail.setText(currentUser.getEmail());



        TextView textprogress=root.findViewById(R.id.textpro);

        try {
            
            int totalIncome = MainActivity.myDatabase.myDao().getTotalIncome(currentUser.getUid());
            int totalExpenses = MainActivity.myDatabase.myDao().getTotalExpenses(currentUser.getUid());


            int totalAmount = totalIncome + totalExpenses;



            if (totalAmount != 0) {

                double incomePercentage = (double) totalIncome / totalAmount * 100;
                double expensesPercentage = (double) totalExpenses / totalAmount * 100;


                int roundedIncomePercentage = (int) Math.round(incomePercentage);
                int roundedExpensesPercentage = (int) Math.round(expensesPercentage);

                Drawable progressDrawable = getResources().getDrawable(R.drawable.progress);

                ProgressBar progressBar = root.findViewById(R.id.progressBar1);
                progressBar.setProgressDrawable(progressDrawable);
                progressBar.setProgress(roundedIncomePercentage);
                progressBar.setMax(100);


                TextView homeIn = root.findViewById(R.id.homeIn);
                TextView homeEx = root.findViewById(R.id.homeEx);
                homeIn.setText("Έσοδα: +" + roundedIncomePercentage + "%");
                homeEx.setText("Έξοδα: -" + roundedExpensesPercentage + "%");
                textprogress.setVisibility(View.GONE);
            } else {

                ProgressBar progressBar = root.findViewById(R.id.progressBar1);
                progressBar.setVisibility(View.GONE);
                textprogress.setText("Δεν υπάρχουν συναλλαγές");
            }






        } catch (Exception e) {
            Log.e("HomeFragment", "Error calculating progress bar: " + e.getMessage());
        }
        setHasOptionsMenu(false);














        return root;
    }










    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

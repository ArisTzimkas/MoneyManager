package com.example.money_manager.ui.home;
import static com.example.money_manager.MainActivity.db;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.example.money_manager.MainActivity;
import com.example.money_manager.PopUp;
import com.example.money_manager.R;
import com.example.money_manager.database.Transactions;
import com.example.money_manager.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String formattedNumber;

    int total;
    private static final String PROFILE_IMAGE_FILENAME_PREFIX = "profile_";
    private static final String PROFILE_IMAGE_FILENAME_SUFFIX = ".jpg";




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentUser = mAuth.getCurrentUser();


        NumberFormat numberFormatter = NumberFormat.getNumberInstance(Locale.getDefault());
        numberFormatter.setMinimumFractionDigits(2);
        numberFormatter.setMaximumFractionDigits(2);
        numberFormatter.setGroupingUsed(true);

        Transactions lastTransaction = MainActivity.myDatabase.myDao().getLastTransaction(currentUser.getUid());
        if (lastTransaction == null) {
            Log.d("HomeFragment", "Last transaction is null. Setting text to default message.");
            binding.textLastTr.setText("Δεν υπάρχουν συναλλαγές");
        } else {
            Drawable drawable;
            if (lastTransaction.getType().equals("ΕΣΟΔΑ")) {
                drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.up, null);
            } else {
                drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.down, null);
            }
            binding.imageView.setImageDrawable(drawable);
            String stringLast = "Κατηγορία : " + lastTransaction.getType() + "\nΠοσό : " + numberFormatter.format(lastTransaction.getValue()) + "€" + "\nΗμερομηνία : " + lastTransaction.getDate();
            Log.d("HomeFragment", "Last transaction retrieved successfully: " + stringLast);
            binding.textLastTr.setText(stringLast);
        }

        binding.progressBarAmountLoading.setVisibility(View.VISIBLE);
        db.collection("UserTotal").document("" + currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                Object fieldValue = document.get("total");
                long totalLong = (long) fieldValue;
                total = (int) totalLong;

                formattedNumber = numberFormatter.format((double) total);
                binding.progressBarAmountLoading.setVisibility(View.GONE);
                binding.displayTotal.setText(String.format(Locale.ROOT, "%s €", formattedNumber));

                binding.progressBar2Loading.setVisibility(View.VISIBLE);
                db.collection("UserGoal").document("" + currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    final CircularProgressBar p2 = binding.progressBar2;

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            binding.progressBar2Loading.setVisibility(View.GONE);
                            p2.setVisibility(View.VISIBLE);
                            DocumentSnapshot document = task.getResult();

                            if (document != null && document.exists()) {
                                Object fieldValue = document.get("goal");
                                if (fieldValue != null) {
                                    long totalGoal = (long) fieldValue;
                                    int goal = (int) totalGoal;
                                    int percentage = (total * 100) / goal;

                                    p2.setProgressWithAnimation((float) percentage, 3000L);
                                    p2.setProgressMax(100);

                                    db.collection("UserGoal").document("" + currentUser.getUid()).update("percent", percentage);

                                    binding.textpro2.setText(percentage + "%");
                                }
                            } else {
                                p2.setVisibility(View.GONE);
                                binding.textpro2.setText("Δεν έχει οριστεί στόχος, μεταβείτε στο Λογαριασμό");
                            }
                        }
                    }

                });


            }
        });

        binding.displayUser.setText(currentUser.getEmail());
        binding.progressBar1Loading.setVisibility(View.VISIBLE);
        try {
            int totalIncome = MainActivity.myDatabase.myDao().getTotalIncome(currentUser.getUid());
            int totalExpenses = MainActivity.myDatabase.myDao().getTotalExpenses(currentUser.getUid());
            int totalAmount = totalIncome + totalExpenses;
            CircularProgressBar progressBar = binding.progressBar1;

            if (totalAmount != 0) {
                double incomePercentage = (double) totalIncome / totalAmount * 100;
                double expensesPercentage = (double) totalExpenses / totalAmount * 100;

                int roundedIncomePercentage = (int) Math.round(incomePercentage);
                int roundedExpensesPercentage = (int) Math.round(expensesPercentage);

                binding.progressBar1Loading.setVisibility(View.GONE);
                progressBar.setProgressWithAnimation((float) roundedIncomePercentage, 3000L);
                progressBar.setProgressMax(100);

                binding.homeIn.setText("+" + roundedIncomePercentage + "%");
                binding.homeEx.setText("-" + roundedExpensesPercentage + "%");
                binding.textpro.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                binding.textIn.setVisibility(View.GONE);
                binding.textOut.setVisibility(View.GONE);
                binding.progressBar1Loading.setVisibility(View.GONE);
                binding.textpro.setText("Δεν υπάρχουν συναλλαγές");
            }
        } catch (Exception e) {
            Log.e("HomeFragment", "Error calculating progress bar: " + e.getMessage());
        }

        binding.profileImage.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_to_user));


        binding.newTransactionButton.setOnClickListener(v -> {
            Intent intent=new Intent(requireContext(), PopUp.class);
            startActivity(intent);
        });

        setHasOptionsMenu(false);
    }




    public void onStart() {
        super.onStart();
        displayCurrentUserProfileImage();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



    private void displayCurrentUserProfileImage() {
        if (getContext() == null) {
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        File profileImageFile = null;

        if (currentUser != null) {
            profileImageFile = getProfileImageFileForUser(getContext(), currentUser.getUid());
        }

        if (profileImageFile != null && profileImageFile.exists() && profileImageFile.length() > 0) {
            Glide.with(this)
                    .load(profileImageFile)
                    .signature(new ObjectKey(String.valueOf(profileImageFile.lastModified())))
                    .placeholder(R.drawable.person)
                    .error(R.drawable.person)
                    .circleCrop()
                    .into(binding.profileImage);
        } else {
            Glide.with(this)
                    .load(R.drawable.person)
                    .circleCrop()
                    .into(binding.profileImage);
        }
    }

    private File getProfileImageFileForUser(Context context, String userId) {
        if (context == null || userId == null || userId.isEmpty()) {
            return null;
        }
        String userSpecificFilename = PROFILE_IMAGE_FILENAME_PREFIX + userId + PROFILE_IMAGE_FILENAME_SUFFIX;
        return new File(context.getFilesDir(), userSpecificFilename);
    }
}

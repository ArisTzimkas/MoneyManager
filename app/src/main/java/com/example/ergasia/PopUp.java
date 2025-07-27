package com.example.ergasia;

import android.annotation.SuppressLint;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.example.ergasia.database.Category;
import com.example.ergasia.database.Transactions;
import com.example.ergasia.database.UserTotal;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.util.List;
import java.util.Locale;
import androidx.appcompat.app.AppCompatActivity;


public class PopUp extends AppCompatActivity {
    String selectedCategory;
    String selectedExpenseCategory;
    public int id;
    FirebaseAuth mAuth;
    AutoCompleteTextView sItems2;
    int value = 0;
    int newTotal;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);

        try {

            //////////////////////////////////////////////Start layout parameters
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);

            int width = dm.widthPixels;
            int height = dm.heightPixels;
            getWindow().setLayout((int) (width * .85), (int) (height * .7));

            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.gravity = Gravity.CENTER;
            params.x = 0;
            params.y = 0;
            getWindow().setAttributes(params);
            ///////////////////////////////////////////End layout parameters


            Button bclose = findViewById(R.id.button_close);
            Button bsave = findViewById(R.id.button_save);

            ////////////////////////////////////////////////////////press ΑΚΥΡΟ
            bclose.setOnClickListener(v -> finish());


            /////////////////////////////////////////////////////Fill spinner
            List<String> spinArray = new ArrayList<>();
            spinArray.add("ΕΣΟΔΑ");
            spinArray.add("ΕΞΟΔΑ");


            TextInputLayout spinnerLayout = findViewById(R.id.spinnerLayout);
            TextInputLayout spinnerLayout2 = findViewById(R.id.spinnerLayout2);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_menu, spinArray);
            AutoCompleteTextView sItems = findViewById(R.id.spinner);
            sItems.setAdapter(adapter);

            sItems.setOnItemClickListener((parentView, view, position, id) -> {
                String currentCategorySelection = parentView.getItemAtPosition(position).toString();
                if (currentCategorySelection.equals("ΕΞΟΔΑ")) {
                    spinnerLayout2.setVisibility(View.VISIBLE);
                } else {
                    spinnerLayout2.setVisibility(View.GONE);
                    sItems2.setText("", false); // clear the text when disabling
                }
            });

            List<String> spinArray2 = new ArrayList<>();
            List<Category> categoryNames = MainActivity.myDatabase.myDao().getCategory();
            for (Category category : categoryNames) {
                spinArray2.add(category.getCname());
            }



            ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinArray2);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sItems2 = findViewById(R.id.spinner2);
            sItems2.setAdapter(adapter2);


            ///////////////////////////////////////////////////////

            List<Transactions> transactionsList = MainActivity.myDatabase.myDao().getTransactions();
            if (!transactionsList.isEmpty()) {
                id = transactionsList.get(transactionsList.size() - 1).getId() + 1;
            } else {
                id = 1; // Start from 1 if the list is empty
            }





        ///////////////////////////////////////////////////////press ΑΠΟΘΗΚΕΥΣΗ
        bsave.setOnClickListener(v -> {
            //MainActivity.myDatabase.myDao().deleteAllFromTransactions();

            selectedCategory = sItems.getText().toString();
            selectedExpenseCategory = sItems2.getText().toString();
            TextInputLayout valueLayout=findViewById(R.id.textInputLayout2);
            TextInputEditText valueInput=findViewById(R.id.valueInput);





            String valueText = valueInput.getText().toString().trim();
            boolean isValid= true;

            if(TextUtils.isEmpty(valueText)||valueText.equals("0")){
                valueLayout.setError("Απαιτείται *");
                valueLayout.requestFocus();
                isValid=false;
            }else{
                valueLayout.setError(null);
                try {
                    value = Integer.parseInt(valueInput.getText().toString());
                } catch (NumberFormatException e) {
                    System.out.println("Could not parse " + e);
                }
            }
            if(selectedCategory.isEmpty()){
                spinnerLayout.setError("Απαιτείται *");
                if(isValid){
                    spinnerLayout.requestFocus();

                }
                isValid=false;
            }else {
                spinnerLayout.setError(null);
            }
            if(selectedCategory.equals("ΕΞΟΔΑ")){
                if(selectedExpenseCategory.isEmpty()){
                    spinnerLayout2.setError("Απαιτείται *");
                    if(isValid){
                        spinnerLayout2.requestFocus();

                    }
                    isValid=false;
                }else {
                    spinnerLayout2.setError(null);
                }
            }
            if(!isValid){
                return;
            }

            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();

            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = sdf.format(currentDate);

            Transactions transactions = new Transactions();
            transactions.setId(id);
            transactions.setType(selectedCategory);
            transactions.setValue(value);
            assert currentUser != null;
            transactions.setUserId(currentUser.getUid());
            transactions.setDate(formattedDate);
            int categoryId = MainActivity.myDatabase.myDao().getCatId(selectedExpenseCategory);
            transactions.setCatId(categoryId);
            id++;

                //Inform MainActivity the save is pressed to refresh the transaction fragment
            Intent intent = new Intent("com.example.ergasia.SAVE_BUTTON_CLICKED");
            LocalBroadcastManager.getInstance(PopUp.this).sendBroadcast(intent);

            MainActivity.myDatabase.myDao().addTransaction(transactions);

            MainActivity.db.collection("UserTotal").document("" + currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            Object fieldValue = document.get("total");
                            long totalLong = (long) fieldValue;
                            int total = (int) totalLong;
                            UserTotal userTotal=new UserTotal();
                            if(selectedCategory.equals("ΕΣΟΔΑ")){
                                newTotal=total+value;
                            }
                            else {
                                newTotal=total-value;
                            }
                            userTotal.setUid(currentUser.getUid());
                            userTotal.setTotal(newTotal);
                            MainActivity.db.collection("UserTotal").document(""+currentUser.getUid()).set(userTotal);
                        }
                    }
                }
            });
                // Finish activity
            finish();
                //notification creation
            createNotification();
        });
        }catch(Exception e) {
            Log.e("PopUpActivity", "Error in onCreate(): " + e.getMessage());
            Toast.makeText(this, "Error occurred in PopUp activity", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if an error occurs
        }
    }


    @SuppressLint("MissingPermission")
    private void createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "not1")
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setTicker("Money Manager")
                .setContentTitle("Νέα συναλλαγή")
                .setContentText(selectedCategory + ": " + value)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        createNotCha();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(100, builder.build());
    }

    private void createNotCha(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            String name=" noti";
            String des="notitest";
            int imp= NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel=new NotificationChannel("not1",name,imp);
            channel.setDescription(des);

            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
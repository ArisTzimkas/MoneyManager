package com.example.ergasia;

import android.annotation.SuppressLint;
import android.app.Activity;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.example.ergasia.database.Category;
import com.example.ergasia.database.Transactions;
import com.example.ergasia.database.UserTotal;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.util.List;
import java.util.Locale;


public class PopUp extends Activity {

    private static final int NOTIFICATION_ID = 1;

    String selectedCategory;
    String selectedExpenseCategory;

    public int id;

    FirebaseAuth mAuth;



    Spinner sItems2;

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
            getWindow().setLayout((int) (width * .8), (int) (height * .7));

            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.gravity = Gravity.CENTER;
            params.x = 0;
            params.y = 0;
            getWindow().setAttributes(params);
            ///////////////////////////////////////////End layout parameters


            Button bclose = (Button) findViewById(R.id.button_close);
            Button bsave = (Button) findViewById(R.id.button_save);

            ////////////////////////////////////////////////////////press ΑΚΥΡΟ
            bclose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });


            /////////////////////////////////////////////////////Fill spinner
            List<String> spinArray = new ArrayList<>();
            spinArray.add("ΕΣΟΔΑ");
            spinArray.add("ΕΞΟΔΑ");

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner sItems = (Spinner) findViewById(R.id.spinner);
            sItems.setAdapter(adapter);
            sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String selectedCategory = parentView.getItemAtPosition(position).toString();
                    if (selectedCategory.equals("ΕΞΟΔΑ")) {
                        sItems2.setEnabled(true);
                    } else {
                        sItems2.setEnabled(false);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Do nothing
                }
            });

            List<String> spinArray2 = new ArrayList<>();
            List<Category> categoryNames = MainActivity.myDatabase.myDao().getCategory();
            for (Category category : categoryNames) {
                spinArray2.add(category.getCname());
            }



            ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinArray2);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sItems2 = (Spinner) findViewById(R.id.spinner2);
            sItems2.setAdapter(adapter2);


            ///////////////////////////////////////////////////////

            List<Transactions> transactionsList = MainActivity.myDatabase.myDao().getTransactions();
            if (!transactionsList.isEmpty()) {
                id = transactionsList.get(transactionsList.size() - 1).getId() + 1;
            } else {
                id = 1; // Start from 1 if the list is empty
            }





        ///////////////////////////////////////////////////////press ΑΠΟΘΗΚΕΥΣΗ
        bsave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //MainActivity.myDatabase.myDao().deleteAllFromTransactions();

                selectedCategory = sItems.getSelectedItem().toString();
                selectedExpenseCategory = sItems2.getSelectedItem().toString();
                EditText valueInput=findViewById(R.id.valueInput);


                try {
                    value = Integer.parseInt(valueInput.getText().toString());
                } catch (NumberFormatException e) {
                    // Handle the case where the input cannot be converted to an integer
                    System.out.println("Could not parse " + e);
                }
                String valueText = valueInput.getText().toString().trim();
                if(!TextUtils.isEmpty(valueText)) {
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

                    Intent intent = new Intent("com.example.ergasia.SAVE_BUTTON_CLICKED");
                    sendBroadcast(intent);

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
                                        userTotal.setUid(currentUser.getUid());
                                        userTotal.setTotal(newTotal);
                                        MainActivity.db.collection("UserTotal").document(""+currentUser.getUid()).set(userTotal);
                                    }
                                    else {
                                        newTotal=total-value;
                                        userTotal.setUid(currentUser.getUid());
                                        userTotal.setTotal(newTotal);
                                        MainActivity.db.collection("UserTotal").document(""+currentUser.getUid()).set(userTotal);
                                    }


                                }
                            }
                        }
                    });
                    // Finish activity
                    finish();
                    //notification creation
                    createNotification();
                }
                else {
                    valueInput.setError("Συμπληρώστε");
                    valueInput.requestFocus();
                }










            }
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
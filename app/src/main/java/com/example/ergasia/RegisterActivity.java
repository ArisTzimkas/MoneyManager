package com.example.ergasia;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ergasia.database.UserTotal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    EditText textEmailRegister;
    EditText textPasswordRegister;
    EditText inputTotal;
    TextView gotoLogin;
    Button registerB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        textEmailRegister=findViewById(R.id.textEmailCreate);
        textPasswordRegister=findViewById(R.id.textPasswordCreate);
        inputTotal=findViewById(R.id.inputSetTotal);
        gotoLogin=findViewById(R.id.tvLoginHere);
        registerB=findViewById(R.id.CreateButton);

        mAuth=FirebaseAuth.getInstance();


        registerB.setOnClickListener(v -> createUser());

        gotoLogin.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this,LoginActivity.class)));


        getSupportActionBar().setTitle("Δημιουργία λογαριασμού");
    }

    private void createUser(){
        String email=textEmailRegister.getText().toString();
        String password=textPasswordRegister.getText().toString();
        String totalString = inputTotal.getText().toString();
        int total = Integer.parseInt(totalString);

        if(TextUtils.isEmpty(email)){
            textEmailRegister.setError("Το Email είναι απαραίτητο");
            textEmailRegister.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            textPasswordRegister.setError("Ο Κωδικός είναι απαραίτητος");
            textPasswordRegister.requestFocus();
        } else if(TextUtils.isEmpty(totalString)){
            inputTotal.setError("Το Αρχικό Κεφάλαιο είναι απαραίτητο");
            inputTotal.requestFocus();
        }
        else {
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Επιτυχής δημιουργία", Toast.LENGTH_LONG).show();

                    currentUser=mAuth.getCurrentUser();
                    String uid=currentUser.getUid();
                    UserTotal userTotal=new UserTotal();
                    userTotal.setUid(uid);
                    userTotal.setTotal(total);

                    MainActivity.db.collection("UserTotal").document(""+uid).set(userTotal);
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                }
                else{
                    Toast.makeText(RegisterActivity.this, "Η δημιουργία απέτυχε: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }
    }
}

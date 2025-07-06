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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    EditText textEmailLogin;
    EditText textPasswordLogin;

    TextView gotoRegister;

    Button loginB;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textEmailLogin=findViewById(R.id.textEmailLogin);
        textPasswordLogin=findViewById(R.id.textPasswordLogin);
        gotoRegister=findViewById(R.id.tvRegisterHere);
        loginB=findViewById(R.id.LoginButton);

        mAuth= FirebaseAuth.getInstance();

        loginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });





        getSupportActionBar().setTitle("Σύνδεση με λογαριασμό");


    }


    private void loginUser(){
        String email=textEmailLogin.getText().toString();
        String password=textPasswordLogin.getText().toString();

        if(TextUtils.isEmpty(email)){
            textEmailLogin.setError("Το Email είναι απαραίτητο");
            textEmailLogin.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            textPasswordLogin.setError("Ο Κωδικός είναι απαραίτητος");
            textPasswordLogin.requestFocus();
        }
        else {
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "Επιτυχής σύνδεση", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Η σύνδεση απέτυχε: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}

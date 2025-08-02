package com.example.ergasia;



import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    TextInputEditText textEmailLogin;
    TextInputEditText textPasswordLogin;
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

        loginB.setOnClickListener(v -> loginUser());

        gotoRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this,RegisterActivity.class)));






    }


    private void loginUser(){
        String email=textEmailLogin.getText().toString();
        String password=textPasswordLogin.getText().toString();
        TextInputLayout emailLayout=findViewById(R.id.textEmailLoginLayout);
        TextInputLayout passwordLayout=findViewById(R.id.textPasswordLoginLayout);
        boolean isValid=true;

        if(TextUtils.isEmpty(email)){
            emailLayout.setError("Απαιτείται *");
            textEmailLogin.requestFocus();
            isValid=false;
        }else{
            emailLayout.setError(null);
        }
        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Απαιτείται *");
            if(isValid){
                textPasswordLogin.requestFocus();
            }
            isValid=false;
        }else {
            passwordLayout.setError(null);
        }
        if(!isValid){
            return;
        }
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Επιτυχής σύνδεση", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                else{
                    Toast.makeText(LoginActivity.this, "Η σύνδεση απέτυχε: Το email ή το password είναι λάθος", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

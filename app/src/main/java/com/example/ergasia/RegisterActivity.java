package com.example.ergasia;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ergasia.database.UserTotal;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    TextInputEditText textEmailRegister;
    TextInputEditText textPasswordRegister;
    TextInputEditText inputTotal;
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


    }

    private void createUser(){
        String email=textEmailRegister.getText().toString();
        String password=textPasswordRegister.getText().toString();
        String totalString = inputTotal.getText().toString();

        TextInputLayout emailLayout=findViewById(R.id.textEmailCreateLayout);
        TextInputLayout passwordLayout =findViewById(R.id.textPasswordCreateLayout);
        TextInputLayout totalLayout=findViewById(R.id.inputSetTotalLayout);
        boolean isValid=true;

        if(TextUtils.isEmpty(email)) {
            emailLayout.setError("Απαιτείται *");
            if (isValid) {
                emailLayout.requestFocus();
            }
            isValid = false;
        } else {
            emailLayout.setError(null);
        }
        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Απαιτείται *");
            if(isValid) {
                passwordLayout.requestFocus();
            }
            isValid=false;
        } else {
            passwordLayout.setError(null);
        }
        if(TextUtils.isEmpty(totalString)){
            totalLayout.setError("Απαιτείται *");
            if(isValid) {
                inputTotal.requestFocus();
            }
            isValid=false;
        } else {
            totalLayout.setError(null);
        }
        if(!isValid){
            return;
        }


        int total = Integer.parseInt(totalString);
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

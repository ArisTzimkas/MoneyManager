package com.example.money_manager;



import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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

        if(!new NetworkUtils().isNetworkAvailable(this)){
            Toast.makeText(LoginActivity.this, "Δεν υπάρχει σύνδεση στο δίκτυο", Toast.LENGTH_LONG).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(LoginActivity.this, "Επιτυχής σύνδεση", Toast.LENGTH_LONG).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
            else{
                Toast.makeText(LoginActivity.this, "Η σύνδεση απέτυχε: Το email ή το password είναι λάθος", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static class NetworkUtils {
        public boolean isNetworkAvailable(Context context) {
            if (context == null) return false;

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) return false;

            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        }
    }
}

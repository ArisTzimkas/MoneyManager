package com.example.ergasia;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;


import com.example.ergasia.database.Category;
import com.example.ergasia.database.Migration1to2;
import com.example.ergasia.database.Migration2to3;
import com.example.ergasia.database.MyDatabase;

import com.google.android.material.navigation.NavigationView;


import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.ergasia.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private static final int REQUEST_CODE_POPUP = 1;

    public static com.example.ergasia.database.MyDatabase myDatabase;

    public  static FirebaseFirestore db;

    private FirebaseAuth mAuth;

    private boolean permissionRequested = false;
    private static final String PERMISSION_REQUESTED_KEY = "permission_requested";
    private boolean showOptionMenu=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate() called");
        permissionRequested = getPermissionRequestedState();

        // Check notification permissions if needed
        if (!permissionRequested) {
            requestNotificationPermissionIfNeeded();
        }
        invalidateOptionsMenu(); // This is KEY: tells Android to redraw the options menu
        try {
            myDatabase = Room.databaseBuilder(getApplicationContext(), MyDatabase.class, "UserDB")
                    .addMigrations(new Migration1to2(1, 2), new Migration2to3(2, 3))
                    .allowMainThreadQueries() // Allow database operations on the main thread
                    .build();

        } catch (Exception e) {
            Log.e("MainActivity", "Error creating database: " + e.getMessage());
        }


        //firebase
        db=FirebaseFirestore.getInstance();
        //authentication
        mAuth = FirebaseAuth.getInstance();


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //////////////////////////////////////////////////Pop Up
        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(view -> {
            Intent intent=new Intent(getApplicationContext(),PopUp.class);
            startActivity(intent);
        });
        ////////////////////////////////////////////////////

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        //Setup menu
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_transactions, R.id.nav_user, R.id.nav_schedule, R.id.nav_search,R.id.nav_about)
                .setOpenableLayout(drawer)
                .build();
        NavController globalNavController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, globalNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, globalNavController);

        //Check if current fragment is transaction
        globalNavController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.nav_transactions) {
                binding.appBarMain.fab.setVisibility(View.VISIBLE);
                showOptionMenu=true;
            } else {
                binding.appBarMain.fab.setVisibility(View.GONE);
                showOptionMenu=false;
            }
            invalidateOptionsMenu();
        });



        //Setup default categories
        if (myDatabase.myDao().getCategoryCount() == 0) {
            // default categories
            Category category1 = new Category();
            category1.setCid(1);
            category1.setCname("Ψώνια");
            myDatabase.myDao().addCategory(category1);

            Category category2 = new Category();
            category2.setCid(2);
            category2.setCname("Καύσιμα");
            myDatabase.myDao().addCategory(category2);

            Category category3 = new Category();
            category3.setCid(3);
            category3.setCname("Υγεία");
            myDatabase.myDao().addCategory(category3);
        }
    }

    private final BroadcastReceiver saveButtonReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Handle the save button click event
            if (Objects.equals(intent.getAction(), "com.example.ergasia.SAVE_BUTTON_CLICKED")) {

                NavController currentNavController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
                currentNavController.navigate(R.id.action_refresh);
            }
        }
    };

    //Display the toolbar 3-dot menu only in the transactions fragment
    public boolean onPrepareOptionsMenu(Menu menu) {
            if(showOptionMenu){
                return true;
            }else{
                menu.clear();
                return false;
            }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_add_category);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter("com.example.ergasia.SAVE_BUTTON_CLICKED");
        LocalBroadcastManager.getInstance(this).registerReceiver(saveButtonReceiver, filter);
        Log.d("MainActivity", "onStart() called");

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){

            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }


    private void requestNotificationPermissionIfNeeded() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null && !notificationManager.isNotificationPolicyAccessGranted()) {
            showNotificationPermissionDialog();
        }
    }

    private void showNotificationPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Άδεια Ειδοποιήσεων");
        builder.setMessage("Η εφαρμογή χρειάζεται πρόσβαση στις ειδοποιήσεις για να λειτουργεί σωστά");
        builder.setPositiveButton("Ρυθμίσεις", (dialog, which) -> {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);


            permissionRequested = true;
            savePermissionRequestedState();
        });

        builder.show();
    }


    private void savePermissionRequestedState() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PERMISSION_REQUESTED_KEY, true);
        editor.apply();
    }


    private boolean getPermissionRequestedState() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean(PERMISSION_REQUESTED_KEY, false);
    }


    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(saveButtonReceiver);
        Log.d("MainActivity", "onStop() called");
    }

    // Inflate the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}
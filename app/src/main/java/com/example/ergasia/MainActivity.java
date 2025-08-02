package com.example.ergasia;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;


import com.example.ergasia.database.Category;
import com.example.ergasia.database.Migration1to2;
import com.example.ergasia.database.Migration2to3;
import com.example.ergasia.database.MyDatabase;

import com.google.android.material.navigation.NavigationView;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
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

    public static com.example.ergasia.database.MyDatabase myDatabase;

    public  static FirebaseFirestore db;

    private boolean permissionRequested = false;
    private static final String CUSTOM_SETTINGS_DIALOG_SHOWN_KEY = "custom_settings_dialog_shown";
    private final ActivityResultLauncher<String> requestNotificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {});
    private boolean showOptionMenu=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ask for permission
        if (!permissionRequested) {
            checkAndRequestNotificationPermission();
        }
        invalidateOptionsMenu();
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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            Toast.makeText(this, "Πραγματοποιήστε σύνδεση", Toast.LENGTH_SHORT).show();
            // Clear the activity stack and finish MainActivity to prevent user from returning
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }


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

        globalNavController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            //Solution for a bug of pressing profileImage to goto user and from user could not navigate to home through menu
            navigationView.setNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                binding.drawerLayout.closeDrawers();
                if (itemId == R.id.nav_home) {
                    if (globalNavController.getCurrentDestination() != null && globalNavController.getCurrentDestination().getId() != R.id.nav_home) {
                        NavOptions navOptions = new NavOptions.Builder()
                                .setPopUpTo(R.id.nav_home, true) // Pop to Home
                                .build();
                        globalNavController.navigate(R.id.nav_home, null, navOptions);
                    }
                    return true;
                } else {
                    return NavigationUI.onNavDestinationSelected(item, globalNavController); // Let NavUI try other items
                }
            });


            //Disabling toolbar menu( 3dot menu) except from transaction fragment
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
    }


    private void checkAndRequestNotificationPermission() {
        permissionRequested = true;
        //Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
        //Android 12 and below
        else{
            showCustomNotificationSettingsDialog();
        }
    }


    private void showCustomNotificationSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Άδεια Ειδοποιήσεων")
                .setMessage("Η εφαρμογή χρειάζεται πρόσβαση στις ειδοποιήσεις για να λειτουργεί σωστά. Παρακαλώ ενεργοποιήστε τις από τις ρυθμίσεις της εφαρμογής.")
                .setPositiveButton("Ρυθμίσεις", (dialog, which) -> {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                    startActivity(intent);
                    saveCustomSettingsDialogShownState();
                })
                .setNegativeButton("Άκυρο", (dialog, which) -> {
                    Toast.makeText(this, "Οι ειδοποιήσεις παραμένουν απενεργοποιημένες.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }).setOnDismissListener(dialog -> {}).show();
    }

    private void saveCustomSettingsDialogShownState() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CUSTOM_SETTINGS_DIALOG_SHOWN_KEY, true);
        editor.apply();
    }


    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(saveButtonReceiver);
        Log.d("MainActivity", "onStop() called");
    }

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
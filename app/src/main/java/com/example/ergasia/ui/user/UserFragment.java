package com.example.ergasia.ui.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.example.ergasia.LoginActivity;
import com.example.ergasia.MainActivity;
import com.example.ergasia.R;
import com.example.ergasia.database.UserGoal;
import com.example.ergasia.databinding.FragmentUserBinding;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;


public class UserFragment extends Fragment {

    private FragmentUserBinding binding;
    Button logout;
    Button goal;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    TextView email;
    TextView id;
    TextView goalText;
    ImageButton editPhotoButton;
    ImageView profileImage;

    private static final String PROFILE_IMAGE_FILENAME_PREFIX = "profile_";
    private static final String PROFILE_IMAGE_FILENAME_SUFFIX = ".jpg";
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private File currentUserProfileImageFile;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //for userImage
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {

                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        saveImageForCurrentUser(uri,currentUser.getUid());
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();

        editPhotoButton = root.findViewById(R.id.editPhotoButton);
        profileImage = root.findViewById(R.id.profileImage);


        editPhotoButton.setOnClickListener(view -> pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build()));


        email=root.findViewById(R.id.emailUser);
        id=root.findViewById(R.id.textid);
        logout=root.findViewById(R.id.connectbutton);
        goal=root.findViewById(R.id.buttonGoal);
        goalText=root.findViewById(R.id.goalText);

        if (currentUser != null) {
            // Set the email to the TextView
            email.setText(currentUser.getEmail());
            id.setText(currentUser.getUid());
        }


        logout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(requireActivity(), LoginActivity.class));
        });


        goal.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_to_goal));

        assert currentUser != null;
        MainActivity.db.collection("UserGoal").document(""+currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        UserGoal userGoal = document.toObject(UserGoal.class);
                        if (userGoal != null) {
                            int goal = userGoal.getGoal();
                            goalText.setText(String.valueOf(goal));
                        }
                    }
                }
            }
        });
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        updateProfileImageViewState();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateProfileImageViewState() {
        if (getContext() == null) return;
        String userSpecificFilename = PROFILE_IMAGE_FILENAME_PREFIX + currentUser.getUid() + PROFILE_IMAGE_FILENAME_SUFFIX;
        currentUserProfileImageFile = new File(getContext().getFilesDir(), userSpecificFilename);
        displayImage();
    }

    private void displayImage() {
        if (getContext() == null) return;

        if (currentUserProfileImageFile != null && currentUserProfileImageFile.exists() && currentUserProfileImageFile.length() > 0) {
            Glide.with(this)
                    .load(currentUserProfileImageFile)
                    .signature(new ObjectKey(String.valueOf(currentUserProfileImageFile.lastModified())))
                    .placeholder(R.drawable.person)
                    .error(R.drawable.person)
                    .circleCrop()
                    .into(binding.profileImage);
        } else {
            Glide.with(this)
                    .load(R.drawable.person) // Default placeholder
                    .circleCrop()
                    .into(binding.profileImage); // Use binding.profileImage
        }
    }

    private void saveImageForCurrentUser(Uri sourceUri, String userId) {
        if (getContext() == null || sourceUri == null || userId == null || userId.isEmpty()) {
            Toast.makeText(getContext(), "Error saving image. Missing data.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userSpecificFilename = PROFILE_IMAGE_FILENAME_PREFIX + userId + PROFILE_IMAGE_FILENAME_SUFFIX;
        File destinationFile = new File(getContext().getFilesDir(), userSpecificFilename);

        try (InputStream inputStream = getContext().getContentResolver().openInputStream(sourceUri);
             OutputStream outputStream = Files.newOutputStream(destinationFile.toPath())) {
            byte[] buffer = new byte[1024 * 4];
            int bytesRead;
            while (true) {
                assert inputStream != null;
                if ((bytesRead = inputStream.read(buffer)) == -1) break;
                outputStream.write(buffer, 0, bytesRead);
            }
            currentUserProfileImageFile = destinationFile;
            displayImage();
            Toast.makeText(getContext(), "Επιτυχής Ενημέρωση", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(getContext(), "Αποτυχία :" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
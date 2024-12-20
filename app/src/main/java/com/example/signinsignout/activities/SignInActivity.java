package com.example.signinsignout.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.signinsignout.databinding.ActivitySignInBinding;
import com.example.signinsignout.utilities.Constants;
import com.example.signinsignout.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {

    private @NonNull ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());

        setListeners();

    }

    /**
     * Listeners for the create new account and sign in buttons. Moves to different activities
     */
    private void setListeners() {

        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class)));

        binding.buttonSignIn.setOnClickListener(v ->{
                if(isValidateSignUpDetails()) {
                    SignIn();
                }
        });
    }

    /**
     * Signing user in and moving them to the main activity class, making sure sign in credentials
     * are correct within firebase
     */
    private void SignIn() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL,binding.inputEmail.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult()!=null && task.getResult().getDocuments().size()>0){
                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                    preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                    preferenceManager.putString(Constants.KEY_NAME,documentSnapshot.getString(Constants.KEY_NAME));
                    preferenceManager.putString(Constants.KEY_IMAGE,documentSnapshot.getString(Constants.KEY_IMAGE));

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    loading(false);
                    showToast("Unable to sign in");
                }

        });
    }

    /**
     * Method to show toast
     * @param message
     */
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Changing visibility for loading bar when clicking sign in button
     * @param isLoading
     */
    private void loading(Boolean isLoading){
        if(isLoading){
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignIn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Validating the sign in edit texts, if any are invalid, return false.
     * Otherwise, return true
     * @return
     */
    private boolean isValidateSignUpDetails() {
        if (binding.inputEmail.getText().toString().trim().isEmpty()){
            showToast("Please Enter your Email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
            showToast("Please Enter Valid Email");
            return false;
        } else if (binding.inputPassword.getText().toString().trim().isEmpty()){
            showToast("Please Enter your Password");
            return false;
        } else {
            return true;
        }

}   }
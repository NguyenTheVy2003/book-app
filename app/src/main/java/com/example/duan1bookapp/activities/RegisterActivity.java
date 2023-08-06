package com.example.duan1bookapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.duan1bookapp.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private String name = "", email = "", password = "" , cPassword = "";
    private ActivityRegisterBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // handle click, begin register
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            boolean isAllFieldsChecked = false;
            @Override
            public void onClick(View v) {

                // store the returned value of the dedicated function which checks
                // whether the entered data is valid or if any fields are left blank.
                isAllFieldsChecked = CheckAllFields();
                // the boolean variable turns to be true then
                // only the user must be proceed to the activity2
                if (isAllFieldsChecked) {
                    createUserAccount();
                }
            }
        });
    }

    private boolean CheckAllFields() {

        name = binding.nameTil.getEditText().getText().toString();
        email = binding.emailTil.getEditText().getText().toString().trim();
        password = binding.passwordTil.getEditText().getText().toString().trim();
        cPassword = binding.cPasswordTil.getEditText().getText().toString().trim();

        if (!validateName() | !validateEmail() | !validatePassWord() | !validateRepeatPassword() ){
            return false;
        }
         else if (!password.equals(cPassword)){
            binding.cPasswordTil.setError("password incorrect, please try again ");
            return false;
        }
        // after all validation return true.
        else {
            return true;
        }
    }
    private boolean validateEmail(){
        String emailInput = binding.emailTil.getEditText().getText().toString().trim();

        if (emailInput.isEmpty()){
            binding.emailTil.setError("Email cannot be blank");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches())
        {
            binding.emailTil.setError("Please enter a valid email address");
            return false;
        }
        else {
            binding.emailTil.setError(null);
            return true;
        }
    }

    private boolean validateRepeatPassword(){
        String repeatPassword = binding.cPasswordTil.getEditText().getText().toString().trim();;
        if (repeatPassword.isEmpty()){
            binding.cPasswordTil.setError("Repeat password cannot be blank");
            return false;
        }
        else {
            binding.cPasswordTil.setError(null);
            return true;
        }
    }
    private boolean validatePassWord(){
         String passwordInput = binding.passwordTil.getEditText().getText().toString().trim();
        if (passwordInput.isEmpty()){
            binding.passwordTil.setError("Password cannot be blank");
            return false;
        } else if (passwordInput.length() <6) {
            binding.passwordTil.setError("Password must have at least 6 characters");
            return false;
        } else {
            binding.passwordTil.setError(null);
            return true;
        }
    }

    private boolean validateName(){
        String usernameInput = binding.nameTil.getEditText().getText().toString();
        if (usernameInput.isEmpty()){
            binding.nameTil.setError("Name cannot be blank");
            return false;
        }
        else if (usernameInput.length() > 20) {
            binding.nameTil.setError("Username too long ");
            return false;
        }
        else {
            binding.nameTil.setError(null);
            return true;
        }
    }

    private void createUserAccount() {
        // show progress
        progressDialog.setMessage("Creating account...");
        progressDialog.show();

        //create user in firebase auth
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // account creation success, now add in firebase realtime database
                        updateUserInfo();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // account creating failed
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void updateUserInfo() {
        progressDialog.setMessage("Saving user info...");

        //timestamp
        long timestamp = System.currentTimeMillis();

        //get current user uid, since user is registered so we can ger now
        String uid = firebaseAuth.getUid();

        // setup data to add in db
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("email", email);
        hashMap.put("name", name);
        hashMap.put("profileImage", ""); // add empty, will do later
        hashMap.put("userType", "user"); // possible values are user, admin: will make admin manually realtime database by changing this value
        hashMap.put("timestamp", timestamp);

        // set data to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // data add to db
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Account created...", Toast.LENGTH_SHORT).show();
                        // since user account is created so start dashboard user
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // data failed adding to db
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}

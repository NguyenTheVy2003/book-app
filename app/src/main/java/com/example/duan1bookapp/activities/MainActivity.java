package com.example.duan1bookapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.duan1bookapp.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // handle loginBtn click, start login screen
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
        // handle skipBtn click, start continue without login screen
        binding.skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DashboardUserActivity.class));

            }
        });
    }

//    private void checkUser() {
//        progressDialog.setMessage("Checking User...");
//
//        // check if user is user or admin form realtime database
//        // get current user
//        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//
//        //check in db
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        ref.child(firebaseUser.getUid())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot snapshot) {
//                        progressDialog.dismiss();
//                        // get user type
//                        String userType = "" + snapshot.child("userType").getValue();
//                        // check user type
//                        if (userType.equals("user")) {
//                            // this is simple user, open user dashboard
//                            startActivity(new Intent(MainActivity.this, DashboardUserActivity.class));
//                            finish();
//                        } else if (userType.equals("admin")) {
//                            // this is admin, open admin dashboard
//                            startActivity((new Intent(MainActivity.this, DashboardAdminActivity.class)));
//                            finish();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError error) {
//
//                    }
//                });
//    }

}
package com.example.duan1bookapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.duan1bookapp.R;
import com.example.duan1bookapp.activities.DashboardAdminActivity;
import com.example.duan1bookapp.activities.MainActivity;
import com.example.duan1bookapp.adapters.AdapterCategory;
import com.example.duan1bookapp.databinding.FragmentBooksUserBinding;
import com.example.duan1bookapp.databinding.FragmentMangaBinding;
import com.example.duan1bookapp.models.ModelCategory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Fragment_Manga extends Fragment {
    private FragmentMangaBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //arraylist to story category
    private ArrayList<ModelCategory> categoryArrayList;
    private AdapterCategory adapterCategory;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMangaBinding.inflate(LayoutInflater.from(getContext()),container,false);

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        loadCategories();


        //edit text change listen, search
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                //called as and when user type each letter
                try {
                    adapterCategory.getFilter().filter(s);
                } catch (Exception e) {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return binding.getRoot();
    }
    private void loadCategories() {

        //init  arraylist
        categoryArrayList = new ArrayList<>();

        //get all categories from firebase > Categories
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear arraylist before adding data into it
                categoryArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get data
                    ModelCategory mode = ds.getValue(ModelCategory.class);
                    // add to arraylist
                    categoryArrayList.add(mode);
                }
                //setup adapter
                adapterCategory = new AdapterCategory(getContext(), categoryArrayList);
                //set adapter to recyclerview
                binding.categoriesRv.setAdapter(adapterCategory);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void checkUser() {
        //get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            //not logged in, go to main screen
            startActivity(new Intent(getContext(), MainActivity.class));
            return;
        } else {

        }

    }
}

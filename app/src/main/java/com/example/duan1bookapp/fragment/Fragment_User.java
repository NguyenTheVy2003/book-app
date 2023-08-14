package com.example.duan1bookapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.duan1bookapp.MyApplication;
import com.example.duan1bookapp.R;
import com.example.duan1bookapp.activities.MainActivity;
import com.example.duan1bookapp.activities.ProfileActivity;
import com.example.duan1bookapp.activities.ProfileEditActivity;
import com.example.duan1bookapp.adapters.AdapterPdfFavorite;
import com.example.duan1bookapp.databinding.FragmentUserBinding;
import com.example.duan1bookapp.models.ModelPdf;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Fragment_User extends Fragment {
    //view binding
    private FragmentUserBinding binding;
    //firebase auth,for leading user data using user uid
    private FirebaseAuth firebaseAuth;

    //arrayList to hold the books
    private ArrayList<ModelPdf> pdfArrayList;
    //adapter to set in recyclerView
    private AdapterPdfFavorite adapterPdfFavorite;

    private static final String TAG = "PROFILE_TAG";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserBinding.inflate(LayoutInflater.from(getContext()), container, false);
        //setup firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        loadUserInfo();

        //need to do in profile not pdf details

        loadFavoriteBooks();

        //handle click,start profile edit page
        binding.profileEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ProfileEditActivity.class));
            }
        });

//         handle click, logout
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();

                startActivity(new Intent(getContext(), MainActivity.class));
                return;

            }
        });
        return binding.getRoot();
    }

    private void loadUserInfo() {
        Log.d(TAG, "loadUserInfo: Loading user info of user" + firebaseAuth.getUid());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (getActivity() == null) {
                            return;
                        } else {
                            //get all info of user here from snapshot
                            String email = "" + snapshot.child("email").getValue();
                            String name = "" + snapshot.child("name").getValue();
                            String profileImage = "" + snapshot.child("profileImage").getValue();
                            String timestamp = "" + snapshot.child("timestamp").getValue();
                            String uid = "" + snapshot.child("uid").getValue();
                            String userType = "" + snapshot.child("userType").getValue();

                            //format data to dd/MM/yyyy
                            String formattedDate = MyApplication.formatTimestamp(Long.parseLong(timestamp));

                            //set data to ui
                            binding.emailTv.setText(email);
                            binding.nameTv.setText(name);
                            binding.memberDateTv.setText(formattedDate);
                            binding.accountTypeTv.setText(userType);


                            //setImage,using glide
                            Glide.with(getActivity())
                                    .load(profileImage)
                                    .placeholder(R.drawable.ic_person_gray)
                                    .into(binding.profileTv);

                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadFavoriteBooks() {
        //init list
        pdfArrayList = new ArrayList<>();

        //load favorite books from database
        //Users > userId  > Favorites
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Favorites")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear list before starting adding data
                        pdfArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            //we will only get the bookId here and we got other details in adapter using that bookId
                            if(ds.child("bookId").exists()){
                                //làm khi tônf tại sách
                                binding.tv.setVisibility(View.GONE);
                                String bookId = "" + ds.child("bookId").getValue();
                                //set id to model
                                ModelPdf modelPdf = new ModelPdf();
                                modelPdf.setId(bookId);
                                //add model to list
                                pdfArrayList.add(modelPdf);
                            }else {
                                binding.tv.setVisibility(View.VISIBLE);
                            }

                        }
                        //set number of favorite books
                        binding.favoriteBookCountTv.setText("" + pdfArrayList.size());//can't set int/long to textView so concatnate with String
                        //setup adapter
                        adapterPdfFavorite = new AdapterPdfFavorite(getContext(), pdfArrayList);
                        //set Adapter to recyclerView
                        binding.booksRv.setAdapter(adapterPdfFavorite);
                        adapterPdfFavorite.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}

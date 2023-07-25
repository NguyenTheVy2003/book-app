package com.example.duan1bookapp.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.duan1bookapp.BooksUserFragment;
import com.example.duan1bookapp.R;
import com.example.duan1bookapp.activities.DashboardUserActivity;
import com.example.duan1bookapp.adapters.AdapterPdfUser;
import com.example.duan1bookapp.databinding.ActivityDashboardUserBinding;
import com.example.duan1bookapp.databinding.FragmentBooksUserBinding;
import com.example.duan1bookapp.databinding.FragmentHomeBinding;
import com.example.duan1bookapp.models.ModelCategory;
import com.example.duan1bookapp.models.ModelPdf;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Fragment_Home extends Fragment {
    // view biding
    private FragmentHomeBinding binding;

    // that we passed while creating instance of this fragment
    private String categoryId;
    private String category;
    private String uid;

    private ArrayList<ModelPdf> pdfArrayList;
    private AdapterPdfUser adapterPdfUser;

    private static final String TAG = "BOOKS_USER_TAG";

    public Fragment_Home() {
        // Required empty public constructor
    }


    public static BooksUserFragment newInstance(String categoryId, String category, String uid ) {
        BooksUserFragment fragment = new BooksUserFragment();
        Bundle args = new Bundle();
        args.putString("categoryId", categoryId);
        args.putString("category", category);
        args.putString("uid", uid);


        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getString("categoryId");
            category = getArguments().getString("category");
            uid = getArguments().getString("uid");

        }
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentHomeBinding.inflate(LayoutInflater.from(getContext()),container,false);

        Log.d(TAG, "onCreateView: Category: " + category);
        if ("All".equals(category)){
            // load all books
            loadAllBooks();
        }
        else if ("Most Viewed".equals(category)){
            // load viewed books
            loadMostViewedDownloadedBooks("viewsCount");
        }
        else if ("Most Downloaded".equals(category)){
            // load most downloaded books
            loadMostViewedDownloadedBooks("downloadsCount");
        }
        else {
            // load selected category books
            loadCategorizedBooks();
        }

        // search
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // called as and when user type any letter
                try {
                    adapterPdfUser.getFilter().filter(s);
                }
                catch (Exception e){
                    Log.d(TAG, "onTextChanged: " + e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return binding.getRoot();


    }
    private void loadAllBooks() {
        // inti list
        pdfArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pdfArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    // get data
                    ModelPdf model = ds.getValue(ModelPdf.class);
                    // add to list
                    pdfArrayList.add(model);
                }
                // setup adapter
                adapterPdfUser = new AdapterPdfUser(getContext(), pdfArrayList);
                // set adapter to recyclerview
                binding.booksRv.setAdapter(adapterPdfUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadMostViewedDownloadedBooks(String oderBy) {
        // inti list
        pdfArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild(oderBy).limitToLast(10) // load 10 most viewed or downloaded books
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            // get data
                            ModelPdf model = ds.getValue(ModelPdf.class);
                            // add to list
                            pdfArrayList.add(model);
                        }
                        // setup adapter
                        adapterPdfUser = new AdapterPdfUser(getContext(), pdfArrayList);
                        // set adapter to recyclerview
                        binding.booksRv.setAdapter(adapterPdfUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadCategorizedBooks() {
        // inti list
        pdfArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            // get data
                            ModelPdf model = ds.getValue(ModelPdf.class);
                            // add to list
                            pdfArrayList.add(model);
                        }
                        // setup adapter
                        adapterPdfUser = new AdapterPdfUser(getContext(), pdfArrayList);
                        // set adapter to recyclerview
                        binding.booksRv.setAdapter(adapterPdfUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}

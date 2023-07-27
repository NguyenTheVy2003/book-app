package com.example.duan1bookapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1bookapp.activities.AllBooksActivity;
import com.example.duan1bookapp.adapters.AdapterPdfUser;
import com.example.duan1bookapp.databinding.FragmentBooksUser2Binding;
import com.example.duan1bookapp.databinding.FragmentBooksUserBinding;
import com.example.duan1bookapp.models.ModelPdf;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BooksUserFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BooksUserFragment2 extends Fragment {

   // that we passed while creating instance of this fragment
   private String categoryId;
   private String category;
   private String uid;

   private ArrayList<ModelPdf> pdfArrayList;
   private AdapterPdfUser adapterPdfUser;

   // view biding
    private FragmentBooksUser2Binding biding;

    private static final String TAG = "BOOKS_USER_TAG";


    public BooksUserFragment2() {
        // Required empty public constructor
    }


    public static BooksUserFragment2 newInstance(String categoryId, String category, String uid ) {
        BooksUserFragment2 fragment = new BooksUserFragment2();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        biding = FragmentBooksUser2Binding.inflate(LayoutInflater.from(getContext()),container,false);

        Log.d(TAG, "onCreateView: Category: " + category);
        if (category.equals("All")){
            // load all books
            loadAllBooks();
        }
        else if (category.equals("Most Viewed")){
            // load viewed books
            loadMostViewedDownloadedBooks("viewsCount");
        }
        else if (category.equals("Most Downloaded")){
            // load most downloaded books
            loadMostViewedDownloadedBooks("downloadsCount");
        }
        else {
            // load selected category books
            loadCategorizedBooks();
        }





        return biding.getRoot();
    }




    private void loadAllBooks() {
        // inti list
        pdfArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear list before starting adding data into it
                pdfArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    // get data
                    ModelPdf model = ds.getValue(ModelPdf.class);
                    // add to list
                    pdfArrayList.add(model);
                }
                //set layout recycler
                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
                linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                biding.booksRv.setLayoutManager(linearLayoutManager);
                // setup adapter
                adapterPdfUser = new AdapterPdfUser(getContext(), pdfArrayList);
                // set adapter to recyclerview
                biding.booksRv.setAdapter(adapterPdfUser);
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
                //set layout recycler
                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
                linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                biding.booksRv.setLayoutManager(linearLayoutManager);
                // setup adapter
                adapterPdfUser = new AdapterPdfUser(getContext(), pdfArrayList);
                // set adapter to recyclerview
                biding.booksRv.setAdapter(adapterPdfUser);
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
                        //set layout recycler
                        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
                        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                        biding.booksRv.setLayoutManager(linearLayoutManager);

                        // setup adapter
                        adapterPdfUser = new AdapterPdfUser(getContext(), pdfArrayList);
                        // set adapter to recyclerview
                        biding.booksRv.setAdapter(adapterPdfUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}
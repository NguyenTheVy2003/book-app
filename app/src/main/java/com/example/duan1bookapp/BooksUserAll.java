package com.example.duan1bookapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.duan1bookapp.adapters.AdapterPdfUser;
import com.example.duan1bookapp.adapters.AdapterPdfUserBooksAll;
import com.example.duan1bookapp.adapters.AdapterPdfUserBooksAll2;
import com.example.duan1bookapp.databinding.FragmentBooksUserAllBinding;
import com.example.duan1bookapp.models.ModelPdf;
import com.example.duan1bookapp.models.ModelPdfBooksAll;
import com.example.duan1bookapp.models.ModelPdfBooksAll2;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BooksUserAll#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BooksUserAll extends Fragment {

   // that we passed while creating instance of this fragment
   private String categoryId;
   private String category;
   private String uid;

   private ArrayList<ModelPdfBooksAll> pdfArrayList;
   private AdapterPdfUserBooksAll adapterPdfUserBooksAll;
//   // view biding
    private FragmentBooksUserAllBinding biding;
    private static final String TAG = "BOOKS_USER_TAG";


    public BooksUserAll() {
        // Required empty public constructor
    }


    public static BooksUserAll newInstance(String categoryId, String category, String uid ) {
        BooksUserAll fragment = new BooksUserAll();
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
        biding = FragmentBooksUserAllBinding.inflate(LayoutInflater.from(getContext()),container,false);

        Log.d(TAG, "onCreateView: Category: " + category);
        if (category.equals("All")){
            // load all books
            loadAllBooks();
        }else {
            // load selected category books
            loadCategorizedBooks();
        }


//         search
        biding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // called as and when user type any letter
                try {
                    adapterPdfUserBooksAll.getFilter().filter(s);

                }
                catch (Exception e){
                    Log.d(TAG, "onTextChanged: " + e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


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
                    ModelPdfBooksAll model = ds.getValue(ModelPdfBooksAll.class);
                    // add to list
                    pdfArrayList.add(model);
                }
                //set layout recycler
                GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(),2);
                biding.booksRv.setLayoutManager(gridLayoutManager);
                // setup adapter
                adapterPdfUserBooksAll= new AdapterPdfUserBooksAll(getContext(), pdfArrayList);
                // set adapter to recyclerview
                biding.booksRv.setAdapter(adapterPdfUserBooksAll);
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
                            ModelPdfBooksAll model = ds.getValue(ModelPdfBooksAll.class);
                            // add to list
                            pdfArrayList.add(model);
                        }
                        //set layout recycler
                        //set layout recycler
                        GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(),2);
                        biding.booksRv.setLayoutManager(gridLayoutManager);

                        // setup adapter
                        adapterPdfUserBooksAll = new AdapterPdfUserBooksAll(getContext(), pdfArrayList);
                        // set adapter to recyclerview
                        biding.booksRv.setAdapter(adapterPdfUserBooksAll);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}
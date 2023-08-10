package com.example.duan1bookapp.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.duan1bookapp.BooksUserFragment2;
import com.example.duan1bookapp.activities.BooksAllActivity;
import com.example.duan1bookapp.activities.TrendingBooksAll;
import com.example.duan1bookapp.adapters.AdapterPdfTrendingBooks;
import com.example.duan1bookapp.adapters.AdapterPdfViewsHistoryBooks;
import com.example.duan1bookapp.adapters.SliderAdapterExample;
import com.example.duan1bookapp.databinding.FragmentHomeBinding;
import com.example.duan1bookapp.databinding.FragmentReadingbookBinding;
import com.example.duan1bookapp.models.ModelCategory;
import com.example.duan1bookapp.models.ModelPdfTrendingBooks;
import com.example.duan1bookapp.models.ModelPdfViewsHistoryBooks;
import com.example.duan1bookapp.models.ModelSlideShow;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class Fragment_ReadingBook extends Fragment {
    // view biding
    private FragmentReadingbookBinding binding;

    //reading books
    private ArrayList<ModelPdfViewsHistoryBooks> pdfViewsHistoryBooksList;
    AdapterPdfViewsHistoryBooks adapterPdfViewsHistoryBooks;

    //firebase auth,for leading user data using user uid
    private FirebaseAuth firebaseAuth;


    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReadingbookBinding.inflate(LayoutInflater.from(getContext()), container, false);
        //setup firebase auth
        firebaseAuth =FirebaseAuth.getInstance();

        loadReadingBooks();
        //search user
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // called as and when user type any letter
                try {
                    adapterPdfViewsHistoryBooks.getFilter().filter(s);
                }
                catch (Exception e){
                    Log.d("TAG", "onTextChanged: " + e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });



        return binding.getRoot();
    }
    //    view reading(xong)
    private void loadReadingBooks(){
        pdfViewsHistoryBooksList=new ArrayList<>();

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("ReadingBooks")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfViewsHistoryBooksList.clear();
                        for (DataSnapshot ds:snapshot.getChildren()){
                            //we will only get the bookId here and we got other details in adapter using that bookId
                            String bookId=""+ds.child("bookId").getValue();
                            //set id to model
                            ModelPdfViewsHistoryBooks modelPdf=new ModelPdfViewsHistoryBooks();
                            modelPdf.setId(bookId);
                            //add model to list
                            pdfViewsHistoryBooksList.add(modelPdf);
                        }
                        //set LinearLayout Manager
                        GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(),2);
                        binding.booksRv.setLayoutManager(gridLayoutManager);
                        //setup adapter
                        adapterPdfViewsHistoryBooks=new AdapterPdfViewsHistoryBooks(getContext(),pdfViewsHistoryBooksList);
                        //set Adapter to recyclerView
                        binding.booksRv.setAdapter(adapterPdfViewsHistoryBooks);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }


}


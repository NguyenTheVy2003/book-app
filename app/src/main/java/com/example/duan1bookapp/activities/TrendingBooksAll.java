package com.example.duan1bookapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.duan1bookapp.R;
import com.example.duan1bookapp.adapters.AdapterPdfTrendingBooks;
import com.example.duan1bookapp.adapters.AdapterPdfTrendingBooksAll;
import com.example.duan1bookapp.databinding.ActivityTrendingBooksAllBinding;
import com.example.duan1bookapp.models.ModelPdfTrendingBooks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TrendingBooksAll extends AppCompatActivity {
    ActivityTrendingBooksAllBinding binding;

    //Trending Books
    private ArrayList<ModelPdfTrendingBooks> pdfTrendingBooksList;
    AdapterPdfTrendingBooksAll adapterPdfTrendingBooksAll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityTrendingBooksAllBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadTrendingBooks();

        binding.imvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //search
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // called as and when user type any letter
                try {
                    adapterPdfTrendingBooksAll.getFilter().filter(s);
                }
                catch (Exception e){
                    Log.d("TAG", "onTextChanged: " + e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
    //trending books(xong)
    private void loadTrendingBooks() {
        //init list
        pdfTrendingBooksList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear list before starting adding data into it
                        pdfTrendingBooksList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            //get data
                            ModelPdfTrendingBooks model = ds.getValue(ModelPdfTrendingBooks.class);
                            int viewCount = (int) model.getViewsCount();
                            if(viewCount >= 5){
                                //add to list
                                pdfTrendingBooksList.add(model);
                            }

                        }
                        GridLayoutManager gridLayoutManager=new GridLayoutManager(TrendingBooksAll.this,2);
                        binding.booksRv.setLayoutManager(gridLayoutManager);
                        //setup adapter
                        adapterPdfTrendingBooksAll = new AdapterPdfTrendingBooksAll(TrendingBooksAll.this, pdfTrendingBooksList);
                        //set adapter to recyclerview
                        binding.booksRv.setAdapter(adapterPdfTrendingBooksAll);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
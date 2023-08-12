package com.example.duan1bookapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.duan1bookapp.R;
import com.example.duan1bookapp.adapters.AdapterPdfTrendingBooks;
import com.example.duan1bookapp.adapters.AdapterPdfTrendingBooksAll;
import com.example.duan1bookapp.databinding.ActivityTrendingBooksAllBinding;
import com.example.duan1bookapp.models.ModelPdfTrendingBooks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
        clickUnble();



    }
    private void clickUnble(){
        binding.booksRv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (binding.searchEt.isFocused()) {
                        Rect outRect = new Rect();
                        binding.searchEt.getGlobalVisibleRect(outRect);
                        if (!outRect.contains((int)motionEvent.getRawX(), (int)motionEvent.getRawY())) {
                            binding.searchEt.clearFocus();
                            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                }
                return false;
            }
        });
        binding.ln1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (binding.searchEt.isFocused()) {
                        Rect outRect = new Rect();
                        binding.searchEt.getGlobalVisibleRect(outRect);
                        if (!outRect.contains((int)motionEvent.getRawX(), (int)motionEvent.getRawY())) {
                            binding.searchEt.clearFocus();
                            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                }
                return false;
            }
        });

    }
    //trending books(xong)
    private void loadTrendingBooks() {
        //init list
        pdfTrendingBooksList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        Query query=ref.orderByChild("viewsCount").startAt(10);
        query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear list before starting adding data into it
                        pdfTrendingBooksList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            //get data
                            ModelPdfTrendingBooks model = ds.getValue(ModelPdfTrendingBooks.class);
                            pdfTrendingBooksList.add(model);
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
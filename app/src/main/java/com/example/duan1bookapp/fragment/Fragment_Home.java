package com.example.duan1bookapp.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.duan1bookapp.BooksUserFragment2;
import com.example.duan1bookapp.R;
import com.example.duan1bookapp.activities.AllBooksActivity;

import com.example.duan1bookapp.activities.AllViewHistory;
import com.example.duan1bookapp.adapters.AdapterPdfFavorite;
import com.example.duan1bookapp.adapters.AdapterPdfReadingBooks;
import com.example.duan1bookapp.adapters.AdapterPdfUser;
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

public class Fragment_Home extends Fragment{
    // view biding
    private FragmentHomeBinding binding;

    private static final String TAG = "BOOKS_USER_TAG";

    //to show in tabs
    public ArrayList<ModelCategory> categoryArrayList;
    public ViewPagerAdapter viewPagerAdapter;
    AdapterPdfUser adapterPdfUser;



    //firebase auth,for leading user data using user uid
    private FirebaseAuth firebaseAuth;
    //reading books
    //arrayList to hold the books
    private ArrayList<ModelPdf> pdfArrayList;
    //adapter to set in recyclerView
    private AdapterPdfReadingBooks adapterPdfReadingBooks;
    private Boolean isCheck;







    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentHomeBinding.inflate(LayoutInflater.from(getContext()),container,false);

        //setup firebase auth
        firebaseAuth =FirebaseAuth.getInstance();
        //load Reading Books
        loadReadingBooks();
        //load View History
        loadTrendingBooks();
        //Load Slide Show
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        loadSlideShow();


        setupViewPagerAdapter(binding.viewpager);
        binding.tabLayout.setupWithViewPager(binding.viewpager);




        //search user
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
        //handle click all Books
        binding.tvAllBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AllBooksActivity.class));
            }
        });
        binding.tvAllViewedHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AllViewHistory.class));
            }
        });
        return binding.getRoot();
    }
        private void setupViewPagerAdapter(ViewPager viewPager){
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, getContext());
        categoryArrayList = new ArrayList<>();

        // load categories from firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories"); // be careful of spelling
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                // clear before adding to list
                categoryArrayList.clear();


                // Now Load from firebase
                for (DataSnapshot ds: snapshot.getChildren()) {
                    // get data
                    ModelCategory model = ds.getValue(ModelCategory.class);
                    // add data to list
                    categoryArrayList.add(model);
                    // add data to viewPagerAdapter
                    viewPagerAdapter.addFragment(BooksUserFragment2.newInstance(
                            "" + model.getId(),
                            "" + model.getCategory(),
                            "" + model.getUid()), model.getCategory());
                    // refresh list
                    viewPagerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        // set adapter to view pager
        viewPager.setAdapter(viewPagerAdapter);
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter{

        private ArrayList<BooksUserFragment2> fragmentList = new ArrayList<>();
        private ArrayList<String> fragmentTitleList = new ArrayList<>();
        private Context context;

        public ViewPagerAdapter( FragmentManager fm, int behavior,Context context) {
            super(fm, behavior);
            this.context =context;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        private void addFragment(BooksUserFragment2 fragment, String title){
            // add fragment passed as parameter in fragmentList
            fragmentList.add(fragment);
            // add title passed as parameter in fragmentTitleList
            fragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }
//    view history
    private void loadReadingBooks(){
        pdfArrayList=new ArrayList<>();

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("ReadingBooks").limitToLast(10)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfArrayList.clear();
                        for (DataSnapshot ds:snapshot.getChildren()){
                            //we will only get the bookId here and we got other details in adapter using that bookId
                            String bookId=""+ds.child("bookId").getValue();
                            //set id to model
                            ModelPdf modelPdf=new ModelPdf();
                            modelPdf.setId(bookId);
                            //add model to list
                            pdfArrayList.add(modelPdf);
                        }
                        //set LinearLayout Manager
                        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
                        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                        binding.booksRv.setLayoutManager(linearLayoutManager);
                        //setup adapter
                        adapterPdfReadingBooks=new AdapterPdfReadingBooks(viewPagerAdapter.context,pdfArrayList);
                        //set Adapter to recyclerView
                        binding.booksRv.setAdapter(adapterPdfReadingBooks);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
//trending books
private void loadTrendingBooks() {
    //init list
    pdfArrayList = new ArrayList<>();

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
    ref.orderByChild("viewsCount").startAt(10).limitToLast(10) // load 10 most viewed or downloaded books
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //clear list before starting adding data into it
                    pdfArrayList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        //get data
                        ModelPdf model = ds.getValue(ModelPdf.class);
                        //add to list
                        pdfArrayList.add(model);
                    }
                    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
                    linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                    binding.booksRv0.setLayoutManager(linearLayoutManager);
                    //setup adapter
                    adapterPdfUser = new AdapterPdfUser(getContext(), pdfArrayList);
                    //set adapter to recyclerview
                    binding.booksRv0.setAdapter(adapterPdfUser);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
}

        private void loadSlideShow(){
            ArrayList<SlideModel> slideShowList=new ArrayList<>();
            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Books");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        slideShowList.add(new SlideModel(
                                ds.child("url").getValue().toString(),
                                ds.child("title").getValue().toString(),
                                ScaleTypes.valueOf(ds.child("description").getValue().toString())));

                        binding.imageSlider.setImageList(slideShowList,ScaleTypes.FIT);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }






}

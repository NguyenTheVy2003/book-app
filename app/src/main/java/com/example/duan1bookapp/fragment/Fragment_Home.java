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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.duan1bookapp.BooksUserFragment2;
import com.example.duan1bookapp.R;
import com.example.duan1bookapp.activities.AllBooksActivity;

import com.example.duan1bookapp.activities.AllViewHistory;
import com.example.duan1bookapp.adapters.AdapterPdfFavorite;
import com.example.duan1bookapp.adapters.AdapterPdfReadingBooks;
import com.example.duan1bookapp.adapters.AdapterPdfUser;

import com.example.duan1bookapp.adapters.SliderAdapter;
import com.example.duan1bookapp.databinding.FragmentHomeBinding;
import com.example.duan1bookapp.models.ModelCategory;

import com.example.duan1bookapp.models.ModelPdf;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

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

    //slideShow
    private SliderAdapter adapter;
    private ArrayList<ModelPdf> sliderDataArrayList;
    FirebaseFirestore db;







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
        //slide Show
        loadImages();



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
                    adapterPdfReadingBooks.getFilter().filter(s);
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
    private void loadImages() {
        // getting data from our collection and after
        // that calling a method for on success listener.
        sliderDataArrayList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        db.collection("Books").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                // inside the on success method we are running a for loop
                // and we are getting the data from Firebase Firestore
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                    // after we get the data we are passing inside our object class.
                    ModelPdf sliderData = documentSnapshot.toObject(ModelPdf.class);
                    ModelPdf model = new ModelPdf();

                    // below line is use for setting our
                    // image url for our modal class.
                    model.setUrl(sliderData.getUrl());

                    // after that we are adding that
                    // data inside our array list.
                    sliderDataArrayList.add(model);

                    // after adding data to our array list we are passing
                    // that array list inside our adapter class.
                    adapter = new SliderAdapter(getContext(), sliderDataArrayList);

                    // below line is for setting adapter
                    // to our slider view
                    binding.slider.setSliderAdapter(adapter);

                    // below line is for setting animation to our slider.
                    binding.slider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);

                    // below line is for setting auto cycle duration.
                    binding.slider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);

                    // below line is for setting
                    // scroll time animation
                    binding.slider.setScrollTimeInSec(3);

                    // below line is for setting auto
                    // cycle animation to our slider
                    binding.slider.setAutoCycle(true);

                    // below line is use to start
                    // the animation of our slider view.
                    binding.slider.startAutoCycle();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // if we get any error from Firebase we are
                // displaying a toast message for failure
                Toast.makeText(getContext(), "Fail to load slider data..", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


package com.example.duan1bookapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.duan1bookapp.BooksUserFragment;
import com.example.duan1bookapp.R;
import com.example.duan1bookapp.adapters.ViewPagerAdapter;
import com.example.duan1bookapp.databinding.ActivityDashboardUserBinding;
import com.example.duan1bookapp.fragment.Fragment_Home;
import com.example.duan1bookapp.fragment.Fragment_Manga;
import com.example.duan1bookapp.fragment.Fragment_User;
import com.example.duan1bookapp.models.ModelCategory;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardUserActivity extends AppCompatActivity {

    //to show in tabs
    public ArrayList<ModelCategory> categoryArrayList;
    public ViewPagerAdapter viewPagerAdapter;



    //view binding
    private ActivityDashboardUserBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        setUpViewPager();

//        setupViewPagerAdapter(binding.viewPager);
//        binding.tabLayout.setupWithViewPager(binding.viewPager);

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId=item.getItemId();
                switch (itemId){
                    case R.id.ic_home:
                        binding.viewpager.setCurrentItem(0);
                        break;
                    case R.id.ic_manga:
                        binding.viewpager.setCurrentItem(1);
                        break;
                    case R.id.ic_user:
                        binding.viewpager.setCurrentItem(2);
                        break;
                }
                return true;
            }
        });

    }

    private void setUpViewPager(){
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        binding.viewpager.setAdapter(viewPagerAdapter);

        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        binding.bottomNavigation.getMenu().findItem(R.id.ic_home).setChecked(true);
                        break;
                    case 1:
                        binding.bottomNavigation.getMenu().findItem(R.id.ic_manga).setChecked(true);
                        break;
                    case 2:
                        binding.bottomNavigation.getMenu().findItem(R.id.ic_user).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }




//    private void setupViewPagerAdapter(ViewPager viewPager){
//        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, this);
//        categoryArrayList = new ArrayList<>();
//
//        // load categories from firebase
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories"); // be careful of spelling
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange( DataSnapshot snapshot) {
//                // clear before adding to list
//                categoryArrayList.clear();
//
//                ModelCategory modelAll = new ModelCategory("01","All","",1);
//                ModelCategory modelMostViewed = new ModelCategory("02","Most Viewed","",1);
//                ModelCategory modelMostDownloaded = new ModelCategory("03","Most Downloaded","",1);
//                // all models to list
//                categoryArrayList.add(modelAll);
//                categoryArrayList.add(modelMostViewed);
//                categoryArrayList.add(modelMostDownloaded);
//                // add data to View pager adapter
//                viewPagerAdapter.addFragment(BooksUserFragment.newInstance(
//                        "" + modelAll.getId(),
//                        "" + modelAll.getCategory(),
//                        "" + modelAll.getUid()
//                ), modelAll.getCategory());
//
//                viewPagerAdapter.addFragment(BooksUserFragment.newInstance(
//                        "" + modelMostViewed.getId(),
//                        "" + modelMostViewed.getCategory(),
//                        "" + modelMostViewed.getUid()
//                ), modelMostViewed.getCategory());
//
//                viewPagerAdapter.addFragment(BooksUserFragment.newInstance(
//                        "" + modelMostDownloaded.getId(),
//                        "" + modelMostDownloaded.getCategory(),
//                        "" + modelMostDownloaded.getUid()
//                ), modelMostDownloaded.getCategory());
//                // refresh list from firebase
//                viewPagerAdapter.notifyDataSetChanged();
//
//                // Now Load from firebase
//                for (DataSnapshot ds: snapshot.getChildren()) {
//                    // get data
//                    ModelCategory model = ds.getValue(ModelCategory.class);
//                    // add data to list
//                    categoryArrayList.add(model);
//                    // add data to viewPagerAdapter
//                    viewPagerAdapter.addFragment(BooksUserFragment.newInstance(
//                            "" + model.getId(),
//                            "" + model.getCategory(),
//                            "" + model.getUid()), model.getCategory());
//                    // refresh list
//                    viewPagerAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//
//            }
//        });
//
//        // set adapter to view pager
//        viewPager.setAdapter(viewPagerAdapter);
//    }
//
//    public class ViewPagerAdapter extends FragmentPagerAdapter{
//
//        private ArrayList<BooksUserFragment> fragmentList = new ArrayList<>();
//        private ArrayList<String> fragmentTitleList = new ArrayList<>();
//        private Context context;
//
//        public ViewPagerAdapter( FragmentManager fm, int behavior, Context context) {
//            super(fm, behavior);
//            this.context = context;
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return fragmentList.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return fragmentList.size();
//        }
//
//        private void addFragment(BooksUserFragment fragment,String title){
//            // add fragment passed as parameter in fragmentList
//            fragmentList.add(fragment);
//            // add title passed as parameter in fragmentTitleList
//            fragmentTitleList.add(title);
//        }
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return fragmentTitleList.get(position);
//        }
//    }

    private void checkUser() {
        //get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            //not logged in, go to main screen
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }



}